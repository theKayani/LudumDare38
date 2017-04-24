package com.hk.globe.world;

import java.awt.Color;

import com.hk.globe.LDGame;
import com.hk.math.vector.Vector2F;

import main.G2D;

public class Bomb
{
	public final Vector2F pos, vel;
	private boolean blewUp = false;
	private float animTick;
	
	public Bomb()
	{
		this.pos = new Vector2F();
		this.vel = new Vector2F();
	}
	
	public void updateBomb(int ticks)
	{
		if(blewUp)
		{
			animTick *= 1.25F;
			
			if(animTick > 60)
			{
				animTick = 60;
			}
		}
		else
		{
			vel.addLocal(0, LDGame.GRAVITY);
			pos.addLocal(vel);
		}
	}
	
	public void paintBomb(G2D g2d)
	{
		g2d.enable(G2D.G_CENTER | G2D.G_FILL);
		if(blewUp)
		{
			for(int i = (int) animTick; i > 0; i--)
			{
				float r = 1F - (i / 60F);
				float g = 1F - (i / 60F);
				g2d.setColor(r, g, 0F);
				g2d.drawCircle(pos.x, pos.y, i);
			}
		}
		else
		{
			g2d.setColor(Color.BLACK);
			g2d.drawCircle(pos.x, pos.y, 20);
		}
		g2d.disable(G2D.G_CENTER | G2D.G_FILL);
	}
	
	public boolean isDead()
	{
		return animTick >= 60;
	}
	
	public void blewUp()
	{
		if(!blewUp)
		{
			animTick = 1F;
		}
		this.blewUp = true;
	}
	
	public boolean hasBlownUp()
	{
		return this.blewUp;
	}
	
	public boolean collided(Globe globe)
	{
		float dst = pos.distance(globe.pos);
		return dst < globe.radius + 20;
	}
}
