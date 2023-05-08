package com.hk.globe.game.chars;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.hk.game.G2D;
import com.hk.globe.Screen;
import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;

public class BossSoldier extends WalkingSoldier
{
	public BossSoldier(GameScreen scr, Globe onGlobe)
	{
		super(scr, onGlobe);
		maxHealth = actualHealth = health = 2000;

//		movementSpeed = 0.01F;
		movementSpeed = 0.2F;
		soldierDamage = 100;
		towerDamage = 200;
		soldierHitCooldown = 120;
		towerHitCooldown = 120;
	}

	@Override
	public void paintSoldier(G2D g2d, boolean held)
	{
//		g2d.drawImage(onGlobe.friendly ? friendlySoldier : enemySoldier, pos.x - 8, pos.y - 25);
		g2d.drawImage(enemySoldier, pos.x - 32, pos.y - 64);
		if(!held)
		{
			g2d.enable(G2D.G_FILL);
			g2d.setColor(Color.BLACK);
			g2d.drawRectangle(pos.x - 33, pos.y - 3, 66, 6);
			g2d.setColor(Color.RED);
			g2d.drawRectangle(pos.x - 32, pos.y - 2, 64, 4);
			g2d.setColor(Color.ORANGE);
			g2d.drawRectangle(pos.x - 32, pos.y - 2, 64F * (health / maxHealth), 4);
			g2d.setColor(Color.GREEN);
			g2d.drawRectangle(pos.x - 32, pos.y - 2, 64F * (actualHealth / maxHealth), 4);
			g2d.disable(G2D.G_FILL);
		}
	}

	public static final BufferedImage enemySoldier = Screen.getImage("/images/soldiers/boss_soldier/enemy.png");
}
