package com.zoneigh.clappybird.model;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class AndroidClapListener implements IClapListener {
	
	final private int MIN_CLAP_INTERVAL_MS = 75;

	private volatile long lastClapTimestamp;
	private volatile boolean clapped;
	private Thread listeningThread;
	
	public AndroidClapListener() {
		this.lastClapTimestamp = 0;
		this.clapped = false;
	}
	
	public boolean isManualAllowed() {
		return false;
	}
	
	public void sendClap() {
		long currentTimestamp = System.currentTimeMillis();
		
		if (lastClapTimestamp + MIN_CLAP_INTERVAL_MS < currentTimestamp) {			
			this.clapped = true;
			this.lastClapTimestamp = currentTimestamp;
		} else {
			this.clapped = false;
		}
	}
	
	public boolean hasClap() {	
		return this.clapped;
	}

	public void acknowledgeClap() {
		this.clapped = false;
	}
	
	public void startListening() {
		Log.d("ClapDetector", "Start was called");
		listeningThread = new Thread(new ClapListenerRunnable(this));
		listeningThread.start();
		Thread.yield();
	}
	
	public void stopListening() {
		if (listeningThread != null) {
			Log.d("ClapDetector", "stop listening() called");
			listeningThread.interrupt();
			listeningThread = null;
		}
	}
	
	private class ClapListenerRunnable implements Runnable {
				
		private static final int sampleDurationMs = 10;
		
		private IClapListener clapListener;
		private AudioRecord audioRecord;
		private int sampleRate;
		private int bufferSize;
		private short[] audioData;
		
		// Placeholder for frequency and magnitude (matching-index)
		private int[] frequencyData;
		private double[] magnitudeData; 
		
		public ClapListenerRunnable (IClapListener clapListener) {
			this.clapListener = clapListener;
		}

		@Override
		public void run() {
			
			this.setupAudioRecord(); // Instantiates audioRecord, sampleRate, bufferSize, audioData, frequencyData, magnitudeData
			
			if (audioRecord == null) {
				Log.d("ClapDetector", "AudioRecord not instantiated properly");
				return;
			}
						
        	audioRecord.startRecording();
			while (!Thread.currentThread().isInterrupted()) {
	        	try { Thread.sleep(sampleDurationMs); } catch (InterruptedException e) { break; }
				
				// Update MagnitudeData & FrequencyData
	        	this.readAudioRecord();
	        	if (this.heardClap()) {
	        		this.clapListener.sendClap();
	        	}
			}
        	audioRecord.stop(); // Must be called after read() operation
			
			this.teardownAudioRecord();
			Log.d("ClapDetector", "Thread while loop eneded");
		}

		// Reads audio record and injects into Audio/Frequency/Magnitude array raw data
		private void readAudioRecord () {
			
			int numshorts = audioRecord.read(audioData,0,audioData.length);
            
            if ((numshorts == AudioRecord.ERROR_INVALID_OPERATION) || 
                    (numshorts == AudioRecord.ERROR_BAD_VALUE)) {
            	return;
            }
                        	
            // Successfull Read
            double[] preRealData = new double[bufferSize];
            double PI = 3.14159265359;
            for (int i = 0; i < bufferSize; i++) {
                double multiplier = 0.5 * (1 - Math.cos(2*PI*i/(bufferSize-1)));
                preRealData[i] = multiplier * audioData[i];
            }

            DoubleFFT_1D fft = new DoubleFFT_1D(bufferSize);
            double[] realData = new double[bufferSize * 2];

            for (int i=0;i<bufferSize;i++) {
                realData[2*i] = preRealData[i];
                realData[2*i+1] = 0;    
            }
            fft.complexForward(realData);

            double magnitude[] = new double[bufferSize / 2];

            for (int i = 0; i < magnitude.length; i++) {
                double R = realData[2 * i];
                double I = realData[2 * i + 1];

                magnitude[i] = Math.sqrt(I*I + R*R);
                
                // Inject Frequency/Magnitude Data
                this.frequencyData[i] = sampleRate * i / bufferSize;
                this.magnitudeData[i] = magnitude[i];
            }
		}
		
		// Determine whether current audio data we have is a clap
		// int[] frequencyData, double[] magnitudeData is pre-populated with raw data by readAudioRecord()
		private boolean heardClap () {
			
			int[] arr = new int[100]; // TODO this really shoulnd't be hardcoded to certain length
			for (int i=0; i<arr.length; i++) {
				arr[i] = 0;
			}
			
			for (int i=0; i<this.magnitudeData.length; i++) {
				
				int frequency = this.frequencyData[i];
				double magnitude = this.magnitudeData[i];
				int index = (frequency / 100);
				
				if (magnitude > 10000) {
					arr[index]++;
				}
				if (magnitude > 20000) {
					arr[index] += 2;
				}
				if (magnitude > 50000) {
					arr[index] += 5;
				}
			}
			
			int pointAboveThreshold = 0;
			for (int i=5; i<25; i++) {
				
				if (arr[i] > 5) {
					pointAboveThreshold++;
				}
			}
			
			if (pointAboveThreshold > 17) {
				return true;
			} else {
				return false;
			}
			
			/*
			// Print Equalizer-like string
			String str = "";
			for (int val : arr) {
				
				if (val == 0) str += " ";
				else if (val > 5) str += ".";
				else if (val > 10) str += "o";
				else if (val > 15) str += "0";
			}
			
			Log.d("Spectrum", str);
			*/			
		}
		
		public void setupAudioRecord() {
			
			Log.i("ClapDetector", "Setting up audioRecord");

			int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100}; // TODO Try high sample rate first?
		    for (int rate : mSampleRates) {
		        for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT }) { // TODO 8bit works?
		            for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) { // TODO stereo works?
		                try {
		                    Log.d("ClapDetector", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
		                    int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

		                    if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
		                        // check if we can instantiate and have a success
		                        AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

		                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
		                        	Log.d("ClapDetector", "Successfully initialized audioRecord");
		                        	
		                        	this.audioRecord = recorder;
		                        	this.sampleRate = rate;
		                        	this.bufferSize = bufferSize;
		        		        	this.audioData = new short[bufferSize];
		        		        	this.frequencyData = new int[bufferSize / 2];
		        		        	this.magnitudeData = new double[bufferSize / 2];
		                        	return;
		                        }
		                    }
		                } catch (Exception e) {
		                    Log.e("ClapDetector", rate + "Exception, keep trying.",e);
		                }
		            }
		        }
		    }
		}
		
		public void teardownAudioRecord() {
			if (null != this.audioRecord) {
				Log.i("ClapDetector", "Tearing down audioRecord");
				this.audioRecord.release();
			}
		}
	}
}
