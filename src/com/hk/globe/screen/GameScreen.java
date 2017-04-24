package com.hk.globe.screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hk.globe.LDGame;
import com.hk.globe.Screen;
import com.hk.globe.game.chars.BossSoldier;
import com.hk.globe.game.chars.FootSoldier;
import com.hk.globe.game.chars.Soldier;
import com.hk.globe.game.chars.SoldierData;
import com.hk.globe.player.Player;
import com.hk.globe.player.ResourceType;
import com.hk.globe.world.Bomb;
import com.hk.globe.world.Cloud;
import com.hk.globe.world.Globe;
import com.hk.globe.world.Tower;
import com.hk.math.MathUtil;
import com.hk.math.Rand;
import com.hk.math.vector.Color3F;
import com.hk.math.vector.Vector2F;
import com.sun.glass.events.KeyEvent;

import main.G2D;
import main.Main;

/**
 * Main game screen renderer and updater.
 * 
 * @author Wati888
 */
public class GameScreen extends Screen
{
	/**
	 * The background color.
	 */
	private final Color3F clr = new Color3F(0.8F, 1.0F, 1.0F).subtractLocal(0.05F);
	/**
	 * The last pressed location of the mouse.
	 */
	private final Vector2F lastPress = new Vector2F();
	/**
	 * Main player resource handler.
	 */
	public final Player player;
	/**
	 * The player's globe, the one they have to protect.
	 */
	public final Globe baseGlobe;
	/**
	 * All the globes attacking the player.
	 */
	public final List<Globe> allGlobes;
	/**
	 * The shaking of the screen. How much longer it's gonna happen.
	 */
	private int shakeAmt;
	/**
	 * The shaking of the screen.
	 */
	private Vector2F shake;
	/**
	 * The translation of the camera.
	 */
	private Vector2F move;
	/**
	 * The visual scale of the camera.
	 */
	private float zoom = 0.01F;
	/**
	 * The actual scale of the camera.
	 */
	private float actualZoom = 1F;
	/**
	 * The bombs in the game, constantly checking
	 * collisions with globes.
	 */
	private final List<Bomb> bombs;
	/**
	 * The clouds.
	 */
	private final Cloud[] clouds;
	private final Rectangle[] pauseButtons;
	/**
	 * The approaching globe, it isn't in the game until it "lands"
	 */
	private Globe comingGlobe;
	/**
	 * Where the coming globe currently is.
	 */
	private Vector2F comingFrom;
	/**
	 * Where the globe is coming from.
	 */
	private boolean leftSide;
	/**
	 * The timer to the next globe.
	 */
	private int nextGlobe = 1000;
	/**
	 * The transform for the base tower's bounds.
	 */
	public final AffineTransform aft;
	private final UnitTab unitTab;
	private float hovX = Float.NaN, hovY = Float.NaN;
	private Tower hovTower = null;
	public int difficulty = 1;
	private static int tutorial = 4;
	public boolean paused = false;
	public int enemiesKilled = 0;
	
	public GameScreen(LDGame game)
	{
		super(game);
		unitTab = new UnitTab(this);
		aft = new AffineTransform();
		// Positioning the base globe.
		baseGlobe = new Globe(this, true, Main.WIDTH / 2, Main.HEIGHT * 4 / 5, 250);
		for(int i = 0; i < 3; i++)
		{
			Soldier s = new FootSoldier(this, baseGlobe);
			s.pos.y = baseGlobe.pos.y;
			s.pos.x = Main.WIDTH / 2 + (i - 1) * 100;
			baseGlobe.soldiers.add(s);
		}
		player = new Player(this);
		
		
		allGlobes = new ArrayList<>();
		allGlobes.add(baseGlobe);
		
		bombs = new ArrayList<>();
		shake = new Vector2F();
	
		move = new Vector2F();
		
		clouds = new Cloud[50];
		for(int i = 0; i < clouds.length; i++)
		{
			// Creating random clouds to be rendered.
			clouds[i] = new Cloud(Rand.nextFloat() * (Main.WIDTH + 500) - 500, Rand.nextFloat() * (Main.HEIGHT + 700) - 350);
		}

		int w = 800;
		int h = 400;
		int x = Main.WIDTH / 2 - w / 2;
		int y = Main.HEIGHT / 2 - h / 2;
			
		pauseButtons = new Rectangle[2];
		
		Rectangle resumeButton = new Rectangle(360, 100);
		resumeButton.x = x + w / 2 - 200;
		resumeButton.y = y + h - 70;
		pauseButtons[0] = resumeButton;
		
		Rectangle exitButton = new Rectangle(360, 100);
		exitButton.x = x + w - 200;
		exitButton.y = y + h - 70;
		pauseButtons[1] = exitButton;

		for(Rectangle r : pauseButtons)
		{
			r.x -= r.width / 2;
			r.y -= r.height / 2;
		}
	}

