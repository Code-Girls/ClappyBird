package com.zoneigh.clappybird.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class AndroidGamer implements IGamer {
	
	private static String GAMER_ID = "GAMER_ID";
	private static String GAMER_BEST_SCORE = "GAMER_BEST_SCORE";
	
	private Context context;
	private String gamerId;
	private int bestScore;
	
	public AndroidGamer (Context context) {
		this.context = context; // TODO is passing context an okay thing to do?
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		this.bestScore = sharedPreferences.getInt(GAMER_BEST_SCORE, 0);
		this.gamerId = sharedPreferences.getString(GAMER_ID, null);
		
		// Create Random GamerId & Store it
		if (this.gamerId == null) {
			this.gamerId = AndroidGamer.generateNewGamerId();
			sharedPreferences.edit().putString(GAMER_ID, this.gamerId).commit();
		}
		
		//new RecordGamerTask().execute("http://api.zoneigh.com/start?gamerId=" + this.gamerId);
	}
	
	@Override
	public String getGamerId() {
		return this.gamerId;
	}

	@Override
	public int getBestScore() {		
		return this.bestScore;
	}

	@Override
	public void recordNewScore(int score) {
		if (score > this.bestScore) {
			
			PreferenceManager.getDefaultSharedPreferences(this.context)
			                 .edit()
			                 .putInt(GAMER_BEST_SCORE, score)
			                 .commit();
			
			this.bestScore = score;
		}

		// TODO track previous scores?
	}
	
	public static String generateNewGamerId() {
	    Random generator = new Random();
	    StringBuilder randomStringBuilder = new StringBuilder();
	    int randomLength = generator.nextInt(32);
	    char tempChar;
	    for (int i = 0; i < randomLength; i++){
	        tempChar = (char) (generator.nextInt(96) + 32);
	        randomStringBuilder.append(tempChar);
	    }
	    return randomStringBuilder.toString();
	}
	
	private class RecordGamerTask extends AsyncTask<String, String, String> {
	    @Override
	    protected String doInBackground(String... uri) {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(uri[0]));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            	Log.i("TESTTT", "SUCCESS:" + responseString + "(" + uri[0] + ")");
	            } else{
	            	Log.i("TESTTT", "SUCCESS:" + "FAILED");

	                //Closes the connection.
	                response.getEntity().getContent().close();
	                //throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        } catch (IOException e) {
	            //TODO Handle problems..
	        }
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	 }
}
