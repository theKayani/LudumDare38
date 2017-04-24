package com.hk.globe.world;

import com.hk.math.Rand;
import com.hk.math.vector.Vector2F;

import main.G2D;

public class Cloud
{
	public final Vector2F pos;
	private final float width, height, speed;
	
	public Cloud(float x, float y)
	{
		this.pos = new Vector2F(x, y);
		width = Rand.nextFloat() * 50F + 25F;
		height = Rand.nextFloat() * 20F + 10F;
		speed = Math.max(Rand.nextFloat() * 2F, 0.5F);
	}
	
	public void updateCloud(int ticks)
	{
		pos.x += speed;
	}
	
	public void paintCloud(G2D g2d)
	{
		g2d.setColor(0xAAFAFAFA, true);
		g2d.enable(G2D.G_FILL);
		g2d.drawRoundRectangle(pos.x, pos.y, width, height, width / 4, height / 4);
		g2d.disable(G2D.G_FILL);
	}
}