	/**
	 * Runs every tick, handling the globes,
	 * bombs, the player, and clouds.
	 */
	@Override
	public void updateScreen(int ticks)
	{
		zoom = zoom * 0.9F + actualZoom * 0.1F;
	
		if(!paused && tutorial == 0)
		{
			if(baseGlobe.isDead())
			{
				return;
			}
			
			if(hovTower != null && hovTower.globe.isDead())
			{
				hovTower = null;
				hovX = hovY = Float.NaN;
			}
			// Counting down to the next globe.
			if(nextGlobe > 0)
			{
				nextGlobe--;
				
				if(nextGlobe == 0)
				{
					boolean put = false;
					// Check which side of the globe should the coming globe
					// be on.
					if(baseGlobe.left == null)
					{
						// Choose the starting location for the globe
						comingFrom = new Vector2F(-200, -200);
						leftSide = true;
						put = true;
					}
					else if(baseGlobe.right == null)
					{
						// Choose the starting location for the globe
						comingFrom = new Vector2F(Main.WIDTH + 200, -200);
						leftSide = false;
						put = true;
					}
					// If and only if the globe has an open spot;
					if(put)
					{
						// Spawn the globe and position it at
						// where it's supposed to be when it lands.
						float rad = getNextRadius();
						comingGlobe = new Globe(this, false, baseGlobe.pos.x + (leftSide ? -250 - rad : 250 + rad), baseGlobe.pos.y, rad);
						List<Soldier> soldiers = genSoldiers(getEnemyPower(), comingGlobe);
						comingGlobe.soldiers.addAll(soldiers);
						difficulty++;
					}
					else
					{
						nextGlobe = 200;
					}
				}
			}
			
			// Move the coming globe.
			if(comingGlobe != null)
			{
				// Move it towards it's actual location.
				comingFrom.addLocal(comingGlobe.pos.subtract(comingFrom).normalizeLocal().multLocal(3F));
				
				// If it's close enough, cement it into place.
				if(comingFrom.distanceSquared(comingGlobe.pos) < 25)
				{
					allGlobes.add(comingGlobe);
					if(leftSide)
					{
						baseGlobe.left = comingGlobe;
						comingGlobe.right = baseGlobe;
					}
					else
					{
						baseGlobe.right = comingGlobe;
						comingGlobe.left = baseGlobe;
					}
					comingGlobe = null;
					comingFrom = null;
					shake(100);
					nextGlobe = 2000 - (difficulty * 10);
				}
			}
			
			// Cycle through all the bombs and tick them all.
			for(int i = 0; i < bombs.size(); i++)
			{
				Bomb bomb = bombs.get(i);
				if(bomb != null)
				{
					bomb.updateBomb(ticks);
					if(bomb.isDead())
					{
						bombs.remove(i);
						i--;
						return;
					}
				}
				
				// VERY SIMPLE Bomb collision detection
				if(bomb != null && !bomb.hasBlownUp() && !bomb.isDead())
				{
					for(int j = 0; j < allGlobes.size(); j++)
					{
						Globe globe = allGlobes.get(j);
						if(globe.inBounds(bomb.pos.x, bomb.pos.y) && globe.hasHit(bomb.pos.x, bomb.pos.y))
						{
							shake(50);
							bomb.blewUp();
						}
					}
				}
			}
		}
		
		// The clouds...
		for(int i = 0; i < clouds.length; i++)
		{
			Cloud cloud = clouds[i];
			cloud.updateCloud(ticks);
			
			if(cloud.pos.x >= Main.WIDTH + 500)
			{
				clouds[i] = new Cloud(-575, Rand.nextFloat() * (Main.HEIGHT + 700) - 350);
			}
		}
			
		if(!paused && tutorial == 0)
		{	
			// The shaking of the camera.
			if(shakeAmt > 0)
			{
				shakeAmt--;
				shake.set(Vector2F.randUnitVector().mult(shakeAmt / 10F));
			}
			
			// Tick all the globes on screen. Never more than 3.
			for(int i = 0; i < allGlobes.size(); i++)
			{
				Globe globe = allGlobes.get(i);
				globe.updateGlobe(ticks);
				if(globe.isDead())
				{
					if(globe.left != null)
					{
						globe.left.right = null;
						globe.left = null;
					}
					if(globe.right != null)
					{
						globe.right.left = null;
						globe.right = null;
					}
					allGlobes.remove(i);
					i--;
				}
			}
			
			// Update the player.
			player.updatePlayer(ticks);
			
			unitTab.updateTab(ticks);
		}
	}

