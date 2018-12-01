package com.yaamani.battleshield.alpha.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.yaamani.battleshield.alpha.Game.BattleshieldGame;

public class DesktopLauncher {
	public static void main (String[] arg) {

		//packTextures();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 720;
        config.width = (int) (config.height * (3f/2f));
        config.useHDPI = true;

		new LwjglApplication(new BattleshieldGame(), config);
    }

    private static void packTextures() {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 4096;
        settings.maxHeight = 4096;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.flattenPaths = true;
        settings.combineSubdirectories = true;
        //settings.fast = true;

        TexturePacker.process(settings,
                "/Users/mac/OneDrive/Battleshield(Alpha)/Unpacked Assets/1080p",
                "/Users/mac/OneDrive/Battleshield(Alpha)/android/assets",
                "All");
    }
}