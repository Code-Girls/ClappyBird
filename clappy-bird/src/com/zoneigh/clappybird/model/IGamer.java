package com.zoneigh.clappybird.model;

public interface IGamer {

	public String getGamerId();
	public int getBestScore();
	public void recordNewScore(int score);
}
