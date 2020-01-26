package com.yaamani.battleshield.alpha.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.yaamani.battleshield.alpha.Game.BattleshieldGame;

public class DesktopLauncher {
	public static void main (String[] arg) {

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 550;
        config.width = (int) (config.height * (3f/2f));
        config.useHDPI = true;
        //config.samples = 16;

		new LwjglApplication(new BattleshieldGame(), config);
    }
}