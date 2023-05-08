package com.hk.globe;

import com.hk.game.G2D;
import com.hk.game.Handler;
import com.hk.globe.world.Tower;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;

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
	
	public abstract void updateScreen(double delta);
	
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
			return ImageIO.read(Objects.requireNonNull(Tower.class.getResource(path)));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
