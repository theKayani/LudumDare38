package com.hk.globe.screen;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.hk.globe.LDGame;
import com.hk.globe.Screen;

import main.G2D;

public class SplashScreen extends Screen
{
	private long start = -1;
	private final BufferedImage img;
	
	public SplashScreen(LDGame game)
	{
		super(game);
		img = getImg();
	}

	@Override
	public void updateScreen(int ticks)
	{
		long time = System.currentTimeMillis();
		if(start == -1)
		{
			start = time;
		}
		else if(time - start > 200)
		{
			game.setScreen(new MenuScreen(game));
		}
	}

	@Override
	public void paintScreen(G2D g2d)
	{
		g2d.g2d.drawImage(img, 0, 0, null);
	}
	
	private BufferedImage getImg()
	{
		try
		{
			return ImageIO.read(SplashScreen.class.getResource("/images/Splashscreen.png"));
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}