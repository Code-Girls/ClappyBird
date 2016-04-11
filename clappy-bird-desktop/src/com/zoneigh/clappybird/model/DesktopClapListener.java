package com.zoneigh.clappybird.model;

import com.badlogic.gdx.Gdx;

public class DesktopClapListener implements IClapListener {

	private long lastClapTimestamp;
	private long minClapIntervalTimestamp; // milliseconds
	private boolean clapped;
	
	public DesktopClapListener(int minClapIntervalTimestamp) {
		this.lastClapTimestamp = 0;
		this.minClapIntervalTimestamp = minClapIntervalTimestamp;
		this.clapped = false;
	}
	
	public boolean isManualAllowed() {
		return true;
	}
	
	public void sendClap() {
		long currentTimestamp = System.currentTimeMillis();
		
		if (lastClapTimestamp + minClapIntervalTimestamp < currentTimestamp) {			
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

	@Override
	public void startListening() {
		// TODO Auto-generated method stub
	}

	@Override
	public void stopListening() {
		// TODO Auto-generated method stub
	}
}
