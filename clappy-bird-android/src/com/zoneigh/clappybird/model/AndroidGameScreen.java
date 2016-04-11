package com.zoneigh.clappybird.model;

import com.zoneigh.clappybird.model.IGameScreen;

public class AndroidGameScreen implements IGameScreen {

	private int width;
	private int height;
	
	public AndroidGameScreen (int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}