	/**
	 * Paint the screen to the graphics wrapper object.
	 */
	@Override
	public void paintScreen(G2D g2d)
	{
		g2d.setColor(clr.r, clr.g, clr.b);
		g2d.enable(G2D.G_FILL);
		g2d.drawRectangle(0, 0, g2d.width, g2d.height);
		g2d.disable(G2D.G_FILL);
		g2d.pushMatrix();

		// Position the camera accurately in the center.
		double ax = (g2d.width - (g2d.width * zoom)) / 2D;
		double ay = (g2d.height - (g2d.height * zoom)) / 2D;
		g2d.translate(shake.x + ax, shake.y + ay);
		g2d.scale(zoom, zoom);
		g2d.translate(move.x, move.y);
		// Transfer these values to a new aft for the tower
		// bounds as well.
		aft.setToIdentity();
		aft.translate(shake.x + ax, shake.y + ay);
		aft.scale(zoom, zoom);
		aft.translate(move.x, move.y);
		
		// Paint the bounds.
		for(Bomb bomb : bombs)
		{
			if(bomb != null)
			{
				bomb.paintBomb(g2d);
			}
		}
		
		// Paint the clouds.
		for(Cloud cloud : clouds)
		{
			cloud.paintCloud(g2d);
		}
		
		// Paint the globes, and soldiers.
		for(int i = 0; i < allGlobes.size(); i++)
		{
			allGlobes.get(i).paintGlobe(g2d);
		}
		for(int i = 0; i < allGlobes.size(); i++)
		{
			allGlobes.get(i).paintSoldiers(g2d);
		}
		
		// Paint the coming globe.
		if(comingGlobe != null)
		{
			g2d.pushMatrix();
			g2d.translate(comingFrom.x - comingGlobe.pos.x, comingFrom.y - comingGlobe.pos.y);
			comingGlobe.paintGlobe(g2d);
			comingGlobe.paintSoldiers(g2d);
			g2d.popMatrix();
		}

		if(tutorial != 0)
		{
			Vector2F twPos = baseGlobe.tower.pos;
			g2d.setColor(Color.WHITE);
			g2d.enable(G2D.G_FILL);
			g2d.drawRoundRectangle(twPos.x + 40, twPos.y - 250, 310, 150, 20, 20);
			g2d.disable(G2D.G_FILL);
			g2d.setColor(Color.BLACK);
			g2d.drawRoundRectangle(twPos.x + 40, twPos.y - 250, 310, 150, 20, 20);
			
			g2d.enable(G2D.G_CENTER);
			if(tutorial == 4)
			{
				g2d.drawString("(Press SPACE to continue)", twPos.x + 40 + 150, twPos.y - 250 + 140);
				g2d.setFontSize(24F);
				g2d.drawString("MOUSE-WHEEL TO", twPos.x + 40 + 155, twPos.y - 250 + 50);
				g2d.drawString("ZOOM IN AND OUT", twPos.x + 40 + 155, twPos.y - 250 + 100);
			}
			else if(tutorial == 3)
			{
				g2d.drawString("(Press SPACE to continue)", twPos.x + 40 + 150, twPos.y - 250 + 140);
				g2d.setFontSize(24F);
				g2d.drawString("RIGHT-CLICK DRAG", twPos.x + 40 + 155, twPos.y - 250 + 50);
				g2d.drawString("TO MOVE SCREEN", twPos.x + 40 + 155, twPos.y - 250 + 100);
			}
			else if(tutorial == 2)
			{
				g2d.drawString("This is your tower. Protect it at all costs!", twPos.x + 40 + 155, twPos.y - 250 + 5);
				g2d.drawString("Use the soldiers down there to defend your dome.", twPos.x + 40 + 155, twPos.y - 250 + 20);
				g2d.drawString("You can train more soldiers in the Units tab to your", twPos.x + 40 + 155, twPos.y - 250 + 35);
				g2d.drawString("bottom right. Click and hold on your tower to build", twPos.x + 40 + 155, twPos.y - 250 + 50);
				g2d.drawString("your STONE resource. Your soldiers will earn your", twPos.x + 40 + 155, twPos.y - 250 + 65);
				g2d.drawString("WOOD resource. The LIVES resource will generate", twPos.x + 40 + 155, twPos.y - 250 + 80);
				g2d.drawString("when there is no enemy globe attacking yours. The", twPos.x + 40 + 155, twPos.y - 250 + 95);
				g2d.drawString("enemy will send their globes and soldiers to attack", twPos.x + 40 + 155, twPos.y - 250 + 110);
				g2d.drawString("your tower and soldiers. Destroy their towers to kill them!", twPos.x + 40 + 155, twPos.y - 250 + 125);
				g2d.drawString("(Press SPACE to continue)", twPos.x + 40 + 150, twPos.y - 250 + 140);
			}
			else if(tutorial == 1)
			{
				g2d.drawString("STONE is used to repair your tower. Right click your", twPos.x + 40 + 155, twPos.y - 250 + 5);
				g2d.drawString("tower and it will gain 50 HP at the cost of 50 STONE.", twPos.x + 40 + 155, twPos.y - 250 + 20);
				g2d.drawString("WOOD and LIVES are used to purchase new soldiers.", twPos.x + 40 + 155, twPos.y - 250 + 35);
				g2d.drawString("Every 10 waves, a boss level will come.", twPos.x + 40 + 155, twPos.y - 250 + 50);
				g2d.drawString("PLAN ACCORDINGLY! These units can only work so", twPos.x + 40 + 155, twPos.y - 250 + 65);
				g2d.drawString("fast! Use keys 1, 2, and 3 for quick unit purchase.", twPos.x + 40 + 155, twPos.y - 250 + 80);
				g2d.drawString("Click the timer in the bottom left to skip to next", twPos.x + 40 + 155, twPos.y - 250 + 95);
				g2d.drawString("wave. You also receive resources for the time lost.", twPos.x + 40 + 155, twPos.y - 250 + 110);
				g2d.drawString("Thank you for playing! #LudumDare38", twPos.x + 40 + 155, twPos.y - 250 + 125);
				g2d.drawString("(Press SPACE to begin)", twPos.x + 40 + 150, twPos.y - 250 + 140);
			}
			g2d.disable(G2D.G_CENTER);
		}
		
		g2d.popMatrix();

		g2d.setFont(new Font("Arial", Font.BOLD, 28));

		g2d.enable(G2D.G_FILL);
		
		if(!Float.isNaN(hovX) && !Float.isNaN(hovY))
		{
			String txt = Screen.fm.format((int) hovTower.getHealth()) + " / " + Screen.fm.format((int) hovTower.maxHealth) + " HP";
			Rectangle2D r2d = g2d.getStringBounds(txt);
			g2d.setColor(Color.WHITE);
			g2d.drawRectangle(hovX, hovY, r2d.getWidth(), r2d.getHeight() + 10);
			g2d.disable(G2D.G_FILL);
			g2d.setColor(Color.BLACK);
			g2d.drawRectangle(hovX, hovY, r2d.getWidth(), r2d.getHeight() + 10);
			
			g2d.enable(G2D.G_CENTER);
			g2d.drawString(txt, hovX + r2d.getWidth() / 2D, hovY + r2d.getHeight() / 2D);
			g2d.disable(G2D.G_CENTER);
			
			g2d.enable(G2D.G_FILL);
		}
		
		// Draw the player resources in the corner of the screen.
		g2d.setColor(clr.r - 0.2F, clr.g - 0.2F, clr.b - 0.2F);
		g2d.drawRoundRectangle(-50, -50, 250, 150, 50, 50);
		g2d.disable(G2D.G_FILL);
		g2d.setColor(clr.r - 0.5F, clr.g - 0.5F, clr.b - 0.5F);
		g2d.drawRoundRectangle(-50, -50, 250, 150, 50, 50);

		g2d.drawString("Stone: " + fm.format(player.resources[ResourceType.STONE.ordinal()]), 5, 30);
		g2d.drawString("Wood: " + fm.format(player.resources[ResourceType.WOOD.ordinal()]), 5, 60);
		g2d.drawString("Lives: " + fm.format(player.resources[ResourceType.LIVES.ordinal()]), 5, 90);
		
		unitTab.paintTab(g2d);
		
		if(unitTab.heldSoldier != null)
		{
			g2d.drawImage(unitTab.heldSoldier.icon, handler.mouseX(), handler.mouseY());
		}
		
		g2d.setFontSize(28F);
		g2d.setColor(Color.RED);
		g2d.drawString(getEnemyPower(), 5, g2d.height - 30);
		g2d.drawString((nextGlobe / 60 == 0 ? "Next Attack: Now!" : "Next Attack: " + (nextGlobe / 60)), 5, g2d.height - 5);
		g2d.setColor(Color.BLUE);
		g2d.drawString("Wave: " + difficulty, 210, 30);

		if(baseGlobe.isDead() || paused)
		{
			g2d.enable(G2D.G_FILL);
			g2d.setColor(0xAA000000, true);
			g2d.drawRectangle(0, 0, g2d.width, g2d.height + 1);

			int w = 800;
			int h = 400;
			int x = Main.WIDTH / 2 - w / 2;
			int y = Main.HEIGHT / 2 - h / 2;
			g2d.clearRect(x, y, w, h);
			
			g2d.setColor(clr.r - 0.2F, clr.g - 0.2F, clr.b - 0.2F);
			g2d.drawRectangle(x, y, w, h);
			
			g2d.disable(G2D.G_FILL);
			
			g2d.setColor(Color.BLUE);
			g2d.drawRectangle(x, y, w, h);
			
			g2d.enable(G2D.G_FILL);
			
			if(paused)
			{
				g2d.setColor(Color.BLUE);
				g2d.enable(G2D.G_CENTER);
				g2d.drawString("Paused", x + w / 2, y + 25);
				g2d.disable(G2D.G_CENTER);
				
				for(int i = 0; i < pauseButtons.length; i++)
				{
					Rectangle r = pauseButtons[i];
					g2d.setColor(new Color(160, 199, 224));
					g2d.drawRectangle(r.x, r.y, r.width, r.height);
					g2d.enable(G2D.G_CENTER);
					g2d.setColor(r.contains(handler.mouseX(), handler.mouseY()) ? Color.RED : Color.BLUE);
					g2d.drawString(getPauseButtonText(i), r.getCenterX(), r.getCenterY());
					g2d.disable(G2D.G_CENTER);
				}
			}
			else if(baseGlobe.isDead())
			{
				g2d.setColor(Color.RED);
				g2d.enable(G2D.G_CENTER);
				g2d.drawString("Game Over", x + w / 2, y + h * 1 / 5);
				g2d.drawString("Wave: " + difficulty, x + w / 2, y + h * 2 / 5);
				g2d.drawString("Enemies Killed: " + enemiesKilled, x + w / 2, y + h * 3 / 5);
				g2d.drawString("(Click anywhere to go to the main menu)", x + w / 2, y + h * 4 / 5);
				g2d.disable(G2D.G_CENTER);
			}
		}
	}
	
