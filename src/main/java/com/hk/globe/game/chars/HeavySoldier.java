package com.hk.globe.game.chars;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.hk.game.G2D;
import com.hk.globe.Screen;
import com.hk.globe.game.chars.SoldierData.SoldierStats;
import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;

@SoldierStats(name = "Knight", lifeCost = 3, woodCost = 200, attackRate = 1000 / 75, damage = 45, health = 300, movementSpeed = 5, icon = "/images/soldiers/heavy_soldier/icon.png")
public class HeavySoldier extends WalkingSoldier
{
	public HeavySoldier(GameScreen scr, Globe onGlobe)
	{
		super(scr, onGlobe);
		// The soldier's health is 300
		maxHealth = actualHealth = health = 300;

		movementSpeed = 0.05F;
		soldierDamage = 45;
		towerDamage = 50;
		soldierHitCooldown = 75;
		towerHitCooldown = 20;
	}

	@Override
	public void paintSoldier(G2D g2d, boolean held)
	{
		g2d.drawImage(onGlobe.friendly ? friendlySoldier : enemySoldier, pos.x - 11, pos.y - 28);
		if(!held)
		{
			g2d.enable(G2D.G_FILL);
			g2d.setColor(Color.BLACK);
			g2d.drawRectangle(pos.x - 12, pos.y - 3, 20, 6);
			g2d.setColor(Color.RED);
			g2d.drawRectangle(pos.x - 11, pos.y - 2, 18, 4);
			g2d.setColor(Color.ORANGE);
			g2d.drawRectangle(pos.x - 11, pos.y - 2, 18 * (health / maxHealth), 4);
			g2d.setColor(Color.GREEN);
			g2d.drawRectangle(pos.x - 11, pos.y - 2, 18 * (actualHealth / maxHealth), 4);
			g2d.disable(G2D.G_FILL);
		}
	}
	
	public static final BufferedImage friendlySoldier = Screen.getImage("/images/soldiers/heavy_soldier/friendly.png");
	public static final BufferedImage enemySoldier = Screen.getImage("/images/soldiers/heavy_soldier/enemy.png");
}
