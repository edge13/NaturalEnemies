/*
 * Name of code: GameUI.java
 * Description: GameUI handles all of the in-game interface.  This includes unit information,
 * 				abilities, and minimap control.
 * Programmer Name: Joel Angelone
 * Date of last modification: 5/2/06
 */

import java.awt.*;
import java.awt.image.*;

public class GameUI
{
	// UI states
	public static final int NORMAL = 0;
	public static final int SELECTING = 1;
	public static final int CHOOSE_TARGET_ATTACK = 2;
	public static final int CHOOSE_DEST = 3;
	public static final int CHOOSE_TARGET_HEAL = 4;
	public static final int CHOOSE_TARGET_LIGHTNING = 5;
	
	// pointer to the parent GamePlay class
	private GamePlay gamePlay;
	
	// images
	private BufferedImageOp op;
	private BufferedImage menuBar;
	private BufferedImage infoBar;
	private BufferedImage cursor;
	private BufferedImage cursorSword;
	private BufferedImage crosshairs;
	private BufferedImage[] cursorMove;
	private Rectangle box;
	private Point boxOrigin;
	private int mode;
	
	private BufferedImage minimap;
	
	private BufferedImage wizardPortrait;
	private BufferedImage knightPortrait;
	private BufferedImage clericPortrait;
	private BufferedImage archerPortrait;
	private BufferedImage ninjaPortrait;
	
	private BufferedImage attackButton;
	private BufferedImage moveButton;
	private BufferedImage stopButton;
	private BufferedImage standgroundButton;
	private BufferedImage healButton;
	private BufferedImage lightningButton;
	
	private BufferedImage lightningCooldownButton;
	private BufferedImage healCooldownButton;
	
	private BufferedImage menuSelect;
	private BufferedImage message;
	
	private Rectangle attack;
	private Rectangle move;
	private Rectangle stop;
	private Rectangle standground;
	private Rectangle heal;
	private Rectangle lightning;
	
	private int healActive;
	private int lightningActive;

	private Font uiFont;
	
	// GameUI() - class constructor
	// Loads all graphics and initializes button positions.
	// Sets the pointer to the GamePlay class.
	public GameUI(GamePlay parent)
	{
		gamePlay = parent;
		menuBar = gamePlay.getImage("Graphics/Menu/menuBar.jpg");
		infoBar = gamePlay.getImage("Graphics/Menu/infoBar.jpg");
		cursor = gamePlay.getImage("Graphics/Misc/cursor.png");
		cursorSword = gamePlay.getImage("Graphics/Misc/cursorSword.png");
		crosshairs = gamePlay.getImage("Graphics/Misc/crosshairs.png");
		cursorMove = gamePlay.getImages("Graphics/Misc/mouseMove.png", 4, 1);
		message = gamePlay.getImage("Graphics/Misc/message.jpg");
		box = new Rectangle();
		boxOrigin = new Point();
		mode = NORMAL;
		
		minimap = gamePlay.getImage("Graphics/Menu/minimap.jpg");
		
		wizardPortrait = gamePlay.getImage("Graphics/Menu/wizardPortrait.png");
		knightPortrait = gamePlay.getImage("Graphics/Menu/knightPortrait.png");
		clericPortrait = gamePlay.getImage("Graphics/Menu/clericPortrait.png");
		archerPortrait = gamePlay.getImage("Graphics/Menu/archerPortrait.png");
		ninjaPortrait = gamePlay.getImage("Graphics/Menu/ninjaPortrait.png");
		
		attackButton = gamePlay.getImage("Graphics/Menu/attackButton.png");
		moveButton = gamePlay.getImage("Graphics/Menu/moveButton.png");
		stopButton = gamePlay.getImage("Graphics/Menu/stopButton.png");
		standgroundButton = gamePlay.getImage("Graphics/Menu/standgroundButton.png");
		healButton = gamePlay.getImage("Graphics/Menu/healButton.png");
		lightningButton = gamePlay.getImage("Graphics/Menu/lightningButton.png");
		
		healCooldownButton = gamePlay.getImage("Graphics/Menu/healCooldownButton.png");
		lightningCooldownButton = gamePlay.getImage("Graphics/Menu/lightningCooldownButton.png");
		
		menuSelect = gamePlay.getImage("Graphics/Menu/menuSelect.png");
		
		attack = new Rectangle(45, 680, 32, 32);
		move = new Rectangle(85, 680, 32, 32);
		stop = new Rectangle(125, 680, 32, 32);
		standground = new Rectangle(165, 680, 32, 32);
		heal = new Rectangle(45, 720, 32, 32);
		lightning = new Rectangle(85, 720, 32, 32);
		
		uiFont = new Font("Bradley Hand ITC", Font.BOLD, 22);
	}
	
