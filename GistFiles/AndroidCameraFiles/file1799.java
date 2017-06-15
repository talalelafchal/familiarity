package com.ld37.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ld37.game.StartPoint;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 650;
		config.height = config.width / 12 * 9;
		config.resizable = false;
		config.useGL30 = true;
		config.title = "Ludum Dare 37 (One Room)";
		config.foregroundFPS = 60;
		new LwjglApplication(new StartPoint(), config);
	}
}
