package com.hk.game;

import java.awt.event.*;
import java.awt.geom.Point2D.Float;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unused")
public class Handler implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	int w;
	int h;
	private final Float mouse;
	private final Game game;
	private final Map<Integer, Handler.KeyPress> keyMap;
	private final Map<Integer, Boolean> mouseMap;

	public Handler(Game game)
	{
		this.game = game;
		mouse = new Float();
		keyMap = new HashMap<>();
		mouseMap = new HashMap<>();
	}

	public void update()
	{
		for (Entry<Integer, KeyPress> val : keyMap.entrySet())
		{
			KeyPress kp = val.getValue();
			if (kp.pressed)
				game.key(val.getKey(), kp.keyChar, true);
		}

		for (Entry<Integer, Boolean> val : mouseMap.entrySet())
		{
			if (val.getValue())
				game.mouse(mouse.x, mouse.y, true, val.getKey());
		}
	}

	public void keyPressed(KeyEvent e)
	{
		keyMap.put(e.getKeyCode(), new KeyPress(true, e.getKeyChar()));
	}

	public void keyReleased(KeyEvent e)
	{
		game.key(e.getKeyCode(), e.getKeyChar(), false);
		keyMap.put(e.getKeyCode(), new KeyPress(false, e.getKeyChar()));
	}

	public void keyTyped(KeyEvent e) {}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		project(e.getX(), e.getY());
		game.mouseWheel(e.getWheelRotation());
	}

	public void mouseDragged(MouseEvent e)
	{
		project(e.getX(), e.getY());
		game.mouseMoved(mouse.x, mouse.y);
	}

	public void mouseMoved(MouseEvent e)
	{
		project(e.getX(), e.getY());
		game.mouseMoved(mouse.x, mouse.y);
	}

	public void mouseClicked(MouseEvent e)
	{
		project(e.getX(), e.getY());
	}

	public void mouseEntered(MouseEvent e)
	{
		project(e.getX(), e.getY());
		game.mouseMoved(mouse.x, mouse.y);
	}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e)
	{
		project(e.getX(), e.getY());
		mouseMap.put(e.getButton(), true);
	}

	public void mouseReleased(MouseEvent e)
	{
		project(e.getX(), e.getY());
		mouseMap.put(e.getButton(), false);
		game.mouse(mouse.x, mouse.y, false, e.getButton());
	}

	private void project(int x, int y)
	{
		mouse.x = (float) x / w * Main.WIDTH;
		mouse.y = (float) y / h * Main.HEIGHT;
	}

	public float mouseX()
	{
		return mouse.x;
	}

	public float mouseY()
	{
		return mouse.y;
	}

	public boolean isKeyDown(int keyCode)
	{
		return keyMap.containsKey(keyCode) && keyMap.get(keyCode).pressed;
	}

	public boolean isButton(int button)
	{
		return mouseMap.containsKey(button) && mouseMap.get(button);
	}

	private static class KeyPress
	{
		public final boolean pressed;
		public final char keyChar;

		public KeyPress(boolean pressed, char keyChar)
		{
			this.pressed = pressed;
			this.keyChar = keyChar;
		}
	}
}