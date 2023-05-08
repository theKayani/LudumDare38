package com.hk.globe.world;

import com.hk.game.G2D;
import com.hk.globe.game.chars.Soldier;
import com.hk.lua.LuaInterpreter;
import com.hk.math.FloatMath;
import com.hk.math.vector.Vector2F;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The globe's class. This handles the globe's:
 * <li> Soldiers
 * <li> Dome pieces
 * <li> Tower
 * 
 * @author Wati888
 */
public class Globe
{
	/**
	 * The controller
	 */
	public final LuaInterpreter interpreter;
	/**
	 * Whether this globe is friendly or not.
	 * Blue or Red.
	 */
	public final boolean friendly;
	/**
	 * The position of the globe in the world.
	 */
	public final Vector2F pos;
	/**
	 * The soldiers that are defending this globe.
	 */
	public final List<Soldier> soldiers;
	/**
	 * The globe's outline, the glass dome itself.
	 */
	private final List<GlobePiece> lines;
	/**
	 * The radius of the tower. This directly correlates
	 * to the health of the tower.
	 */
	public final float radius;
	/**
	 * The tower of the globe.
	 */
	public final Tower tower;
	/**
	 * The globe to the left, and right of it.
	 * This is for the soldiers to keep track.
	 */
	public Globe left, right;
	public boolean hasSoldierOn = false;
	private boolean blowUp = false;
	private float blowUpTimer;
	
	public Globe(LuaInterpreter interpreter, boolean friendly, float x, float y, float radius)
	{
		this.interpreter = interpreter;
		this.friendly = friendly;
		this.pos = new Vector2F(x, y);
		this.radius = radius;
		this.tower = new Tower(this, friendly);
		lines = new ArrayList<>();
		soldiers = new ArrayList<>();
		
		// Creating the dome.
		for(int i = 0; i < 360; i += 10)
		{
			float ri = FloatMath.toRadians(i);
			float x1 = FloatMath.cos(ri) * radius + x;
			float y1 = FloatMath.sin(ri) * radius + y;
			lines.add(new GlobePiece(x1, y1, ri, radius * 0.2F));
		}
	}
	
	/**
	 * Updating the globe, it's pieces, and the soldiers.
	 */
	public void updateGlobe(double delta)
	{
		tower.updateTower();

		for(int i = lines.size() - 1; i >= 0; i--)
		{
			if(lines.get(i).updatePiece(delta))
			{
				lines.remove(i);
			}
		}
		
		for(int i = 0; i < soldiers.size(); i++)
		{
			Soldier s = soldiers.get(i);
			s.updateSoldier(delta);
			
			if(s.isDead())
			{
				soldiers.remove(i);
				i--;
			}
		}
		
		if(blowUp)
		{
			blowUpTimer *= 1.25F;
			if(blowUpTimer > 110)
			{
				blowUpTimer = 110;
			}
		}
		else if((!hasSoldierOn && tower.getHealth() <= 0) || lines.isEmpty())
		{
			blowUp = true;
			blowUpTimer = 1F;
			for (GlobePiece l : lines)
			{
				float dx = pos.x - l.pos.x;

				// Break off the dome piece, and drop it.
				l.detach();
				l.rotV = dx < 0 ? -15 : 15;

				l.vel.x -= dx / 3F;
				l.vel.y -= 5F;
			}
		}
		
		hasSoldierOn = false;
	}
	
	/**
	 * Painting the globe at it's given position.
	 */
	public void paintGlobe(G2D g2d)
	{
		g2d.pushColor();
		// Creating the brown ground.
		if(!blowUp && blowUpTimer < 110)
		{
			g2d.setColor(0x8B4538);
			g2d.g2d.fillArc((int) (pos.x - radius + 5), (int) (pos.y - radius + 5), (int) radius * 2 - 10, (int) radius * 2 - 10, 0, -180);
			g2d.setColor(Color.GREEN);		
			g2d.enable(G2D.G_FILL);
			// The green grass.
			g2d.drawRectangle(pos.x - radius + 4, pos.y - 4, radius * 2 - 8, 8);		
			g2d.disable(G2D.G_FILL);
		
			// Drawing the outline pieces.
			for (GlobePiece line : lines)
				line.paintPiece(g2d);

			// Painting this globe's tower.
			tower.paintTower(g2d);
		}
		
		g2d.popColor();
		
		g2d.enable(G2D.G_CENTER | G2D.G_FILL);
		if(blowUp && blowUpTimer < 110)
		{
			float t = Math.min(blowUpTimer, 100F);
			for(int i = (int) t / 2; i > 0; i--)
			{
				float r = 1F - (i / (t / 2));
				float g = 1F - (i / (t / 2));
				g2d.setColor(r, g, 0F);
				g2d.drawCircle(pos.x, pos.y, ((i * 2) / (t * 0.9F)) * radius);
			}
		}
		g2d.disable(G2D.G_CENTER | G2D.G_FILL);
	}
	
	public void paintSoldiers(G2D g2d)
	{
		// Painting this globe's soldier.
		for (Soldier soldier : soldiers)
			soldier.paintSoldier(g2d, false);
	}
	
	public boolean isDead()
	{
		return blowUp && blowUpTimer >= 110F && lines.isEmpty();
	}
	
	/** 
	 * Returns whether the globe has been hit at this position.
	 * If yes, then performs all the damage, etc.
	 */
	public boolean hasHit(float x, float y)
	{
		if(!inBounds(x, y))
		{
			return false;
		}
		boolean hit = false;
		for (GlobePiece l : lines)
		{
			float dx = x - l.pos.x;
			float dy = y - l.pos.y;

			// Break off the dome piece, and drop it.
			if (dx * dx + dy * dy < 2500)
			{
				l.detach();
				l.rotV = dx < 0 ? -15 : 15;

				l.vel.x -= dx / 3F;
				l.vel.y -= 5F;
				hit = true;
			}
		}
		hit = hit || pos.y < y || tower.bounds.contains(x, y);
		if(hit)
		{
			// Damage the soldiers if there are any.
			for (Soldier s : soldiers)
			{
				float dst = s.pos.distanceSquared(x, y);
				if (dst < 2500)
				{
					float r = (2500F - dst) / 2500F * 50F;
					s.removeHealth(r);
				}
			}
			// Damage the tower as well.
			tower.removeHealth(15);
		}
		return hit;
	}
	
	/**
	 * Returns true if the specific point is inside the globe.
	 */
	public boolean inBounds(float x, float y)
	{
		return pos.distanceSquared(x, y) < radius * radius;
	}
}
