package com.hk.globe;

import java.awt.Color;
import java.awt.EventQueue;

import com.hk.globe.screen.SplashScreen;

import main.G2D;
import main.Game;
import main.GameSettings;
import main.GameSettings.Quality;
import main.Main;

/**
 * This is the basis of the Java Swing game.
 * The game base thing, I made my self. If
 * the source of that is needed, let me know.
 * It's pretty simple anyways.
 * 
 * @author Wati999
 */
public class LDGame extends Game
{
	/**
	 * Global gravity value.
	 * 9.81 (m/s) / 60 (fps)
	 */
	public static final float GRAVITY = 0.1635F;
	/**
	 * The current screen being rendered.
	 */
	private Screen currScreen;
	
	public LDGame()
	{
		// Set the current screen to the splash screen.
		setScreen(new SplashScreen(this));
//		setScreen(new GameScreen(this));
	}

	/**
	 * Runs every tick.
	 * The parameter is the current frame.
	 */
	@Override
	public void update(int ticks)
	{
		currScreen.updateScreen(ticks);
	}

	/**
	 * Paint the current screen to the graphics wrapper object.
	 */
	@Override
	public void paint(G2D g2d)
	{
		currScreen.paintScreen(g2d);
	}
	
	/**
	 * Sets the current screen to the given one.
	 * Also initializes it.
	 */
	public void setScreen(Screen screen)
	{
		this.currScreen = screen;
		currScreen.initialize();
	}

	public void mouse(float x, float y, boolean pressed, int button)
	{
		currScreen.mouse(x, y, pressed, button);
	}

	public void mouseMoved(float x, float y)
	{
		currScreen.mouseMoved(x, y);
	}

	public void mouseWheel(int amt)
	{
		currScreen.mouseWheel(amt);
	}

	public void key(int key, char keyChar, boolean pressed)
	{
		currScreen.key(key, keyChar, pressed);
	}
	
	/**
	 * Beginning of the code.
	 */
	public static void main(String[] args)
	{
		// Set the native resolution of the window
		// to 1600x900
		System.setProperty("Main.WIDTH", "1600");
		System.setProperty("Main.HEIGHT", "900");
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				LDGame game = new LDGame();
				GameSettings settings = new GameSettings();
		
				// The name of the game is called:
				settings.title = "Snowglobe";
				settings.version = "1.0";
				settings.quality = Quality.POOR;
				// Setting the width and height of the window.
				settings.width = 1600;
				settings.height = 900;
				// Showing the fps in the title.
				settings.showFPS = true;
				settings.background = Color.BLACK;
				settings.maxFPS = 60;
//				settings.maxFPS = -1;		
		
				// Initializing a new game with the given
				// game object and settings.
				Main.initialize(game, settings);
			}
		});
	}
}
