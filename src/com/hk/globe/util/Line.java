package com.hk.globe.util;

import java.io.Serializable;

public final class Line implements Serializable
{
	public float x1, y1, x2, y2;
	
	public Line()
	{
		this(0, 0, 0, 0);
	}

	public Line(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	private static final long serialVersionUID = -4365603028334779473L;
}