	// render()
	// All UI-related rendering is done here.
	public void render(Graphics2D g, Point camera)
	{
		g.drawImage(menuBar, op, 0, 624);
		
		renderInfoBar(g);
		renderMenu(g);
		
		renderMinimap(g);
		
		if (mode == SELECTING)
			renderSelectionBox(g);
		
		else if (mode == CHOOSE_TARGET_ATTACK)
			g.drawImage(menuSelect, op, attack.x, attack.y);
		
		else if (mode == CHOOSE_DEST)
			g.drawImage(menuSelect, op, move.x, move.y);
		
		else if (mode == CHOOSE_TARGET_HEAL)
			g.drawImage(menuSelect, op, heal.x, heal.y);
		
		else if (mode == CHOOSE_TARGET_LIGHTNING)
			g.drawImage(menuSelect, op, lightning.x, lightning.y);
			
		renderCursor(g, camera);
	}
	
	// processInput()
	// Checks if the user selects and action.
	public void processInput(int mx, int my)
	{
		if (attack.inside(mx, my))
			mode = CHOOSE_TARGET_ATTACK;
		
		else if (move.inside(mx, my))
			mode = CHOOSE_DEST;
		
		else if (stop.inside(mx, my))
			gamePlay.orderUnits(Unit.STOP, mx, my, null);
		
		else if (standground.inside(mx, my))
			gamePlay.orderUnits(Unit.STAND_GROUND, mx, my, null);
		
		// special abilities must also be active
		else if (heal.inside(mx, my) && healActive == 2)
			mode = CHOOSE_TARGET_HEAL;
		
		else if (lightning.inside(mx, my) && lightningActive == 2)
			mode = CHOOSE_TARGET_LIGHTNING;
	}
	
	// renderCursor()
	// Renders the custom cursor graphics to the screen.
	public void renderCursor(Graphics2D g, Point camera)
	{
		Unit target = gamePlay.getTargetedEnemy(gamePlay.getMouseX(), gamePlay.getMouseY());
		
		// cursor is a sword when targeting an enemy
		if (target != null && mode == NORMAL && gamePlay.getMouseY() < 624)
		{
			int hp = target.getCurrentHp();
			int maxhp = target.getMaxHp();
			
			float ratio = ((float)hp / (float)maxhp) * 96;
			
			g.setColor(Color.BLACK);
			g.fillRect((int)target.sprite.getX() - camera.x, (int)target.sprite.getY() - camera.y, 96, 6);
			g.setColor(Color.GREEN);
			g.fillRect((int)target.sprite.getX() - camera.x + 1, (int)target.sprite.getY() - camera.y + 1, (int)(ratio - 2), 4);
			
			g.drawImage(cursorSword, op, gamePlay.getMouseX(), gamePlay.getMouseY());
		}
		
		// cursor is an arrow when the camera is moving
		else
		{
			if (gamePlay.getMouseY() < 10)
				g.drawImage(cursorMove[2], op, gamePlay.getMouseX(), gamePlay.getMouseY());
			else if (gamePlay.getMouseY() > 758)
				g.drawImage(cursorMove[0], op, gamePlay.getMouseX(), gamePlay.getMouseY() - 16);
			else if (gamePlay.getMouseX() < 10)
				g.drawImage(cursorMove[1], op, gamePlay.getMouseX(), gamePlay.getMouseY());
			else if (gamePlay.getMouseX() > 1014)
				g.drawImage(cursorMove[3], op, gamePlay.getMouseX() - 16, gamePlay.getMouseY());
			
			// cursor is a crosshair when performing an action
			else
			{
				if ((mode == CHOOSE_TARGET_ATTACK || mode == CHOOSE_TARGET_HEAL || mode == CHOOSE_TARGET_LIGHTNING)
					 && gamePlay.getMouseY() < 624)
					g.drawImage(crosshairs, op, gamePlay.getMouseX() - 11, gamePlay.getMouseY() - 11);
				
				// cursor looks normal otherwise
				else
					g.drawImage(cursor, op, gamePlay.getMouseX(), gamePlay.getMouseY());
			}
		}
	}
	
