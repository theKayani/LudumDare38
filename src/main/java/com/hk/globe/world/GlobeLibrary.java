package com.hk.globe.world;

import com.hk.lua.*;

import java.util.function.BiConsumer;

public enum GlobeLibrary implements BiConsumer<Environment, LuaObject>, Lua.LuaMethod
{
	_new() {
		@Override
		public LuaObject call(LuaInterpreter interp, LuaObject[] args)
		{
			return super.call(interp, args);
		}

		@Override
		public String toString()
		{
			return "new";
		}
	};

	/** {@inheritDoc} */
	@Override
	public LuaObject call(LuaInterpreter interp, LuaObject[] args)
	{
		throw new Error();
	}

	/** {@inheritDoc} */
	@Override
	public void accept(Environment env, LuaObject table)
	{
		String name = toString();
		if(name != null && !name.trim().isEmpty())
			table.rawSet(Lua.newString(name), Lua.newMethod(this));
	}
}
