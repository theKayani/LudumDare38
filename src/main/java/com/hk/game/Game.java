package com.hk.game;

public abstract class Game
{
	Handler handler;

	public abstract void update(double delta);

	public abstract void paint(G2D g2d);

	public final Handler getHandler()
	{
		return handler;
	}

	public void mouse(float x, float y, boolean pressed, int button) {}

	public void mouseMoved(float x, float y) {}

	public void mouseWheel(int amt) {}

	public void key(int key, char keyChar, boolean pressed) {}
}
