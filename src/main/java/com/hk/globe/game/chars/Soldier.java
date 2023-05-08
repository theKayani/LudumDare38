package com.hk.globe.game.chars;

import java.util.ArrayList;
import java.util.List;

import com.hk.game.G2D;
import com.hk.game.Main;
import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;
import com.hk.math.MathUtil;
import com.hk.math.vector.Vector2F;

public abstract class Soldier
{
	public final GameScreen scr;
	public final Globe onGlobe;
	public final Vector2F pos, vel;
	protected float maxHealth;
	protected float actualHealth, health;
	protected Soldier target;
	
	public Soldier(GameScreen scr, Globe onGlobe)
	{
		this.scr = scr;
		this.onGlobe = onGlobe;
		pos = new Vector2F();
		vel = new Vector2F();
	}
	
	public void updateSoldier(double delta)
	{
		vel.y += 9.81 * delta;
		
		pos.addLocal(vel);
		health = health * 0.9F + actualHealth * 0.1F;
		
		if(pos.y > Main.HEIGHT + 100)
		{
			setHealth(0);
		}
	}
	
	public abstract void paintSoldier(G2D g2d, boolean held);
	
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
		setHealth(actualHealth - (amt > 0 ? amt : 0));
	}
	
	public float getHealth()
	{
		return actualHealth;
	}
	
	public void setMaxHealth(float maxHealth)
	{
		this.maxHealth = maxHealth;
		setHealth(actualHealth);
	}
	
	public boolean isDead()
	{
		return actualHealth <= 0;
	}

}
