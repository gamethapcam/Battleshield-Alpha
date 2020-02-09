package com.yaamani.battleshield.alpha.desktop;

/*import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;*/
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.yaamani.battleshield.alpha.Game.BattleshieldGame;

public class DesktopLauncher {
	public static void main (String[] arg) {


		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 550;
		config.width = (int) (config.height * (3f/2f));
		config.useHDPI = true;
		//config.samples = 16;
		//LwjglApplicationConfiguration.disableAudio = true;
		//config.vSyncEnabled = false;

		new LwjglApplication(new BattleshieldGame(), config);


		/*Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setHdpiMode(HdpiMode.Pixels);

		int height = 550;
		config.setWindowedMode((int) (height * (3f/2f)), height);

		new Lwjgl3Application(new BattleshieldGame(), config);*/
    }
}