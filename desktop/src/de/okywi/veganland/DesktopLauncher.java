package de.okywi.veganland;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import de.okywi.veganland.VeganLand;

import static de.okywi.veganland.VeganLand.SCREEN_SIZE;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setTitle("VeganLand");
		//config.setWindowedMode(SCREEN_SIZE[0], SCREEN_SIZE[1]);
		new Lwjgl3Application(new VeganLand(), config);

	}
}