	// startSelecting()
	// Sets the starting coordinate for the unit selection box
	void startSelecting(int x, int y)
	{
		mode = SELECTING;
		boxOrigin.x = x;
		boxOrigin.y = y;
	}
	
	// renderSelectionBox()
	// Draws the unit selection box to the screen.
	void renderSelectionBox(Graphics2D g)
	{
		int mx = gamePlay.getMouseX();
		int my = gamePlay.getMouseY();
		
		if (mx < boxOrigin.x)
		{
			box.x = mx;
			box.width = boxOrigin.x - mx;
		}
		else
		{
			box.x = boxOrigin.x;
			box.width= mx - boxOrigin.x;
		}
		
		if (my < boxOrigin.y)
		{
			box.y = my;
			box.height = boxOrigin.y - my;
		}
		else
		{
			box.y = boxOrigin.y;
			box.height = my - boxOrigin.y;
		}
		
		g.setColor(Color.GREEN);
		g.drawRect(box.x, box.y, box.width, box.height);
	}
	
	// renderMenu()
	// Draws the bulk of the UI.  This includes unit information and abilities
	// for selected units.
	public void renderMenu(Graphics2D g)
	{
		int num = gamePlay.getNumSelected();
		
		// nothing to draw if no units are selected
		if (num == 0)
			return;
		
		// check for special abilities
		querySpecial();
		
		g.setFont(uiFont);
		g.setColor(Color.WHITE);
		
		// draw abilities
		g.drawString("Actions", 40, 665);
		g.drawImage(attackButton, op, attack.x, attack.y);
		g.drawImage(moveButton, op, move.x, move.y);
		g.drawImage(stopButton, op, stop.x, stop.y);
		g.drawImage(standgroundButton, op, standground.x, standground.y);
		if (healActive == 1)
			g.drawImage(healCooldownButton, op, heal.x, heal.y);
		else if (healActive == 2)
			g.drawImage(healButton, op, heal.x, heal.y);
		if (lightningActive == 1)
			g.drawImage(lightningCooldownButton, op, lightning.x, lightning.y);
		else if (lightningActive == 2)
			g.drawImage(lightningButton, op, lightning.x, lightning.y);

		// special case - only one unit selected
		// draw a portrait, healthbar, and statistics
		if (num == 1)
		{
			for (int i = 0; i < gamePlay.getNumFriendlyUnits(); i++)
			{
				Unit unit = gamePlay.friendlyUnits[i];
				if (unit.isSelected())
				{
					if (unit.isStandingGround())
						g.drawImage(menuSelect, op, standground.x, standground.y);
					else if (unit.getState() == Unit.ATTACK)
						g.drawImage(menuSelect, op, attack.x, attack.y);
					else if (unit.getState() == Unit.MOVE)
						g.drawImage(menuSelect, op, move.x, move.y);
					else if (unit.getState() == Unit.STOP)
						g.drawImage(menuSelect, op, stop.x, stop.y);
					
					if (unit.getType() == Unit.KNIGHT)
					{
						g.drawString("Knight", 370, 665);
						g.drawImage(knightPortrait, op, 380, 675);
					}
					
					else if (unit.getType() == Unit.WIZARD)
					{
						g.drawString("Wizard", 370, 665);								
						g.drawImage(wizardPortrait, op, 380, 675);
					}
					
					else if (unit.getType() == Unit.CLERIC)
					{
						g.drawString("Cleric", 380, 665);
						g.drawImage(clericPortrait, op, 380, 675);
					}
					
					else if (unit.getType() == Unit.ARCHER)
					{
						g.drawString("Archer", 380, 665);
						g.drawImage(archerPortrait, op, 380, 675);
					}
					
					else if (unit.getType() == Unit.NINJA)
					{
						g.drawString("Ninja", 380, 665);
						g.drawImage(ninjaPortrait, op, 380, 675);
					}
						
					String info = "Health:  ";
					info += String.valueOf(unit.getCurrentHp());
					info += " / ";
					info += String.valueOf(unit.getMaxHp());
					g.drawString(info, 470, 675);
					
					info = "Range:  ";
					info += unit.getRange();
					g.drawString(info, 470, 700);
					
					info = "Armor:  ";
					info += String.valueOf(unit.getArmor());
					g.drawString(info, 470, 725);
					
					info = "Power:  ";
					info += String.valueOf(unit.getPower());
					g.drawString(info, 470, 750);
							
					int hp = unit.getCurrentHp();
					int maxhp = unit.getMaxHp();					
					float ratio = ((float)hp / (float)maxhp) * 50;		
					g.setColor(Color.BLACK);
					g.fillRect(380, 737, 50, 6);
					g.setColor(Color.GREEN);
					g.fillRect(380, 737, (int) ratio, 6);
					
					return;
				}
			}
			
		}
		
		int totalCurrent = 0;
		int totalMax = 0;
		int selectedIndex = 0;
		
		// normal case - multiple units
		// just draw a portrait and health bar
		for (int i = 0; i < gamePlay.getNumFriendlyUnits(); i++)
		{
			Unit unit = gamePlay.friendlyUnits[i];
			
			if (unit.isSelected())
			{
				g.setColor(Color.WHITE);
				if (unit.getType() == Unit.KNIGHT)
					g.drawImage(knightPortrait, op, 310 + selectedIndex*60, 655);
				
				else if (unit.getType() == Unit.WIZARD)								
					g.drawImage(wizardPortrait, op, 310 + selectedIndex*60, 655);
				
				else if (unit.getType() == Unit.CLERIC)
					g.drawImage(clericPortrait, op, 310 + selectedIndex*60, 655);
				
				else if (unit.getType() == Unit.ARCHER)
					g.drawImage(archerPortrait, op, 310 + selectedIndex*60, 655);
				
				else if (unit.getType() == Unit.NINJA)
					g.drawImage(ninjaPortrait, op, 310 + selectedIndex*60, 655);
				
				int hp = unit.getCurrentHp();
				int maxhp = unit.getMaxHp();					
				float ratio = ((float)hp / (float)maxhp) * 50;		
				g.setColor(Color.BLACK);
				g.fillRect(310 + selectedIndex*60, 717, 50, 6);
				g.setColor(Color.GREEN);
				g.fillRect(310 + selectedIndex*60, 717, (int) ratio, 6);
				
				totalCurrent += hp;
				totalMax += maxhp;
				
				selectedIndex++;
			}
		}
		
		// calculate the average percent health of selected units and render it
		float ratio = (float)totalCurrent/(float)totalMax;
		ratio *= 100;
		String output = "Average Health: ";
		output += String.valueOf((int)ratio);
		output += "%";
		g.setColor(Color.WHITE);
		g.drawString(output, 290 + (selectedIndex-1)*20, 750);
	}
	