	/**
	 * Helper method to shake the screen
	 * the amount of ticks specified.
	 */
	public void shake(int amt)
	{
		shakeAmt = amt > 0 ? amt : 0;
	}
	
	public List<Soldier> genSoldiers(int difficulty, Globe globe)
	{
		List<Soldier> soldiers = new ArrayList<>();
		List<SoldierData> validChoices = new ArrayList<>(Arrays.asList(Arrays.copyOf(Soldier.USABLE_SOLDIERS, Soldier.USABLE_SOLDIERS.length)));
		
		if(this.difficulty % 10 == 0)
		{
			for(int i = 0; i < difficulty / 10; i++)
			{
				Soldier s = new BossSoldier(this, globe);
				s.pos.x = globe.pos.x + i * (Rand.nextFloat(globe.radius * 1.6F) - globe.radius * 0.8F);
				s.pos.y = globe.pos.y;
				soldiers.add(s);
			}
			for(int i = 0; i < difficulty / 5; i++)
			{
				Soldier s = new FootSoldier(this, globe);
				s.pos.x = globe.pos.x + i * (Rand.nextFloat(globe.radius * 1.6F) - globe.radius * 0.8F);
				s.pos.y = globe.pos.y;
				soldiers.add(s);
			}
		}
		else
		{
			while(difficulty > 0)
			{
				for(int i = 0; i < validChoices.size(); i++)
				{
					if(validChoices.get(i).lifeCost > difficulty)
					{
						validChoices.remove(i);
						i--;
					}
				}
			
				SoldierData chsn = Rand.nextFrom(validChoices);
				difficulty -= chsn.lifeCost;
			
				Soldier soldier = chsn.createSoldier(this, globe);
				soldier.pos.set(globe.pos.x + Rand.nextFloat(globe.radius * 1.6F) - globe.radius * 0.8F, globe.pos.y);
				soldiers.add(soldier);
			}
		}
		
		return soldiers;
	}
	
