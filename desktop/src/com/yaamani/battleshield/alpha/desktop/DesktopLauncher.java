package com.yaamani.battleshield.alpha.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.yaamani.battleshield.alpha.Game.BattleshieldGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 720;
        config.width = (int) (config.height * (3f/2f));
        config.useHDPI = true;

		new LwjglApplication(new BattleshieldGame(), config);
    }
}
