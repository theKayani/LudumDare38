package com.hk.globe.screen;

import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.hk.game.G2D;
import com.hk.globe.LDGame;
import com.hk.globe.Screen;
import com.hk.lua.Lua;
import com.hk.lua.LuaInterpreter;

public class SplashScreen extends Screen
{
	private final LuaInterpreter interpreter;
	private final BufferedImage img;
	private double delay = 0;

	public SplashScreen(LDGame game)
	{
		super(game);

		interpreter = Lua.interpreter();
		Lua.importStandard(interpreter);

		img = getImg();
	}

	@Override
	public void updateScreen(double delta)
	{
		delay += delta;
		if(delay > 1)
			game.setScreen(new MenuScreen(game, interpreter));
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
			return ImageIO.read(Objects.requireNonNull(SplashScreen.class.getResource("/images/Splashscreen.png")));
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}