package com.hk.globe.game.chars;

import com.hk.globe.LDGame;
import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;
import com.hk.math.MathUtil;
import com.hk.math.Rand;

public abstract class WalkingSoldier extends Soldier
{
	private final float diff = 1F - (Rand.nextFloat(2F) - 1F) / 10F;
	/**
	 * The globe that the soldier is currently on.
	 */
	private Globe onTopGlobe = null;
	/**
	 * The direction the soldier is walking.
	 */
	protected int walkingDir;
	/**
	 * The cooldown till the soldier can hit again.
	 */
	private int hitCooldown;
	private float backX = Float.NaN;
	
	protected float movementSpeed = Float.NaN;
	protected float movementDecay = 0.8F;
	protected int soldierDamage = Integer.MAX_VALUE;
	protected int towerDamage = Integer.MAX_VALUE;
	protected int soldierHitCooldown = Integer.MAX_VALUE;
	protected int towerHitCooldown = Integer.MAX_VALUE;

	public WalkingSoldier(GameScreen scr, Globe onGlobe)
	{
		super(scr, onGlobe);
	}

	@Override
	public void updateSoldier(double delta)
	{
		super.updateSoldier(delta);
		if(Float.isNaN(backX))
		{
			backX = pos.x - onGlobe.pos.x;
			if(Math.abs(backX) > onGlobe.radius * 0.8F)
			{
				backX = onGlobe.pos.x + (Rand.nextFloat(onGlobe.radius * 1.6F) - onGlobe.radius * 0.8F);
			}
		}

		// If it's on a globe, and walking, move it.
		if(onTopGlobe != null)
		{
			vel.x += movementSpeed * MathUtil.sign(walkingDir) * diff;
		}
		
		vel.x *= movementDecay;

		// Reset the globe thing every tick.
		onTopGlobe = null;
		for(int i = 0; i < scr.allGlobes.size(); i++)
		{
			Globe globe = scr.allGlobes.get(i);
			if(globe.inBounds(pos.x, pos.y) && globe.pos.y < pos.y)
			{
				vel.y = 0;
				pos.y = onGlobe.pos.y;
				onTopGlobe = globe;
				onTopGlobe.hasSoldierOn = true;
				break;
			}
		}
		
		walk(0);
		if(onGlobe.left == null && onGlobe.right == null)
		{
			moveTo(onGlobe.pos.x + backX, onGlobe.pos.y, 20);
			return;
		}
		
		Globe attackGlobe;
		if(onGlobe.left != null)
			attackGlobe = onGlobe.left;
		else
			attackGlobe = onGlobe.right;

		if(attackGlobe.tower.getHealth() <= 0)
		{
			moveTo(onGlobe.pos.x + backX, onGlobe.pos.y, 20);
		}
		else if(target == null && !attackGlobe.soldiers.isEmpty())
		{
			target = Rand.nextFrom(attackGlobe.soldiers);
			target.target = this;
		}
		else if(target == null && attackGlobe.tower.getHealth() > 0)
		{
			moveTo(attackGlobe.pos.x, attackGlobe.pos.y, 20);

			if(attackGlobe.pos.distanceSquared(pos) < 625)
			{
				hitCooldown--;
				if(hitCooldown <= 0)
				{
					attackGlobe.tower.removeHealth(attackGlobe.friendly && attackGlobe.soldiers.isEmpty() ? towerDamage * 10 : towerDamage * diff);
					hitCooldown = (int) (towerHitCooldown * diff);
				}
			}
		}
		else if(target != null)
		{
			moveTo(target.pos.x, target.pos.y, 20);

			if(target.pos.distanceSquared(pos) < 625)
			{
				hitCooldown--;
				if(hitCooldown <= 0)
				{
					boolean wasAlive = onGlobe.friendly && target.getHealth() > 0;
					target.removeHealth(soldierDamage * diff);
					if(target.isDead() && wasAlive)
						scr.enemiesKilled++;

					hitCooldown = (int) (soldierHitCooldown * diff);
				}
			}

			if(target.isDead())
			{
				target = null;
			}
		}
	}
	
	public void moveTo(float x, float y, int threshold)
	{
		if(x - pos.x < -threshold)
		{
			walk(-1);
		}
		else if(x - pos.x > threshold)
		{
			walk(1);
		}
		else
		{
			walk(0);
		}
	}
	
	public void walk(int dir)
	{
		this.walkingDir = dir;
	}
	
	public void jump()
	{
		if(onTopGlobe != null)
		{
			vel.y -= 2F;
		}
	}
}
