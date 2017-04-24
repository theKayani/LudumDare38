package com.hk.globe.screen;

import java.awt.Color;
import java.awt.Rectangle;

import com.hk.globe.Screen;
import com.hk.globe.game.chars.Soldier;
import com.hk.globe.game.chars.SoldierData;
import com.hk.globe.player.ResourceType;

import main.G2D;
import main.Main;

public class UnitTab
{
	public final GameScreen scr;
	private boolean open = false;
	private float translation;
	private final Rectangle bounds;
	public int hoverOver = -1;
	public SoldierData heldSoldier;
	
	public UnitTab(GameScreen scr)
	{
		this.scr = scr;
		translation = Main.HEIGHT - 50;
		bounds = new Rectangle(0, 0, 400, Main.HEIGHT);
	}
	
	public void updateTab(int ticks)
	{
		if(open)
		{
			translation = translation * 0.9F;
		}
		else
		{
			translation = translation * 0.9F + (Main.HEIGHT - 50) * 0.1F;
		}
	}
	
	public void paintTab(G2D g2d)
	{
		g2d.pushMatrix();
		g2d.translate(g2d.width - 400, translation);
		g2d.setColor(140, 179, 204);
		// 64, 102, 128
		// 140, 179, 204
		// 160, 199, 224
		g2d.enable(G2D.G_FILL);
		g2d.drawRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
		g2d.setColor(Color.BLACK);
		g2d.disable(G2D.G_FILL);
		g2d.drawRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
		g2d.setColor(0F, 0.4F, 0.4F);
		
		g2d.enable(G2D.G_CENTER);
		g2d.setFontSize(28F);
		g2d.drawString("Units", bounds.width / 2, 15);
		g2d.disable(G2D.G_CENTER);
		
		if(Main.HEIGHT - 50 - translation > 5)
		{
			g2d.drawLine(0, 45, 400, 45);
			g2d.setColor(Color.BLUE);
			g2d.setFontSize(24F);
			for(int i = 0; i < Soldier.USABLE_SOLDIERS.length; i++)
			{
				SoldierData sd = Soldier.USABLE_SOLDIERS[i];
				g2d.setColor(scr.player.hasResource(ResourceType.LIVES, sd.lifeCost) && scr.player.hasResource(ResourceType.WOOD, sd.woodCost) ? (hoverOver == i ? new Color(0F, 0.5F, 1F) : Color.BLUE) : Color.RED);
				int w = 400;
				int h = (bounds.height - 45) / Soldier.USABLE_SOLDIERS.length;
				g2d.drawRectangle(0, h * i + 45, w, h);
				
				g2d.drawImage(sd.icon, 20, i * (bounds.height / Soldier.USABLE_SOLDIERS.length) + 45);
				g2d.setFontSize(28F);
				g2d.drawString(sd.name, 120, 50 + i * (bounds.height / Soldier.USABLE_SOLDIERS.length) + 45);

				g2d.setFontSize(24F);
				g2d.drawString("Health: " + Screen.fm.format(sd.health), 20, 100 + i * (bounds.height / Soldier.USABLE_SOLDIERS.length) + 45);
				g2d.drawString("Damage: " + Screen.fm.format(sd.damage), 20, 130 + i * (bounds.height / Soldier.USABLE_SOLDIERS.length) + 45);
				g2d.drawString("Attack Rate: " + Screen.fm.format(sd.attackRate) + " hits / s", 20, 160 + i * (bounds.height / Soldier.USABLE_SOLDIERS.length) + 45);
				g2d.drawString("Movement Speed: " + Screen.fm.format(sd.movementSpeed), 20, 190 + i * (bounds.height / Soldier.USABLE_SOLDIERS.length) + 45);
				g2d.drawString("Cost: " + Screen.fm.format(sd.lifeCost) + (sd.lifeCost == 1 ? " Life" : " Lives") + ", and " + Screen.fm.format(sd.woodCost) + " Wood", 20, 220 + i * (bounds.height / Soldier.USABLE_SOLDIERS.length) + 45);
			}
		}
		
		g2d.popMatrix();
	}
	
	public void click(float x1, float y1)
	{
		hoverOver = -1;
		hover(x1, y1);
		if(hoverOver != -1)
		{
			tryAndBuy(hoverOver);
		}
	}
	
	public void hover(float x1, float y1)
	{
		for(int i = 0; i < Soldier.USABLE_SOLDIERS.length; i++)
		{
			int w = 400;
			int h = (bounds.height - 45) / Soldier.USABLE_SOLDIERS.length;
			int x = 0;
			int y = h * i + 45;
			if(x1 > x && x1 < x + w && y1 > y && y1 < y + h)
			{
				hoverOver = i;
				break;
			}
		}
	}
	
	public void tryAndBuy(int indx)
	{
		SoldierData data = Soldier.USABLE_SOLDIERS[indx];
		if(scr.player.hasResource(ResourceType.LIVES, data.lifeCost) && scr.player.hasResource(ResourceType.WOOD, data.woodCost))
		{
			scr.player.takeResource(ResourceType.LIVES, data.lifeCost);
			scr.player.takeResource(ResourceType.WOOD, data.woodCost);
			heldSoldier = Soldier.USABLE_SOLDIERS[indx];
		}
	}
	
	public void toggle()
	{
		open = !open;
	}
	
	public boolean isOpen()
	{
		return open;
	}
}
