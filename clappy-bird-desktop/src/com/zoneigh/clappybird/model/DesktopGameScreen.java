package com.zoneigh.clappybird.model;

public class DesktopGameScreen implements IGameScreen {
	
	private int width;
	private int height;
	
	public DesktopGameScreen (int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}
}