	// renderMinimap()
	// draws the minimap in the bottom right corner
	public void renderMinimap(Graphics2D g)
	{
		g.drawImage(minimap, op, 846, 646);
		g.setColor(Color.BLACK);
		
		// draw the fog on the minimap
		for (int x = 0; x < Map.SIZE/GamePlay.FOG_SIZE; x++)
		{
			for (int y = 0; y < Map.SIZE/GamePlay.FOG_SIZE; y++)
			{
				if (gamePlay.getFog(x, y) == 0)
				{
					g.fillRect(850 + x*3, 650 + y*3, 3, 3);
				}
			}
		}
		
		g.setColor(Color.RED);
		
		// draw enemy units
		for (int i = 0; i < gamePlay.getNumEnemyUnits(); i++)
		{
			Unit unit = gamePlay.enemyUnits[i];
			if (!unit.isAlive())
				continue;
			
			int xLoc = (int)((unit.sprite.getX() + Unit.SIZE/2)/GamePlay.FOG_SIZE);
			int yLoc = (int)((unit.sprite.getY() + Unit.SIZE/2)/GamePlay.FOG_SIZE);
			
			if (gamePlay.getFog(xLoc, yLoc) == 2)
				g.fillRect(850 + xLoc*3, 650 + yLoc*3, 3, 3);
		}
		
		g.setColor(Color.CYAN);
		
		// draw friendly units
		for (int i = 0; i < gamePlay.getNumFriendlyUnits(); i++)
		{
			Unit unit = gamePlay.friendlyUnits[i];
			if (!unit.isAlive())
				continue;
			
			int xLoc = (int)((unit.sprite.getX() + Unit.SIZE/2)/GamePlay.FOG_SIZE);
			int yLoc = (int)((unit.sprite.getY() + Unit.SIZE/2)/GamePlay.FOG_SIZE);
			
			g.fillRect(850 + xLoc*3, 650 + yLoc*3, 3, 3);
		}
		
		g.setColor(Color.WHITE);
		
		// draw the viewport box
		int camx = gamePlay.getCamX()/GamePlay.FOG_SIZE;
		int camy = gamePlay.getCamY()/GamePlay.FOG_SIZE;
		
		g.drawRect(850 + camx*3, 650 + camy*3, 32, 20);
	}
	
