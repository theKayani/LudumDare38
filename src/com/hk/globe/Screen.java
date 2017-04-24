package com.hk.globe;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import com.hk.globe.world.Tower;

import main.G2D;
import main.Handler;

public abstract class Screen
{
	/**
	 * Formats thousands numbers with the commas.
	 */
	public static final DecimalFormat fm = new DecimalFormat("#,###");
	
	public final LDGame game;
	public final Handler handler;
	
	public Screen(LDGame game)
	{
		this.game = game;
		this.handler = game.getHandler();
	}
	
	public void initialize()
	{}
	
	public abstract void updateScreen(int ticks);
	
	public abstract void paintScreen(G2D g2d);

	public void mouse(float x, float y, boolean pressed, int button)
	{}

	public void mouseMoved(float x, float y)
	{}

	public void mouseWheel(int amt)
	{}

	public void key(int key, char keyChar, boolean pressed)
	{}

	public static BufferedImage getImage(String path)
	{
		try
		{
			return ImageIO.read(Tower.class.getResource(path));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
