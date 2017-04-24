package com.hk.globe.game.chars;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hk.globe.Screen;
import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;

public class SoldierData
{
	public final Class<? extends Soldier> cls;
	
	public final int health, damage, movementSpeed, attackRate, lifeCost, woodCost;
	public final String name;
	public final BufferedImage icon;
	
	public SoldierData(Class<? extends Soldier> cls)
	{
		this.cls = cls;
		SoldierStats ss = cls.getAnnotation(SoldierStats.class);
		
		health = ss.health();
		damage = ss.damage();
		movementSpeed = ss.movementSpeed();
		attackRate = ss.attackRate();
		name = ss.name();
		lifeCost = ss.lifeCost();
		woodCost = ss.woodCost();
		icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = icon.createGraphics();
		g.scale(2, 2);
		g.drawImage(Screen.getImage(ss.icon()), 0, 0, null);
	}
	
	public Soldier createSoldier(GameScreen scr, Globe globe)
	{
		try
		{
			return cls.getConstructor(GameScreen.class, Globe.class).newInstance(scr, globe);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SoldierStats
	{
		int health();
		
		int damage();
		
		int movementSpeed();
		
		int attackRate();
		
		int lifeCost();
		
		int woodCost();
		
		String icon();
		
		String name();
	}
}
