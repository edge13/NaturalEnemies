/*
 * Name of code: GamePlay.java
 * Description: GamePlay is the core class of the game.  An instance of this
 * 				class is created each time a new level is loaded.  GamePlay
 * 				controls all objects and performs the primary render and update
 * 				functions each loop cycle.
 * Programmer Name: Joel Angelone
 * Date of last modification: 5/8/06
 */

import java.awt.Graphics2D;
import java.awt.image.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import com.golden.gamedev.GameEngine;
import com.golden.gamedev.GameObject;
import java.awt.Point;
import com.golden.gamedev.object.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class GamePlay extends GameObject
{
	// array limits
	public static final int MAX_UNITS = 50;
	public static final int MAX_POWERUPS = 10;
	public static final int FOG_SIZE = 96;
	
	// difficulty levels
	public static final int EASY = 0;
	public static final int NORMAL = 1;
	public static final int HARD = 2;
	
	// game states
	public static final int PLAYING = 0;
	public static final int VICTORY = 1;
	public static final int DEFEAT = 2;
	
	// member variables
	private RTS		rtsPointer;
	private GameUI	gameUI;
	private Map		map;
	private Point	camera;
	private int		fog[][];
	
	private PauseMenu pauseMenu;
	
	private CollisionManager unitCollision;
	private CollisionManager objectCollision;
	private CollisionManager powerupCollision;
	private SpriteGroup unitGroup;
	private SpriteGroup powerupGroup;
	public Powerup powerups[];
	public Unit friendlyUnits[];
	public Unit enemyUnits[];
	
	private int numFriendlyUnits;
	private int numEnemyUnits;
	private int numSelected;
	private int numPowerups;
	
	// fog of war graphics
	private BufferedImage fogThick;
	private BufferedImage fogThin;
	private BufferedImageOp op;
	
	private int status;
	private int score;
	private int difficulty;
	private boolean godMode;
	private boolean oneHitKills;
	private boolean paused;
	private int level;
	
	// GamePlay() - class constructor
	// Initializes the GameObject and sets the pointer to the GameEngine parent (RTS).
	public GamePlay(GameEngine parent, RTS rts)
	{
		super(parent);
		rtsPointer = rts;
	}
	
	// initResources()
	// Initializes all the objects present in a single level of the game - the UI,
	// camera, menus, units, fog of war, powerups, and map.  All collision detection is
	// initialized here.
	public void initResources()
	{
		// object initialization
		gameUI = new GameUI(this);
		pauseMenu = new PauseMenu(this);
		camera = new Point(1050, 2300);
		status = PLAYING;
		numSelected = 0;
		
		// default values for member variables
		score = 0;
		difficulty = EASY;
		godMode = false;
		oneHitKills = false;
		paused = false;
		
		// allocate memory for objects and fog of war information
		friendlyUnits = new Unit[MAX_UNITS];
		enemyUnits = new Unit[MAX_UNITS];
		powerups = new Powerup[MAX_POWERUPS];
		fog = new int[Map.SIZE/FOG_SIZE][Map.SIZE/FOG_SIZE];
		fogThick = getImage("Graphics/Environment/fogThick.png");
		fogThin = getImage("Graphics/Environment/fogThin.png");
		
		// create groups for sprites
		unitGroup = new SpriteGroup("Unit Group");
		powerupGroup = new SpriteGroup("Powerup Group");
		
		// load whichever level the parent class is holding
		try { loadFromFile(rtsPointer.gameFile); }	
		catch (FileNotFoundException notFound) { }	
		catch (IOException io) { }
		
		// add each unit and powerup to their corresponding sprite group
		for (int i = 0; i < numFriendlyUnits; i++)
			unitGroup.add(friendlyUnits[i].sprite);
		for (int i = 0; i < numEnemyUnits; i++)
			unitGroup.add(enemyUnits[i].sprite);
		for (int i = 0; i <numPowerups; i++)
			powerupGroup.add(powerups[i].sprite);

		// initialize collision detection
		unitCollision = new UnitCollision(this);
		unitCollision.setCollisionGroup(unitGroup, unitGroup);
		objectCollision = new ObjectCollision(this);
		objectCollision.setCollisionGroup(unitGroup, map.objectGroup);
		powerupCollision = new PowerupCollision(this);
		powerupCollision.setCollisionGroup(unitGroup, powerupGroup);
		
		// set music volume to half of the default value
		bsMusic.setVolume(0.5f);
		
		// set the music and starting camera location based on the level
		if (level == 1)
		{
			playMusic("Sounds/Music/levelOne.mid");
		}
		else if (level == 2)
		{
			camera.x = 1200;
			camera.y = 1800;
			playMusic("Sounds/Music/levelTwo.mid");
		}
		else if (level == 3)
		{
			camera.x = 0;
			camera.y = 900;
			playMusic("Sounds/Music/levelThree.mid");
		}
	}
	
	// render()
	// Draws everything to the screen each update cycle.
	public void render(Graphics2D g)
	{	
		map.render(g, camera);
		
		for (int i = 0; i < numPowerups; i++)
			powerups[i].render(g, camera);
		
		// render dead units first so they don't appear on top of living units
		for (int i = 0; i < numFriendlyUnits; i++)
			if (!friendlyUnits[i].isAlive())
				friendlyUnits[i].render(g, camera);
		
		for (int i = 0; i < numEnemyUnits; i++)
			if (!enemyUnits[i].isAlive())
				enemyUnits[i].render(g, camera);
		
		for (int i = 0; i < numFriendlyUnits; i++)
			if (friendlyUnits[i].isAlive())
				friendlyUnits[i].render(g, camera);
		
		// only render enemy units that aren't covered by fog of war
		for (int i = 0; i < numEnemyUnits; i++)
			if (enemyUnits[i].isAlive())
			{
				int xLoc = (int)((enemyUnits[i].sprite.getX() + Unit.SIZE/2)/FOG_SIZE);
				int yLoc = (int)((enemyUnits[i].sprite.getY() + Unit.SIZE/2)/FOG_SIZE);
				
				if (fog[xLoc][yLoc] == 2)
					enemyUnits[i].render(g, camera);
			}
		
		renderFog(g);
		
		gameUI.render(g, camera);
		
		if (paused)
			pauseMenu.render(g);
		
		if (status == DEFEAT)
			gameUI.renderDefeat(g);
		
		else if (status == VICTORY)
			gameUI.renderVictory(g);
	}
	
	// update()
	// Performs game logic each time through the game loop
	public void update(long elapsedTime)
	{
		// if the game is paused, only update the pause menu
		if (paused)
		{
			pauseMenu.update(elapsedTime);
			return;
		}
		
		// if the player has won, we're done updating
		if (status == VICTORY)
		{
			// wait for the player to press enter
			if (keyDown(java.awt.event.KeyEvent.VK_ENTER))
			{
				// load the next level
				if (level == 1)
				{
					parent.nextGameID = RTS.GAME_LOAD;
					if (difficulty == EASY)
						rtsPointer.gameFile = "Data/levelTwoEasy.txt";
					else if (difficulty == NORMAL)
						rtsPointer.gameFile = "Data/levelTwoNormal.txt";
					else
						rtsPointer.gameFile = "Data/levelTwoHard.txt";
				}
				else if (level == 2)
				{
					parent.nextGameID = RTS.GAME_LOAD;
					if (difficulty == EASY)
						rtsPointer.gameFile = "Data/levelThreeEasy.txt";
					else if (difficulty == NORMAL)
						rtsPointer.gameFile = "Data/levelThreeNormal.txt";
					else
						rtsPointer.gameFile = "Data/levelThreeHard.txt";
				}
				else if (level == 3) 
				{
					parent.nextGameID = RTS.GAME_CREDITS;
				}
				finish();
			}
			return;
		}
		
		// quit to the start menu if the player is defeated
		else if (status == DEFEAT)
		{
			if (keyDown(java.awt.event.KeyEvent.VK_ENTER))
			{
				parent.nextGameID = RTS.GAME_MENU;
				finish();
			}
			return;
		}
		
		processInput();
		
		if (status != PLAYING)
			return;
		
		// check for victory and defeat
		updateStatus();
		
		// sprite update routines
		for (int i = 0; i < numPowerups; i++)
			powerups[i].update(elapsedTime);
		for (int i = 0; i < numFriendlyUnits; i++)
			friendlyUnits[i].update(elapsedTime);
		for (int i = 0; i < numEnemyUnits; i++)
			enemyUnits[i].update(elapsedTime);
		
		unitCollision.checkCollision();
		objectCollision.checkCollision();
		powerupCollision.checkCollision();
		
		updateFog();
	}
	
	// processInput()
	// Checks the input from the user and performs the appropriate operations.
	public void processInput()
	{
		// store the mouse coordinates for convenience
		int mx = getMouseX();
		int my = getMouseY();
		
		// move the camera if the mouse is on the edge of the screen, or an arrow
		// key is pressed
		if (gameUI.getMode() != GameUI.SELECTING)
		{
			if (mx >= 1014 || keyDown(KeyEvent.VK_RIGHT)) moveCamera(25, 0);
			if (my >= 758 || keyDown(KeyEvent.VK_DOWN)) moveCamera(0, 25);
			if (mx <= 10 || keyDown(KeyEvent.VK_LEFT)) moveCamera(-25, 0);
			if (my <= 10 || keyDown(KeyEvent.VK_UP)) moveCamera(0, -25);
		}
		
		if (click())
		{
			// clicks on the bottom of the screen are handled by the UI
			if (my > 624)
				gameUI.processInput(mx, my);
		
			// if the current command is attack, check to see if the click is on an enemy unit
			else if (gameUI.getMode() == GameUI.CHOOSE_TARGET_ATTACK)
			{
				Unit target = getTargetedEnemy(mx, my);
				
				// if so, attack that unit
				if (target != null)
					orderUnits(Unit.ATTACK, mx, my, target);
				// otherwise, attack-move to the location of the click
				else
					orderUnits(Unit.ATTACK_MOVE, mx - Unit.SIZE/2 + camera.x, my - Unit.SIZE + 16 + camera.y, null);
		
				gameUI.resetMode();
			}
			
			// if the current command is move, move to the clicked destination
			else if (gameUI.getMode() == GameUI.CHOOSE_DEST)
			{
				orderUnits(Unit.MOVE, mx - Unit.SIZE/2 + camera.x, my - Unit.SIZE + 16 + camera.y, null);
				gameUI.resetMode();
			}
			
			// if the current command is heal, then perform the heal special on the targeted friendly
			else if (gameUI.getMode() == GameUI.CHOOSE_TARGET_HEAL)
			{
				Unit target = getTargetedFriendly(mx, my);
				if (target != null)
				{
					orderUnits(Unit.HEAL, mx, my, target);
					gameUI.resetMode();
				}
			}
			
			// if the command is lighting, do the lightning special on the targeted enemy
			else if (gameUI.getMode() == GameUI.CHOOSE_TARGET_LIGHTNING)
			{
				Unit target = getTargetedEnemy(mx, my);
				if (target != null)
				{
					orderUnits(Unit.LIGHTNING, mx, my, target);
					gameUI.resetMode();
				}
			}
			
			// if we weren't in a command mode previously, a click means start drawing a
			// selection box
			else
			{
				gameUI.startSelecting(mx, my);
			}	
		}
		
		if (rightClick())
		{
			// right click commands in this area are minimap commands
			if (my > 624)
			{
				// order units to move to the corresponding area of the map
				if (mx >= 850 && mx <= 946 && my >= 650 && my <= 746)
				{
					int xd = (mx - 850) / 3;
					int yd = (my - 650) / 3;
					
					xd *= FOG_SIZE;
					yd *= FOG_SIZE;
					
					orderUnits(Unit.MOVE, xd, yd, null);
				}
				else
				{
					gameUI.resetMode();
				}
				
			}
			
			// otherwise we either attack or move, depending on if an enemy is present at the
			// click location
			else if (gameUI.getMode() == GameUI.NORMAL)
			{
				Unit target = getTargetedEnemy(mx, my);
				if (target != null)
					orderUnits(Unit.ATTACK, mx, my, target);
				else
					orderUnits(Unit.MOVE, mx - Unit.SIZE/2 + camera.x, my - Unit.SIZE + 16 + camera.y, null);
			}
			
			else
				gameUI.resetMode();
		}
		
		// if we're selecting and release the mouse, stop drawing and run the selection algorithm
		if (gameUI.getMode() == GameUI.SELECTING)
		{
			if (!bsInput.isMouseDown(MouseEvent.BUTTON1))
			{
				selectUnits(mx, my);
				gameUI.resetMode();
			}
		}
		
		// the player can click or click-and-drag the minimap to move the camera
		if (bsInput.isMouseDown(MouseEvent.BUTTON1))
		{
			if (mx >= 850 && mx <= 950 && my >= 650 && my <= 750)
			{
				int xpos = (mx - 850)/3;
				int ypos = (my - 650)/3;
				
				moveCameraTo(xpos*FOG_SIZE - 512, ypos*FOG_SIZE - 312);
			}
		}
		
		if (keyDown(KeyEvent.VK_ESCAPE))
    		paused = true;
	}
	
	// updateStatus()
	// checks for victory and defeat and sets the appropriate flags
	public void updateStatus()
	{	
		// normally we just check for any remaining enemies
		if (level != 3)
		{
			boolean victory = true;
			boolean defeat = true;
			
			// no remaining enemies means victory
			for (int i = 0; i < numEnemyUnits; i++)
				if (enemyUnits[i].isAlive())
				{
					victory = false;
					break;
				}
			
			if (victory)
			{
				status = VICTORY;
				playMusic("Sounds/Music/victory.mid");
				return;
			}
			
			// no remaining friendlies means defeat
			for (int i = 0; i < numFriendlyUnits; i++)
				if (friendlyUnits[i].isAlive())
				{
					defeat = false;
					break;
				}
			
			if (defeat)
			{
				status = DEFEAT;
				playMusic("Sounds/Music/defeat.mid");
				return;
			}
		}
		
		// level 3 has special conditions
		else if (level == 3)
		{
			boolean victory = true;
			boolean defeat = true;
			
			// if the commander is dead, victory
			if (enemyUnits[0].isAlive())
				victory = false;
			
			if (victory)
			{
				status = VICTORY;
				playMusic("Sounds/Music/victory.mid");
				return;
			}
			
			// normal defeat conditions
			for (int i = 0; i < numFriendlyUnits; i++)
				if (friendlyUnits[i].isAlive())
				{
					defeat = false;
					break;
				}
			
			if (defeat)
			{
				status = DEFEAT;
				playMusic("Sounds/Music/defeat.mid");
				return;
			}
		}
	}
	
	// moveCamera()
	// moves the camera by the number of pixels specified by dx and dy
	public void moveCamera(int dx, int dy)
	{	
		camera.x += dx;
		camera.y += dy;
		
		// boundary check
		if (camera.x < 0) camera.x= 0;
		if (camera.y < 0) camera.y = 0;
		
		if (camera.x + 1024 > Map.SIZE) camera.x = Map.SIZE - 1024;
		if (camera.y + 624 > Map.SIZE) camera.y = Map.SIZE - 624;
	}
	
	
	// moveCameraTo()
	// moves the camera to the x,y coordinate specified by x and y
	public void moveCameraTo(int x, int y)
	{
		camera.x = x;
		camera.y = y;
	
		// boundary check
		if (camera.x < 0) camera.x= 0;
		if (camera.y < 0) camera.y = 0;
		
		if (camera.x + 1024 > Map.SIZE) camera.x = Map.SIZE - 1024;
		if (camera.y + 624 > Map.SIZE) camera.y = Map.SIZE - 624;
	}

	// getTargetedEnemy()
	// returns a pointer to the enemy located at the mouse x,y position
	// returns null if no enemies are there
	public Unit getTargetedEnemy(int x, int y)
	{
		for (int i = 0; i < numEnemyUnits; i++)
		{
			if (!enemyUnits[i].isAlive())
				continue;
				
			int ux = (int) enemyUnits[i].sprite.getX();
			int uy = (int) enemyUnits[i].sprite.getY();
					
			if (x >= (ux - camera.x) && x <= (ux + Unit.SIZE - camera.x) &&
				y >= (uy - camera.y) && y <= (uy + Unit.SIZE - camera.y))
			{
			
				int xLoc = (int)((ux + Unit.SIZE/2)/FOG_SIZE);
				int yLoc = (int)((uy + Unit.SIZE/2)/FOG_SIZE);
			
				// can't target enemies covered by fog of war
				if (fog[xLoc][yLoc] == 2)
					return enemyUnits[i];
			}
		}
		
		return null;
	}
	
	// getTargetedFriendly
	// returns a pointer to the friendly unit located at the mouse x,y position
	// returns null if no friendly units are there
	public Unit getTargetedFriendly(int x, int y)
	{
		for (int i = 0; i < numFriendlyUnits; i++)
		{
			if (!friendlyUnits[i].isAlive())
				continue;
				
			int ux = (int) friendlyUnits[i].sprite.getX();
			int uy = (int) friendlyUnits[i].sprite.getY();
					
			if (x >= (ux - camera.x) && x <= (ux + Unit.SIZE - camera.x) &&
				y >= (uy - camera.y) && y <= (uy + Unit.SIZE - camera.y))
			{
				return friendlyUnits[i];
			}
		}
		
		return null;
	}
	
	// selectUnits()
	// flags all units in the current selection box as selected, and resets the flag
	// on all other units.
	public void selectUnits(int x, int y)
	{
		numSelected = 0;
		
		for (int i = 0; i < numFriendlyUnits; i++)
		{
			if (!friendlyUnits[i].isAlive())
			{
				friendlyUnits[i].unSelect();
				continue;
			}
			
			int ux = (int)friendlyUnits[i].sprite.getX();
			int uy = (int)friendlyUnits[i].sprite.getY();
		
			if (gameUI.getRect().inside(ux + Unit.SIZE/2 - camera.x, uy + Unit.SIZE/2 - camera.y))
			{
				if (numSelected < 8)
				{
					friendlyUnits[i].select();
					numSelected++;
				}
			}
			
			else if (x > (ux - camera.x) && x < (ux + Unit.SIZE - camera.x) && 
					 y > (uy - camera.y) && y < (uy + Unit.SIZE - camera.y) && numSelected < 8)
			{
				if (numSelected < 8)
				{
					friendlyUnits[i].select();
					numSelected++;
				}
			}
			
			else
			{
				friendlyUnits[i].unSelect();
			}
		}
	}
	
	// orderUnits()
	// orders all selected units to perform an action specified by the command parameter
	public void orderUnits(int command, int x, int y, Unit target)
	{
		int selectedIndex = 0;
		
		for (int i = 0; i < numFriendlyUnits; i++)
		{
			if (friendlyUnits[i].isSelected())
			{
				// dont order dead units
				if (!friendlyUnits[i].isAlive())
					continue;
				
				// stop the current action
				friendlyUnits[i].reset();
				
				switch (command)
				{
					case Unit.MOVE:
						if (selectedIndex < 4)
							friendlyUnits[i].move(x + selectedIndex*100, y);
						else
							friendlyUnits[i].move(x + (selectedIndex-4)*100, y + 100);
					break;
					
					case Unit.ATTACK:
						friendlyUnits[i].attack(target);
					break;
					
					case Unit.ATTACK_MOVE:
						if (selectedIndex < 4)
							friendlyUnits[i].attackMove(x + selectedIndex*100, y);
						else
							friendlyUnits[i].attackMove(x + (selectedIndex-4)*100, y + 100);
					break;
					
					case Unit.STOP:
						friendlyUnits[i].stop();
					break;
					
					case Unit.STAND_GROUND:
						friendlyUnits[i].standGround();
					break;
					
					case Unit.HEAL:
						if (friendlyUnits[i].getSpecialType() == Unit.HEAL && friendlyUnits[i].getCooldown() == 0)
							friendlyUnits[i].heal(target);
					break;
					
					case Unit.LIGHTNING:
						if (friendlyUnits[i].getSpecialType() == Unit.LIGHTNING && friendlyUnits[i].getCooldown() == 0)
							friendlyUnits[i].lightning(target);
					break;
				}
				
				selectedIndex++;
			}
		}
	}
	
	// renderFog()
	// draws the fog of war to the screen
	public void renderFog(Graphics2D g)
	{	
		for (int x = 0; x < Map.SIZE / FOG_SIZE; x++)
		{
			for (int y = 0; y < Map.SIZE / FOG_SIZE; y++)
			{
				if (fog[x][y] == 0)
					g.drawImage(fogThick, op, x*FOG_SIZE - 6 - camera.x, y*FOG_SIZE - 6 - camera.y);

				else if (fog[x][y] == 1)
					g.drawImage(fogThin, op, x*FOG_SIZE - 6 - camera.x, y*FOG_SIZE - 6 - camera.y);
			}
		}
	}
	
	// updateFog()
	// updates each tile of the fog of war array
	public void updateFog()
	{
		for (int x = 0; x < Map.SIZE / FOG_SIZE; x++)
		{
			for (int y = 0; y < Map.SIZE / FOG_SIZE; y++)
			{
				if (fog[x][y] == 2)
					fog [x][y] = 1;
				
				for (int i = 0; i < numFriendlyUnits; i++)
				{
					// dead units don't reveal anything
					if (!friendlyUnits[i].isAlive())
						continue;
					
					int xLoc = x*FOG_SIZE + FOG_SIZE/2;
					int yLoc = y*FOG_SIZE + FOG_SIZE/2;
					
					if (friendlyUnits[i].distanceFromPoint(xLoc, yLoc) < Unit.SIGHT_RADIUS + 100)
					{
						fog[x][y] = 2;
						break;
					}
				}
			}
		}
	}
	
	// loadFromFile()
	// Loads a game file. this could be a default file (new game) or a saved game file. 
	public void loadFromFile(String file) throws FileNotFoundException, IOException
	{
		BufferedReader infile = new BufferedReader(new FileReader(file));
		
		map = new Map(this, infile.readLine());
		level = Integer.parseInt(infile.readLine());
		difficulty = Integer.parseInt(infile.readLine());
		numFriendlyUnits = Integer.parseInt(infile.readLine());
		numEnemyUnits = Integer.parseInt(infile.readLine());
		numPowerups = Integer.parseInt(infile.readLine());
		
		for (int i = 0; i < numPowerups; i++)
		{
			infile.readLine();
			powerups[i] = new Powerup(this, i, Integer.parseInt(infile.readLine()));
			powerups[i].sprite.setX(Integer.parseInt(infile.readLine()));
			powerups[i].sprite.setY(Integer.parseInt(infile.readLine()));
		}
		
		for (int i = 0; i < numFriendlyUnits; i++)
		{
			infile.readLine();
			friendlyUnits[i] = new Unit(this, i, Integer.parseInt(infile.readLine()));
			friendlyUnits[i].setCurrentHp(Integer.parseInt(infile.readLine()));
			friendlyUnits[i].sprite.setX(Integer.parseInt(infile.readLine()));
			friendlyUnits[i].sprite.setY(Integer.parseInt(infile.readLine()));
		}
		
		for (int i = 0; i < numEnemyUnits; i++)
		{
			infile.readLine();
			enemyUnits[i] = new Unit(this, -i - 1, Integer.parseInt(infile.readLine()));
			enemyUnits[i].setCurrentHp(Integer.parseInt(infile.readLine()));
			enemyUnits[i].sprite.setX(Integer.parseInt(infile.readLine()));
			enemyUnits[i].sprite.setY(Integer.parseInt(infile.readLine()));
		}
		
		for (int i = 0; i < Map.SIZE/FOG_SIZE; i++)
		{
			for (int j = 0; j < Map.SIZE/FOG_SIZE; j++)
			{
				fog[i][j] = Integer.parseInt(infile.readLine());
			}
		}
		
		infile.close();
	}
	
	// save()
	// saves all relavent data to the specified save slot
	public void save(int slot) throws IOException
	{
		BufferedWriter outfile;
		if (slot == 1)
			outfile = new BufferedWriter(new FileWriter("Save/slotOne.txt"));
		else if (slot == 2)
			outfile = new BufferedWriter(new FileWriter("Save/slotTwo.txt"));
		else
			outfile = new BufferedWriter(new FileWriter("Save/slotThree.txt"));
		
		if (level == 1)
		{
			outfile.write("Data/mapOne.txt");
			outfile.write('\n');
			outfile.write("1");
			outfile.write('\n');
		}
		
		else if (level == 2)
		{
			outfile.write("Data/mapTwo.txt");
			outfile.write('\n');
			outfile.write("2");
			outfile.write('\n');
		}
		
		else
		{
			outfile.write("Data/mapThree.txt");
			outfile.write('\n');
			outfile.write("3");
			outfile.write('\n');
		}
	
		outfile.write(String.valueOf(difficulty));
		outfile.write('\n');
		outfile.write(String.valueOf(getRemainingFriendlyUnits()));
		outfile.write('\n');
		outfile.write(String.valueOf(getRemainingEnemyUnits()));
		outfile.write('\n');
		outfile.write(String.valueOf(getRemainingPowerups()));
		outfile.write('\n');
		
		for (int i = 0; i < numPowerups; i++)
		{
			if (!powerups[i].isAlive())
				continue;
			
			outfile.write("[Powerup]");
			outfile.write('\n');
			outfile.write(String.valueOf(powerups[i].getType()));
			outfile.write('\n');
			outfile.write(String.valueOf((int)powerups[i].sprite.getX()));
			outfile.write('\n');
			outfile.write(String.valueOf((int)powerups[i].sprite.getY()));
			outfile.write('\n');
		}
		
		for (int i = 0; i < numFriendlyUnits; i++)
		{
			if (!friendlyUnits[i].isAlive())
				continue;
			
			outfile.write("[Unit]");
			outfile.write('\n');
			outfile.write(String.valueOf(friendlyUnits[i].getType()));
			outfile.write('\n');
			outfile.write(String.valueOf(friendlyUnits[i].getCurrentHp()));
			outfile.write('\n');
			outfile.write(String.valueOf((int)friendlyUnits[i].sprite.getX()));
			outfile.write('\n');
			outfile.write(String.valueOf((int)friendlyUnits[i].sprite.getY()));
			outfile.write('\n');
		}
		
		for (int i = 0; i < numEnemyUnits; i++)
		{
			if (!enemyUnits[i].isAlive())
				continue;
			
			outfile.write("[Enemy]");
			outfile.write('\n');
			outfile.write(String.valueOf(enemyUnits[i].getType()));
			outfile.write('\n');
			outfile.write(String.valueOf(enemyUnits[i].getCurrentHp()));
			outfile.write('\n');
			outfile.write(String.valueOf((int)enemyUnits[i].sprite.getX()));
			outfile.write('\n');
			outfile.write(String.valueOf((int)enemyUnits[i].sprite.getY()));
			outfile.write('\n');
		}
		
		for (int i = 0; i < Map.SIZE/FOG_SIZE; i++)
		{
			for (int j = 0; j < Map.SIZE/FOG_SIZE; j++)
			{
				outfile.write(String.valueOf(fog[i][j]));
				outfile.write('\n');
			}
		}
		
		outfile.close();
	}
	
	// getFog()
	// gets the fog of war information at the x,y coordinate specified
	public int getFog(int x, int y)
	{
		if (x < 0 || x > Map.SIZE/FOG_SIZE)
			return -1;
		if (y < 0 || y > Map.SIZE/FOG_SIZE)
			return -1;
		
		return fog[x][y];
	}
	
	// calcScore()
	// calculates and returns the current score
	public int calcScore()
	{
		score = 0;
		
		// bonus for dead enemies
		for (int i = 0; i < numEnemyUnits; i++)
		{
			if (!enemyUnits[i].isAlive())
				score += 100;
		}
		
		// bonus for living friendlies
		for (int i = 0; i < numFriendlyUnits; i++)
		{	
			if (friendlyUnits[i].isAlive())
				score += 50;
		}
		
		return score;
	}
	
	// enableGodMode()
	// turns on god mode
	public void enableGodMode()
	{
		godMode = true;
	}
	
	// enableOneHitKills()
	// turns on one-hit-kills
	public void enableOneHitKills()
	{
		oneHitKills = true;
	}
	
	// cheatsOff()
	// turns off all cheats
	public void cheatsOff()
	{
		godMode = false;
		oneHitKills = false;
	}
	
	// unSelect()
	// When a unit dies it calls this function to reduce the total number of selected units.
	// This prevents the UI from showing dead units as selected.
	public void unSelect()
	{
		if (numSelected > 0)
			numSelected--;
	}
	
	// unPause()
	// Returns to the game from the pause menu.
	public void unPause()
	{
		paused = false;
	}
	
	// getRemainingPowerups()
	// returns the number of powerups that haven't been used up.
	public int getRemainingPowerups()
	{
		int numLeft = 0;
		
		for (int i = 0; i < numPowerups; i++)
		{
			if (powerups[i].isAlive())
				numLeft++;
		}
		
		return numLeft;
	}
	
	// getRemainingFriendlyUnits()
	// returns the number of alive friendly units.
	public int getRemainingFriendlyUnits()
	{
		int numLeft = 0;
		
		for (int i = 0; i < numFriendlyUnits; i++)
		{
			if (friendlyUnits[i].isAlive())
				numLeft++;
		}
		
		return numLeft;	
	}
	
	// getRemainingEnemyUnits()
	// returns the number of alive enemy units.
	public int getRemainingEnemyUnits()
	{
		int numLeft = 0;
		
		for (int i = 0; i < numEnemyUnits; i++)
		{
			if (enemyUnits[i].isAlive())
				numLeft++;
		}
		
		return numLeft;
	}
	
	// ******
	// Getter functions
	// ******
	
	public boolean isGodMode()
	{
		return godMode;
	}
	
	public boolean isOneHitKills()
	{
		return oneHitKills;
	}
	
	public int getDifficulty()
	{
		return difficulty;
	}
	
	public int getCamX()
	{
		return camera.x;
	}
	
	public int getCamY()
	{
		return camera.y;
	}
	
	public int getNumSelected()
	{
		return numSelected;
	}
	
	public int getNumFriendlyUnits()
	{
		return numFriendlyUnits;
	}
	
	public int getNumEnemyUnits()
	{
		return numEnemyUnits;
	}
	
	public Map getMap()
	{
		return map;
	}
}