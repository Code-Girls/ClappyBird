package com.zoneigh.clappybird;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.zoneigh.clappybird.model.DesktopClapListener;
import com.zoneigh.clappybird.model.DesktopGamer;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "clappy-bird";
		cfg.useGL20 = false;
		cfg.width = 640;
		cfg.height = 960;
		
		DesktopGamer desktopGamer = new DesktopGamer();
		DesktopClapListener desktopClapListener = new DesktopClapListener(50);
		
		new LwjglApplication(new ClappyBird(desktopGamer, desktopClapListener), cfg);
	}
}