	public float getNextRadius()
	{
		if(difficulty % 10 == 0)
		{
			return 400;
		}
		else if(difficulty < 10)
		{
			return Rand.nextInt(75, 150);
		}
		else if(difficulty < 20)
		{
			return Rand.nextInt(150, 225);
		}
		else if(difficulty < 30)
		{
			return Rand.nextInt(225, 300);
		}
		else if(difficulty < 40)
		{
			return Rand.nextInt(300, 375);
		}
		else
		{
			return Rand.nextInt(375, 450);
		}
	}
	
	private int getEnemyPower()
	{
		return (int) (difficulty * 1.25F + Math.round(difficulty / 10F)); 
	}
	
	private String getPauseButtonText(int buttonID)
	{
		if(buttonID == 0)
		{
			return "Resume";
		}
		else if(buttonID == 1)
		{
			return "Back To Menu";
		}
		throw new IllegalArgumentException(buttonID + " isn't a button");
	}
	
	/**
	 * Mouse press listener
	 */
	public void mouse(float x, float y, boolean pressed, int button)
	{
		if(baseGlobe.isDead())
		{
			if(!pressed)
			{
				game.setScreen(new MenuScreen(game));
			}
		}
		else if(paused)
		{
			if(!pressed)
			{
				for(int i = 0; i < pauseButtons.length; i++)
				{
					Rectangle r = pauseButtons[i];
					if(r.contains(x, y))
					{
						if(i == 0)
						{
							paused = false;
						}
						else if(i == 1)
						{
							game.setScreen(new MenuScreen(game));
						}
						break;
					}
				}
			}
		}
		else
		{
			if(!pressed && tutorial == 0)
			{
				if(button == MouseEvent.BUTTON1)
				{
					if(unitTab.heldSoldier != null)
					{
						Point2D p = new Point2D.Float(x, y);
						try
						{
							aft.inverseTransform(p, p);
						}
						catch (NoninvertibleTransformException e)
						{
							throw new RuntimeException(e);
						}
						Soldier s = unitTab.heldSoldier.createSoldier(this, baseGlobe);
						s.pos.set((float) p.getX(), (float) p.getY());
						baseGlobe.soldiers.add(s);
						unitTab.heldSoldier = null;
						
						return;
					}
					else if(!unitTab.isOpen() && x > Main.WIDTH - 400 && y > Main.HEIGHT - 50)
					{
						unitTab.toggle();
						return;
					}
					else if(unitTab.isOpen() && x > Main.WIDTH - 400)
					{
						if(y < 50)
						{
							unitTab.toggle();
						}
						else
						{
							unitTab.click(x - (Main.WIDTH - 400), y);
						}
						return;
					}
					else if(nextGlobe > 0 && x < 200 && y > Main.HEIGHT - 50)
					{
						if(nextGlobe > 200 && (baseGlobe.left == null || baseGlobe.left.soldiers.isEmpty()) && (baseGlobe.right == null || baseGlobe.right.soldiers.isEmpty()))
						{
							player.addResource(ResourceType.WOOD, (nextGlobe / 20) * baseGlobe.soldiers.size());
							player.addResource(ResourceType.LIVES, nextGlobe / 200);
							nextGlobe = 1;
						}
					}
				}
			}
			
			// Apply the aft transform to the base tower's bounds.
			if(baseGlobe.tower.getHealth() > 0 && tutorial == 0 && aft.createTransformedShape(baseGlobe.tower.bounds).contains(x, y))
			{
				// If the player presses the tower,
				// add stone resources.
				if(pressed && button == MouseEvent.BUTTON1)
				{
					player.addResource(ResourceType.STONE, 1);
				}
				// If the player right-clicks the tower and isn't
				// currently pressing, try and heal the tower.
				if(!pressed && button == MouseEvent.BUTTON3)
				{
					if(player.hasResource(ResourceType.STONE, 50) && baseGlobe.tower.getHealth() < baseGlobe.tower.maxHealth)
					{
						player.takeResource(ResourceType.STONE, 50);
						baseGlobe.tower.addHealth(50);
					}
				}
				return;
			}
			
			// Set values for moving the screen around.
			if(pressed && button == MouseEvent.BUTTON3)
			{
				lastPress.set(x, y);
			}
			else if(button == MouseEvent.BUTTON3)
			{
				lastPress.set(-1, -1);
			}
		}
	}

