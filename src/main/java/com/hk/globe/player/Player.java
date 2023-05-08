package com.hk.globe.player;

import com.hk.globe.screen.GameScreen;
import com.hk.globe.world.Globe;
import com.hk.lua.Lua;
import com.hk.lua.LuaObject;

@SuppressWarnings("unused")
public class Player
{
	public final GameScreen scr;
	public final Globe baseGlobe;
	private final LuaObject playerObject;

	public Player(GameScreen scr)
	{
		this.scr = scr;
		baseGlobe = scr.baseGlobe;
		playerObject = scr.interpreter.require(scr.game.luaFile("player_resources.lua"));
	}
	
	public void addResource(String resourceType, int amt)
	{
		playerObject.rawGet("addResource").call(scr.interpreter, Lua.newString(resourceType), Lua.newNumber(amt));
	}
	
	public void takeResource(String resourceType, int amt)
	{
		playerObject.rawGet("takeResource").call(scr.interpreter, Lua.newString(resourceType), Lua.newNumber(amt));
	}
	
	public boolean hasResource(String resourceType, int amt)
	{
		LuaObject res = playerObject.rawGet("hasResource").call(scr.interpreter, Lua.newString(resourceType), Lua.newNumber(amt));
		if(!res.isBoolean())
			throw new IllegalStateException("unexpected result from player_resources.lua:hasResource: " + res);
		return res.getBoolean();
	}
	
	public Number getResource(String resourceType)
	{
		LuaObject res = playerObject.rawGet("getResource").call(scr.interpreter, Lua.newString(resourceType));
		if(!res.isNumber())
			throw new IllegalStateException("unexpected result from player_resources.lua:getResource: " + res);
		return res.isInteger() ? res.getLong() : res.getDouble();
	}

	public String[] getDisplayResources()
	{
		LuaObject res = playerObject.rawGet("getDisplayResources").call(scr.interpreter);
		if(!res.isTable())
			throw new IllegalStateException("unexpected result from player_resources.lua:getDisplayResources: " + res);
		long len = res.getLength();
		if(len > 2048)
			throw new IllegalStateException("player_resources.lua:getDisplayResources: too many display resources: " + len);
		String[] displayResources = new String[(int) res.getLength()];
		for (int i = 0; i < displayResources.length; i++)
		{
			LuaObject res2 = res.getIndex(scr.interpreter, i + 1);
			if(!res2.isString() && !res2.isTable())
				throw new IllegalStateException("unexpected result from player_resources.lua:getDisplayResources[" + i + "]: " + res);
			displayResources[i] = res2.getString(scr.interpreter);
		}
		return displayResources;
	}

	private final double maxWoodDelay = 0.3333333333333333;
	private final double maxLivesDelay = 3;
	private double woodDelay = maxWoodDelay;
	private double livesDelay = maxLivesDelay;
	public void updatePlayer(double delta)
	{
		woodDelay -= delta;
		while(woodDelay < 0)
		{
			addResource("Wood", baseGlobe.soldiers.size());
			woodDelay += maxWoodDelay;
		}

		if(baseGlobe.left == null && baseGlobe.right == null)
		{
			livesDelay -= delta;
			while(livesDelay < 0)
			{
				addResource("Lives", 1);
				livesDelay += maxLivesDelay;
			}
		}
//		playerObject.rawGet("updateResources").call(scr.interpreter, Lua.newNumber(delta));
	}
}
