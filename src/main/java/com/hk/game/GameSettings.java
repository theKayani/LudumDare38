package com.hk.game;

import java.awt.Color;

public class GameSettings
{
	public boolean fullscreen = false;
	public boolean constantGCCalls = false;
	public boolean resizeable = true;
	public boolean showFPS = false;
	public int width = 640;
	public int height = 480;
	public int maxFPS = 30;
	public int closeOperation = 3;
	public String title = "Game";
	public String version = null;
	public GameSettings.Quality quality;
	public Color background;

	public GameSettings()
	{
		quality = GameSettings.Quality.AVERAGE;
		background = Color.WHITE;
	}

	GameSettings(GameSettings s)
	{
		quality = GameSettings.Quality.AVERAGE;
		background = Color.WHITE;
		fullscreen = s.fullscreen;
		constantGCCalls = s.constantGCCalls;
		resizeable = s.resizeable;
		showFPS = s.showFPS;
		width = s.width;
		height = s.height;
		maxFPS = s.maxFPS;
		closeOperation = s.closeOperation;
		title = s.title;
		version = s.version;
		quality = s.quality;
		background = s.background;
	}

	public enum Quality
	{
		POOR,
		AVERAGE,
		GOOD;

		Quality() {}
	}
}
