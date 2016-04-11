package com.zoneigh.clappybird.model;

public interface IClapListener {

	public boolean hasClap();
	public void sendClap();
	public void acknowledgeClap();
	public boolean isManualAllowed();
	
	public void startListening();
	public void stopListening();
}
