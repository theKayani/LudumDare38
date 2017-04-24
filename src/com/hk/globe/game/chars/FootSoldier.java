package com.hk.globe.game.chars;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.hk.globe.Screen;
import com.hk.globe.game.chars.SoldierData.SoldierStats;
import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;

import main.G2D;

/**
 * This is a foot soldier. He's melee and
 * only does damage when close to the target.
 * 
 * @author Wati888
 */
@SoldierStats(name = "Footman", lifeCost = 1, woodCost = 10, attackRate = 1000 / 50, damage = 15, health = 100, movementSpeed = 10, icon = "/images/soldiers/foot_soldier/icon.png")
public class FootSoldier extends WalkingSoldier
{
	public FootSoldier(GameScreen scr, Globe onGlobe)
	{
		super(scr, onGlobe);
		// The soldier's health is 100
		maxHealth = actualHealth = health = 100;

		movementSpeed = 0.1F;
		soldierDamage = 15;
		towerDamage = 20;
		soldierHitCooldown = 50;
		towerHitCooldown = 10;
	}

	@Override
	public void paintSoldier(G2D g2d, boolean held)
	{
		g2d.drawImage(onGlobe.friendly ? friendlySoldier : enemySoldier, pos.x - 8, pos.y - 25);
		if(!held)
		{
			g2d.enable(G2D.G_FILL);
			g2d.setColor(Color.BLACK);
			g2d.drawRectangle(pos.x - 9, pos.y - 3, 18, 6);
			g2d.setColor(Color.RED);
			g2d.drawRectangle(pos.x - 8, pos.y - 2, 16, 4);
			g2d.setColor(Color.ORANGE);
			g2d.drawRectangle(pos.x - 8, pos.y - 2, 16F * (health / maxHealth), 4);
			g2d.setColor(Color.GREEN);
			g2d.drawRectangle(pos.x - 8, pos.y - 2, 16F * (actualHealth / maxHealth), 4);
			g2d.disable(G2D.G_FILL);
		}
	}
	
	public static final BufferedImage friendlySoldier = Screen.getImage("/images/soldiers/foot_soldier/friendly.png");
	public static final BufferedImage enemySoldier = Screen.getImage("/images/soldiers/foot_soldier/enemy.png");
}
