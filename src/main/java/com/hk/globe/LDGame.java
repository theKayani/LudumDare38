package com.hk.globe;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.hk.game.G2D;
import com.hk.game.Game;
import com.hk.game.GameSettings;
import com.hk.game.Main;
import com.hk.globe.screen.SplashScreen;

/**
 * This is the basis of the Java Swing game.
 * The game base thing, I made my self. If
 * the source of that is needed, let me know.
 * It's pretty simple anyways.
 * 
 * @author Wati888
 */
public class LDGame extends Game
{
	/**
	 * The current screen being rendered.
	 */
	private Screen currScreen;
	private boolean musicOn = false;
	private final Clip themeSong;
	public final Path resourcePath;

	public LDGame(Path resourcePath)
	{
		this.resourcePath = resourcePath;
		setScreen(new SplashScreen(this));
		
		themeSong = getThemeSong();
		setMusicOn(true);
	}

	/**
	 * Runs every tick.
	 * The parameter is the current frame.
	 */
	@Override
	public void update(double delta)
	{
		currScreen.updateScreen(delta);
	}

	/**
	 * Paint the current screen to the graphics wrapper object.
	 */
	@Override
	public void paint(G2D g2d)
	{
		currScreen.paintScreen(g2d);
	}
	
	public void setMusicOn(boolean musicOn)
	{
		if(this.musicOn != musicOn)
		{
			this.musicOn = musicOn;
		
			if(musicOn)
				themeSong.start();
			else
				themeSong.stop();
		}
	}
	
	private Clip getThemeSong()
	{
		try
		{
			AudioInputStream ais = AudioSystem.getAudioInputStream(Objects.requireNonNull(LDGame.class.getResource("/audio/theme_song.wav")));
			Clip c = AudioSystem.getClip();
			c.open(ais);
			FloatControl fc = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			fc.setValue(-20F);
			c.loop(Clip.LOOP_CONTINUOUSLY);
			
			return c;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
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
		if(key == KeyEvent.VK_M)
		{
			if(!pressed)
			{
				setMusicOn(!musicOn);
			}
		}
		else
		{
			currScreen.key(key, keyChar, pressed);
		}
	}

	public Reader luaFile(String path)
	{
		try
		{
			return Files.newBufferedReader(resourcePath.resolve("lua/" + path));
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
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
		Path path;
		if(args.length == 0)
			path = Paths.get("C:\\Users\\kayan\\Desktop\\Work\\Snowglobe\\src\\main\\resources");
		else
			path = Paths.get(args[0]);
		Path resourcePath = path;
		EventQueue.invokeLater(() -> {
			LDGame game = new LDGame(resourcePath);
			GameSettings settings = new GameSettings();

			// The name of the game is called:
			settings.title = "Snowglobe";
			settings.version = "1.0";
			settings.quality = GameSettings.Quality.POOR;
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
		});
	}
}
