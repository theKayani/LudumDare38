package com.hk.globe.player;

public enum ResourceType
{
	STONE, WOOD, LIVES;
	
	public static int size()
	{
		return values().length;
	}
	
	public static ResourceType get(int index)
	{
		return values()[index];
	}
}
