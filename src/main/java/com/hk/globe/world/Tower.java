package com.hk.globe.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.hk.game.G2D;
import com.hk.globe.Screen;
import com.hk.globe.screen.GameScreen;
import com.hk.math.MathUtil;
import com.hk.math.vector.Vector2F;

public class Tower
{
	private static final float healthScale = 50F;
	public final Globe globe;
	public final boolean friendly;
	public final Vector2F pos;
	public final float maxHealth;
	private float actualHealth, health;
	private final BufferedImage towerImage, deadTowerImage;
	public final Rectangle2D bounds;
	
	public Tower(Globe globe, boolean friendly)
	{
		this.friendly = friendly;
		this.globe = globe;
		pos = new Vector2F(globe.pos);
		maxHealth = actualHealth = health = globe.radius * (0.9F * healthScale);
		towerImage = new BufferedImage(40, (int) maxHealth, BufferedImage.TYPE_INT_ARGB);
		deadTowerImage = new BufferedImage(40, (int) maxHealth, BufferedImage.TYPE_INT_ARGB);
		setupTowerImage();
		bounds = new Rectangle2D.Float(pos.x - 20, pos.y - maxHealth / healthScale, 40, maxHealth / healthScale);
	}
	
	public void updateTower()
	{
		health = health * 0.9F + actualHealth * 0.1F;
	}
	
	public void paintTower(G2D g2d)
	{
		g2d.drawImage(deadTowerImage, pos.x - 20, pos.y - maxHealth / healthScale);
		g2d.enable(G2D.G_FILL);
		g2d.setColor(Color.ORANGE);
		g2d.drawRectangle(pos.x - 20, pos.y - health / healthScale, 40, health / healthScale);
		g2d.disable(G2D.G_FILL);
		if((int) (actualHealth / healthScale) > 0)
		{
			Image sub = towerImage.getSubimage(0, (int) ((maxHealth - actualHealth) / healthScale), 40, (int) (actualHealth / healthScale));
			g2d.drawImage(sub, pos.x - 20, pos.y - actualHealth / healthScale);
		}
	}
	
	public void setHealth(float health)
	{
		this.actualHealth = MathUtil.between(0, health, maxHealth);
	}
	
	public void addHealth(float amt)
	{
		setHealth(actualHealth + (amt > 0 ? amt : 0));
	}
	
	public void removeHealth(float amt)
	{
		if(globe.friendly)
		{
			GameScreen screen = globe.interpreter.getExtra("gameScreen", GameScreen.class);
			if (screen != null)
				screen.shake((int) (amt / maxHealth * 300F));
		}

		setHealth(actualHealth - (amt > 0 ? amt : 0));
	}
	
	public float getHealth()
	{
		return actualHealth;
	}
	
	private void setupTowerImage()
	{
		BufferedImage img = friendly ? friendTower : enemyTower;
		int ht = (int) (maxHealth / healthScale);

		Graphics g = towerImage.getGraphics();
		int i = 0;
		while(ht >= 10)
		{
			ht -= 10;
			g.drawImage(img, 0, i * 10, null);
			i++;
		}
		
		if(ht > 0)
		{
			g.drawImage(img.getSubimage(0, 0, 40, ht), 0, i * 10, null);
		}
		ht = (int) (maxHealth / healthScale);

		g = deadTowerImage.getGraphics();
		i = 0;
		while(ht >= 10)
		{
			ht -= 10;
			g.drawImage(deadTower, 0, i * 10, null);
			i++;
		}
		if(ht > 0)
		{
			g.drawImage(deadTower.getSubimage(0, 0, 40, ht), 0, i * 10, null);
		}
	}
	
	private static final BufferedImage friendTower = Screen.getImage("/images/friendly_tower.png");
	private static final BufferedImage enemyTower = Screen.getImage("/images/enemy_tower.png");
	private static final BufferedImage deadTower = Screen.getImage("/images/dead_tower.png");
}
