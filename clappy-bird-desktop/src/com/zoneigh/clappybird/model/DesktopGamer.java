package com.zoneigh.clappybird.model;

public class DesktopGamer implements IGamer {
	private int bestScore = 0;
	
	@Override
	public String getGamerId() {
		// TODO Auto-genreated method stub
		return null;
	}

	@Override
	public int getBestScore() {
		// TODO Auto-generated method stub
		return bestScore;
	}

	@Override
	public void recordNewScore(int score) {
		// TODO Auto-generated method stub
		if (score > bestScore) {
			bestScore = score;
		}
	}

}
