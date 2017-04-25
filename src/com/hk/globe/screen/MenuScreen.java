package com.hk.globe.screen;

import java.awt.Color;
import java.awt.Rectangle;

import com.hk.globe.LDGame;
import com.hk.globe.Screen;
import com.hk.globe.world.Cloud;
import com.hk.globe.world.Globe;
import com.hk.math.Rand;
import com.hk.math.vector.Color3F;

import main.G2D;
import main.Main;

public class MenuScreen extends Screen
{
	private final Color3F clr = new Color3F(0.8F, 1.0F, 1.0F).subtractLocal(0.05F);
	private final Cloud[] clouds;
	private final Rectangle[] buttons;
	private final Globe globeA, globeB;
	private final GameScreen startScreen;

	public MenuScreen(LDGame game)
	{
		super(game);
		
		clouds = new Cloud[50];
		for(int i = 0; i < clouds.length; i++)
		{
			clouds[i] = new Cloud(Rand.nextFloat(Main.WIDTH), Rand.nextFloat(Main.HEIGHT));
		}
		
		buttons = new Rectangle[2];
		Rectangle startButton = new Rectangle(400, 200);
		startButton.x = Main.WIDTH / 2;
		startButton.y = Main.HEIGHT * 2 / 5;
		buttons[0] = startButton;
		
		Rectangle exitButton = new Rectangle(400, 200);
		exitButton.x = Main.WIDTH / 2;
		exitButton.y = Main.HEIGHT * 4 / 5;
		buttons[1] = exitButton;
		
		for(Rectangle r : buttons)
		{
			r.x -= r.width / 2;
			r.y -= r.height / 2;
		}
		
		startScreen = new GameScreen(game);
		
		globeA = new Globe(null, true, Main.WIDTH * 4 / 5, Main.HEIGHT / 2, 200);
		globeB = new Globe(null, false, Main.WIDTH * 1 / 5, Main.HEIGHT / 2, 200);
		for(int i = 0; i < 3; i++)
		{
			globeA.spawnRandomSoldier();
			globeB.spawnRandomSoldier();
		}
	}

	@Override
	public void updateScreen(int ticks)
	{	
		for(int i = 0; i < clouds.length; i++)
		{
			Cloud cloud = clouds[i];
			cloud.updateCloud(ticks);
			
			if(cloud.pos.x > Main.WIDTH)
			{
				clouds[i] = new Cloud(-75, Rand.nextFloat(Main.HEIGHT));
			}
		}
	}

	@Override
	public void paintScreen(G2D g2d)
	{
		g2d.setColor(clr.r, clr.g, clr.b);
		g2d.enable(G2D.G_FILL);
		g2d.drawRectangle(0, 0, g2d.width, g2d.height);
		g2d.disable(G2D.G_FILL);
		
		for(Cloud cloud : clouds)
		{
			cloud.paintCloud(g2d);
		}
		
		g2d.setColor(Color.CYAN);
		g2d.setFontSize(32F);
		for(int i = 0; i < buttons.length;  i++)
		{
			Rectangle r = buttons[i];
			g2d.enable(G2D.G_FILL);
			g2d.setColor(r.contains(handler.mouseX(), handler.mouseY()) ? new Color(160, 199, 224) : new Color(140, 179, 204));
			g2d.drawRectangle(r.x, r.y, r.width, r.height);
			g2d.disable(G2D.G_FILL);
			g2d.setColor(Color.BLACK);
			g2d.drawRectangle(r.x, r.y, r.width, r.height);
			g2d.enable(G2D.G_CENTER);
			g2d.drawString(getString(i), r.getCenterX(), r.getCenterY());
			g2d.disable(G2D.G_CENTER);
		}
		
		g2d.setFontSize(72F);
		g2d.enable(G2D.G_CENTER);
		g2d.setColor(Color.BLACK);
		g2d.drawString("Snowglobe", g2d.width / 2 + 1, g2d.height * 1 / 6);
		g2d.drawString("Snowglobe", g2d.width / 2 - 1, g2d.height * 1 / 6);
		g2d.drawString("Snowglobe", g2d.width / 2, g2d.height * 1 / 6 + 1);
		g2d.drawString("Snowglobe", g2d.width / 2, g2d.height * 1 / 6 - 1);
		g2d.setColor(Color.CYAN);
		g2d.drawString("Snowglobe", g2d.width / 2, g2d.height * 1 / 6);
		g2d.disable(G2D.G_CENTER);
		g2d.setFontSize(24F);
		g2d.setColor(Color.BLACK);
		g2d.drawString("Press 'M' anywhere to toggle the music", 5, g2d.height - 5);
		
		globeA.paintGlobe(g2d);
		globeB.paintGlobe(g2d);
		
		globeA.paintSoldiers(g2d);
		globeB.paintSoldiers(g2d);
	}
	
	private String getString(int button)
	{
		if(button == 0)
		{
			return "Start";
		}
		else if(button == 1)
		{
			return "Exit";
		}
		throw new IllegalArgumentException(button + " isn't a button id");
	}
	
	private void buttonPressed(int i)
	{
		if(i == 0)
		{
			game.setScreen(startScreen);
		}
		else if(i == 1)
		{
			System.exit(0);
		}
	}
	
	public void mouse(float x, float y, boolean pressed, int button)
	{
		if(!pressed)
		{
			for(int i = 0; i < buttons.length;  i++)
			{
				Rectangle r = buttons[i];
				
				if(r.contains(x, y))
				{
					buttonPressed(i);
					return;
				}
			}
		}
	}
}