	/**
	 * Mouse move listener
	 */
	public void mouseMoved(float x, float y)
	{
		if(!paused)
		{
			unitTab.hoverOver = -1;
			hovX = hovY = Float.NaN;
			hovTower = null;
//			Shape s = aft.createTransformedShape(baseGlobe.tower.bounds);
			if(unitTab.isOpen() && x > Main.WIDTH - 400)
			{
				unitTab.hover(x - (Main.WIDTH - 400), y);
				return;
			}
			for(int i = 0; i < allGlobes.size(); i++)
			{
				Shape s = aft.createTransformedShape(allGlobes.get(i).tower.bounds);
				if(s.contains(x, y))
				{
					hovX = x;
					hovY = y;
					hovTower = allGlobes.get(i).tower;
					return;
				}
			}
			if(handler.isButton(MouseEvent.BUTTON3))
			{
				if(lastPress.x != -1 && lastPress.y != -1)
				{
					// Translate the screen the amount the mouse moved.
					move.subtractLocal(lastPress.subtract(x, y).divideLocal(actualZoom));
					lastPress.set(x, y);
	
					move.x = MathUtil.clamp(move.x, 500, -500);
					move.y = MathUtil.clamp(move.y, 350, -350);
				}
			}
		}
	}

	/**
	 * Mouse wheel listener
	 */
	public void mouseWheel(int amt)
	{
		// Zoom in and out the screen.
		if(!paused)
		{
			actualZoom = MathUtil.clamp(actualZoom * (1F - amt / 10F), 10, 0.1F);
		}
	}
	
	public void key(int keyCode, char keyChar, boolean pressed)
	{
		if(baseGlobe.isDead()) return;
		if(!pressed)
		{
			if(!paused)
			{
				if(keyCode == KeyEvent.VK_1)
				{
					unitTab.tryAndBuy(0);
				}
				else if(keyCode == KeyEvent.VK_2)
				{
					unitTab.tryAndBuy(1);
				}
				else if(keyCode == KeyEvent.VK_3)
				{
					unitTab.tryAndBuy(2);
				}
				else if(keyCode == KeyEvent.VK_SPACE && tutorial > 0)
				{
					tutorial--;
				}
				
				if(baseGlobe.left != null || baseGlobe.right != null)
				{
					Globe g = baseGlobe.left == null ? baseGlobe.right : baseGlobe.left;
					if(keyCode == KeyEvent.VK_8)
					{
						Soldier s = ((SoldierData) Rand.nextFrom(Soldier.USABLE_SOLDIERS)).createSoldier(this, g);
						s.pos.set(g.pos);
						g.soldiers.add(s);
					}
				}
			}
			if(keyCode == KeyEvent.VK_ESCAPE)
			{
				paused = !paused;
			}
		}
	}
}
