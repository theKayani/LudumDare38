package com.hk.globe.player;

import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;

public class Player
{
	public final GameScreen scr;
	public final Globe baseGlobe;
	public final int[] resources;
	
	public Player(GameScreen scr)
	{
		this.scr = scr;
		baseGlobe = scr.baseGlobe;
		resources = new int[ResourceType.size()];
	}
	
	public void addResource(ResourceType type, int amt)
	{
		resources[type.ordinal()] += amt < 0 ? 0 : amt;
	}
	
	public void takeResource(ResourceType type, int amt)
	{
		resources[type.ordinal()] -= amt < 0 ? 0 : amt;
	}
	
	public boolean hasResource(ResourceType type, int amt)
	{
		return resources[type.ordinal()] >= amt;
	}
	
	public int getResource(ResourceType type)
	{
		return resources[type.ordinal()];
	}
	
	public void updatePlayer(int ticks)
	{
		if(ticks % 20 == 0)
		{
			for(int i = 0; i < baseGlobe.soldiers.size(); i++)
			{
				addResource(ResourceType.WOOD, 1);
			}
		}
		
		if(baseGlobe.left == null && baseGlobe.right == null)
		{
			if(ticks % 200 == 0)
			{
				addResource(ResourceType.LIVES, 1);
			}
		}
	}
}
