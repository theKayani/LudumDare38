package com.hk.globe.game.chars;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.hk.game.G2D;
import com.hk.globe.Screen;
import com.hk.globe.game.chars.SoldierData.SoldierStats;
import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;

@SoldierStats(name = "Horseman", lifeCost = 4, woodCost = 100, attackRate = 1000 / 60, damage = 20, health = 150, movementSpeed = 20, icon = "/images/soldiers/horse_soldier/icon.png")
public class HorseSoldier extends WalkingSoldier
{
	public HorseSoldier(GameScreen scr, Globe onGlobe)
	{
		super(scr, onGlobe);
		// The soldier's health is 300
		maxHealth = actualHealth = health = 150;

		movementSpeed = 0.2F;
		soldierDamage = 20;
		towerDamage = 25;
		soldierHitCooldown = 60;
		towerHitCooldown = 25;
	}

	@Override
	public void paintSoldier(G2D g2d, boolean held)
	{
		g2d.drawImage(onGlobe.friendly ? (walkingDir == -1 ? friendlySoldierL : friendlySoldierR) : (walkingDir == -1 ? enemySoldierL : enemySoldierR), pos.x - 16, pos.y - 32);
		if(!held)
		{
			g2d.enable(G2D.G_FILL);
			g2d.setColor(Color.BLACK);
			g2d.drawRectangle(pos.x - 17, pos.y - 3, 34, 6);
			g2d.setColor(Color.RED);
			g2d.drawRectangle(pos.x - 16, pos.y - 2, 32, 4);
			g2d.setColor(Color.ORANGE);
			g2d.drawRectangle(pos.x - 16, pos.y - 2, 32 * (health / maxHealth), 4);
			g2d.setColor(Color.GREEN);
			g2d.drawRectangle(pos.x - 16, pos.y - 2, 32 * (actualHealth / maxHealth), 4);
			g2d.disable(G2D.G_FILL);
		}
	}

	public static final BufferedImage friendlySoldier = Screen.getImage("/images/soldiers/horse_soldier/friendly.png");
	public static final BufferedImage enemySoldier = Screen.getImage("/images/soldiers/horse_soldier/enemy.png");
	public static final BufferedImage friendlySoldierL = friendlySoldier.getSubimage(0, 0, 32, 32);
	public static final BufferedImage friendlySoldierR = friendlySoldier.getSubimage(32, 0, 32, 32);
	public static final BufferedImage enemySoldierL = enemySoldier.getSubimage(0, 0, 32, 32);
	public static final BufferedImage enemySoldierR = enemySoldier.getSubimage(32, 0, 32, 32);
}