	// renderInfoBar()
	// Displays a small bar with score, cheat, and difficulty information at the
	// top of the screen.
	public void renderInfoBar(Graphics2D g)
	{
		g.drawImage(infoBar, op, 0, 0);
		g.setColor(Color.WHITE);
		g.setFont(uiFont);
		
		String score = "Score: ";
		score += String.valueOf(gamePlay.calcScore());
		g.drawString(score, 880, 20);
		
		if (gamePlay.getDifficulty() == GamePlay.EASY)
			g.drawString("Difficulty: Easy", 650, 20);
		else if (gamePlay.getDifficulty() == GamePlay.NORMAL)
			g.drawString("Difficulty: Medium", 650, 20);
		else if (gamePlay.getDifficulty() == GamePlay.HARD)
			g.drawString("Difficulty: Hard", 650, 20);
		
		if (gamePlay.isGodMode())
		{
			if (gamePlay.isOneHitKills())
				g.drawString("Cheats Enabled: God mode, One-hit Kills", 20, 20);
			else
				g.drawString("Cheats Enabled: God mode", 20, 20);
		}
		else if (gamePlay.isOneHitKills())
			g.drawString("Cheats Enabled: One hit kills", 20, 20);
		else
			g.drawString("Cheats Enabled: None", 20, 20);
	}
	
	// renderDefeat()
	// Displays a defeat message to the user.
	public void renderDefeat(Graphics2D g)
	{
		g.drawImage(message, op, 310, 205);
		g.setColor(Color.WHITE);
		g.setFont(uiFont);
		g.drawString("Defeated...", 445, 232);
		g.drawString("Your Score: " + String.valueOf(gamePlay.calcScore()), 405, 265);
		g.drawString("Press Enter to continue", 370, 308);
	}
	
	// renderVictory()
	// displays a victory message to the user.
	public void renderVictory(Graphics2D g)
	{
		g.drawImage(message, op, 310, 205);
		g.setColor(Color.WHITE);
		g.setFont(uiFont);
		g.drawString("Victory!", 445, 232);
		g.drawString("Your Score: " + String.valueOf(gamePlay.calcScore()), 405, 265);
		g.drawString("Press Enter to continue", 370, 308);
	}
	
	// querySpecial()
	// Determines if any selected units have a special ability, and sets the active flag
	// for that special ability.
	private void querySpecial()
	{
		healActive = 0;
		lightningActive = 0;
		
		for (int i = 0; i < gamePlay.getNumFriendlyUnits(); i++)
		{
			if (!gamePlay.friendlyUnits[i].isSelected())
				continue;
			
			if (gamePlay.friendlyUnits[i].getSpecialType() == Unit.HEAL)
			{
				healActive = 1;
				if (gamePlay.friendlyUnits[i].getCooldown() == 0)
					healActive = 2;
			}
			
			if (gamePlay.friendlyUnits[i].getSpecialType() == Unit.LIGHTNING)
			{
				lightningActive = 1;
				if (gamePlay.friendlyUnits[i].getCooldown() == 0)
					lightningActive = 2;
			}
		}
	}
	
	// resetMode()
	// Sets the mode to normal.
	public void resetMode()
	{
		mode = NORMAL;
	}
	
	// ******
	// Getters
	// ******
	
	public int getMode()
	{
		return mode;
	}
	
	public Rectangle getRect()
	{
		return box;
	}
}