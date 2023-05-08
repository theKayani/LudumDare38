package com.hk.globe.world;

import java.awt.Color;

import com.hk.game.G2D;
import com.hk.game.Main;
import com.hk.globe.LDGame;
import com.hk.math.FloatMath;
import com.hk.math.vector.Vector2F;

public class GlobePiece
{
	public final Vector2F pos, vel;
	public final float width;
	public float rot, rotV;
	private boolean detached = false;
	
	public GlobePiece(float x, float y, float rot, float width)
	{
		this.pos = new Vector2F(x, y);
		this.rot = rot;
		this.width = width;
		this.vel = new Vector2F();
	}
	
	public boolean updatePiece(double delta)
	{
		if(detached)
		{
			vel.y += 9.81 * delta;
			
			pos.addLocal(vel);

			rot += rotV;
			
			return pos.y > Main.HEIGHT + 10;
		}
		return false;
	}
	
	public void paintPiece(G2D g2d)
	{
		g2d.setColor(detached ? Color.RED : Color.WHITE);
		g2d.pushMatrix();
		g2d.rotateR(rot + FloatMath.PI / 2F, pos.x, pos.y);
		g2d.enable(G2D.G_CENTER | G2D.G_FILL);
		g2d.drawRectangle(pos.x, pos.y, width, 4);
		g2d.disable(G2D.G_CENTER | G2D.G_FILL);
		g2d.popMatrix();
	}
	
	public void detach()
	{
		detached = true;
	}
}
