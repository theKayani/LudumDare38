package com.hk.globe.world;

import com.hk.game.G2D;
import com.hk.math.vector.Vector2F;

import java.awt.*;

@SuppressWarnings("unused")
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
	
	public void updateBomb(double delta)
	{
		if(blewUp)
		{
			animTick += delta * 60;
			
			if(animTick > 60)
				animTick = 60;
		}
		else
		{
			vel.y += 9.81 * delta;
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
