/*Name of code: StartMenu.java
 * Description: StartMenu.java is a GUI for setting game settings, starting new games,
 * 				starting previously saved games, or exiting the game
 * Programmer Name: Adam Riha
 */

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import com.golden.gamedev.*;

public class StartMenu extends GameObject
{
	//width and length of button images
	private static final int buttonlengthx = 170;
	private static final int buttonlengthy = 51;
	
	//constant positions of buttons
	private static final int buttonx = 430;
	private static final int buttony2 = 520;
	private static final int buttony3 = 580;
	private static final int buttony4 = 640;
	private static final int buttony5 = 700;
	
	private BufferedImage title;	//title background
	private BufferedImage instructionscreen;	//in game instruction image
	private BufferedImage cursor;	//cursor graphic
	private BufferedImageOp op;		//buffered image filter
	
	//button images
	private BufferedImage newGame;
	private BufferedImage loadGame;
	private BufferedImage instructions;
	private BufferedImage exit;
	private BufferedImage easy;
	private BufferedImage medium;
	private BufferedImage hard;
	private BufferedImage load1;
	private BufferedImage load2;
	private BufferedImage load3;
	private BufferedImage returnbutton;
	
	//button rectangles.  Used to determine when a button is clicked
	private Rectangle newRect;
	private Rectangle loadRect;
	private Rectangle instructionRect;
	private Rectangle exitRect;
	private Rectangle easyRect;
	private Rectangle mediumRect;
	private Rectangle hardRect;
	private Rectangle load1Rect;
	private Rectangle load2Rect;
	private Rectangle load3Rect;
	private Rectangle loadReturnRect;
	private Rectangle returnRect;
	
	//Boolean variables to determine menu's current state
	private boolean showDifficultyMenu = false;
	private boolean showInstructions = false;
	private boolean showLoadMenu = false;
	
	//used to call main class's functions
	private RTS rtsPointer;
	
	/*
	 * Name: StartMenu(GameEngine, RTS)
	 * Description: Constructor for start menu
	 * Arguments:	parent(GameEngine), rts(RTS)
	 * Variables: 	parent: calling function (GameEngine)
	 * 				rtsPointer: points to calling function as well
	 * Input/Output: None
	 * Files Accessed: None
	 * Files Changed: None
	 */
	public StartMenu(GameEngine parent, RTS rts)
	{
		super(parent);
		rtsPointer = rts;
	}
	
	/*
	 * Name: initResources()
	 * Description: Loads the graphic files for the menu system.  Also
	 * 				sets the position of the rectangles.  Loads music as well
	 * Variables:	cursor: mouse cursor graphic
	 * 				easy: easy button graphic
	 * 				exit: exit button graphic
	 * 				hard: hard button graphic
	 * 				instructions: instruction button graphic
	 * 				instructionscreen: instructions screen
	 * 				loadGame: load game button graphic
	 * 				load(1 - 3): load 1, load 2, load 3 button graphics
	 * 				medium: medium button graphic
	 * 				newGame: new game button graphic
	 * 				returnbuton: return button graphic
	 * 				title: main menu background
	 * 				*Rect: rectangle covering area where buttons will be
	 * Input/Output: 	None
	 * Files Accessed:  All files in Graphics/StartMenu/ and Graphics/Misc/cursor.png
	 * 					Sounds/Music/menu.mid
	 * Files Changed:	None
	 */
	public void initResources()
	{
		//reads in background images
		title = getImage("Graphics/StartMenu/title.jpg");
		instructionscreen = getImage("Graphics/StartMenu/ingameinstructions.jpg");
		
		//reads in button images
		newGame = getImage("Graphics/StartMenu/newgame.png");
		loadGame = getImage("Graphics/StartMenu/loadgame.png");
		instructions = getImage("Graphics/StartMenu/instructions.png");
		exit = getImage("Graphics/StartMenu/exit.png");
		easy = getImage("Graphics/StartMenu/easy.png");
		medium = getImage("Graphics/StartMenu/medium.png");
		hard = getImage("Graphics/StartMenu/hard.png");
		load1 = getImage("Graphics/StartMenu/load1.png");
		load2 = getImage("Graphics/StartMenu/load2.png");
		load3 = getImage("Graphics/StartMenu/load3.png");
		returnbutton = getImage("Graphics/StartMenu/return.png");
		
		//sets rectangle position, width, and length
		newRect = new Rectangle(buttonx, buttony2, buttonlengthx, buttonlengthy);
		loadRect = new Rectangle(buttonx, buttony3, buttonlengthx, buttonlengthy);
		instructionRect = new Rectangle(buttonx, buttony4, buttonlengthx, buttonlengthy);
		exitRect = new Rectangle(buttonx, buttony5, buttonlengthx, buttonlengthy);
		easyRect = new Rectangle(buttonx, buttony2, buttonlengthx, buttonlengthy);
		mediumRect = new Rectangle(buttonx, buttony3, buttonlengthx, buttonlengthy);
		hardRect = new Rectangle(buttonx, buttony4, buttonlengthx, buttonlengthy);
		load1Rect = new Rectangle(buttonx, buttony2, buttonlengthx, buttonlengthy);
		load2Rect = new Rectangle(buttonx, buttony3, buttonlengthx, buttonlengthy);
		load3Rect = new Rectangle(buttonx, buttony4, buttonlengthx, buttonlengthy);
		loadReturnRect = new Rectangle(buttonx, buttony5, buttonlengthx, buttonlengthy);
		returnRect = new Rectangle(50, 700, buttonlengthx, buttonlengthy);
		
		//reads in cursor graphic
		cursor = getImage("Graphics/Misc/cursor.png");

		//start the music
		playMusic("Sounds/Music/menu.mid");
	}
	
	/*
	 * Name: render(Graphics2D)
	 * Description: Draws the graphics to the screen.  The graphics that are drawn depend
	 * 				on the current state of the menu
	 * Variables:	cursor: mouse cursor graphic
	 * 				easy: easy button graphic
	 * 				exit: exit button graphic
	 * 				hard: hard button graphic
	 * 				instructions: instruction button graphic
	 * 				instructionscreen: instructions screen
	 * 				loadGame: load game button graphic
	 * 				load(1 - 3): load 1, load 2, load 3 button graphics
	 * 				medium: medium button graphic
	 * 				newGame: new game button graphic
	 * 				op: buffered image filter
	 * 				returnbuton: return button graphic
	 * 				showDifficultyMenu: current state of menu.  If true, shows the difficulty menu
	 * 				showInstructions: current menu state.  If true, shows instructions screen
	 * 				showLoadMenu: current menu state.  If true, shows load game screen
	 * 				title: main menu background
	 * 				*Rect: rectangle covering area where buttons will be
	 * Input/Output: 	None
	 * Files Accessed:  None
	 * Files Changed:	None
	 */
	public void render(Graphics2D g)
	{
		//draw main title background
		g.drawImage(title, op, 0, 0);
		
		//if the difficulty menu is showing
		if(showDifficultyMenu == true)
		{
			//draw difficulty buttons and return button
			g.drawImage(easy, op, easyRect.x, easyRect.y);
			g.drawImage(medium, op, mediumRect.x, mediumRect.y);
			g.drawImage(hard, op, hardRect.x, hardRect.y);
			g.drawImage(returnbutton, op, loadReturnRect.x, loadReturnRect.y);
		}
		
		//if instructions screen is showing
		else if(showInstructions == true)
		{	
			//draw instructions screen and return button
			g.drawImage(instructionscreen, op, 0, 0);
			g.drawImage(returnbutton, op, returnRect.x, returnRect.y);

		}
		
		//if load menu is showing
		else if(showLoadMenu == true)
		{
			//draw load butons and return button
			g.drawImage(load1, op, load1Rect.x, load1Rect.y);
			g.drawImage(load2, op, load2Rect.x, load2Rect.y);
			g.drawImage(load3, op, load3Rect.x, load3Rect.y);
			g.drawImage(returnbutton, op, loadReturnRect.x, loadReturnRect.y);
		}
		
		//the main menu is showing
		else
		{
			//draw main menu buttons
			g.drawImage(newGame, op, newRect.x, newRect.y);
			g.drawImage(loadGame, op, loadRect.x, loadRect.y);
			g.drawImage(instructions, op, instructionRect.x, instructionRect.y);
			g.drawImage(exit, op, exitRect.x, exitRect.y);
		}
		
		//draw mouse cursor
		g.drawImage(cursor, op, getMouseX(), getMouseY());
	}
	
	/*
	 * Name: update(long)
	 * Description: updates what menu is to be showing or closing based on user input.
	 * 				Input is read in through the clicking of the left mouse button.
	 * Variables:	mx, my: current mouse positions
	 * 				parent: used to set the games next state based on "parent.nextGameID"
	 * 				*Rect: rectangle covering area where buttons will be
	 * 				rtsPointer: used to set difficulty of game
	 * 	  			showDifficultyMenu: current state of menu.  If true, shows the difficulty menu
	 * 				showInstructions: current menu state.  If true, shows instructions screen
	 * 				showLoadMenu: current menu state.  If true, shows load game screen
	 * Input/Output: 	Input: user moving and clicking the mouse
	 * Files Accessed:  Save/slotOne.txt, Save/slotTwo.txt, Save/slotThree.txt
	 * 					Data/levelOneEasy.txt, Data/levelOneNormal.txt, Data/levelOneHard.txt
	 * Files Changed:	None
	 */
	public void update(long elapsedTime)
	{
		//get current mouse location
		int mx = getMouseX();
		int my = getMouseY();
		
		//difficulty menu is showing
		if(showDifficultyMenu == true)
		{
			//easy button is clicked
			if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && easyRect.contains(mx, my))
			{
				//set difficulty to easy
				parent.nextGameID = RTS.GAME_LOAD;
				rtsPointer.gameFile = "Data/levelOneEasy.txt";
				finish();
			}
			
			//medium button is clicked
			else if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && mediumRect.contains(mx, my))
			{
				//set difficulty to medium
				parent.nextGameID = RTS.GAME_LOAD;
				rtsPointer.gameFile = "Data/levelOneNormal.txt";
				finish();
			}
			
			//hard button is clicked
			else if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && hardRect.contains(mx, my))
			{
				//set difficulty to hard
				parent.nextGameID = RTS.GAME_LOAD;
				rtsPointer.gameFile = "Data/levelOneHard.txt";
				finish();
			}
			
			//return button is clicked
			else if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && loadReturnRect.contains(mx, my))
				showDifficultyMenu = false;
		}		
		
		//Instructions button is clicked
		else if(showInstructions == true)
		{
			//return button is clicked
			if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && returnRect.contains(mx, my))
				showInstructions = false;
		}
		
		//load menu is showing
		else if(showLoadMenu == true)
		{
			//load 1 button is clicked
			if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && load1Rect.contains(mx, my))
			{
				//load game 1
				rtsPointer.gameFile = "Save/slotOne.txt";
				parent.nextGameID = RTS.GAME_LOAD;
				finish();
			}
			
			//load 2 button is clicked
			else if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && load2Rect.contains(mx, my))
			{
				//load game 2
				rtsPointer.gameFile = "Save/slotTwo.txt";
				parent.nextGameID = RTS.GAME_LOAD;
				finish();
			}
			
			//load 3 button is clicked
			else if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && load3Rect.contains(mx, my))
			{
				//load game 3
				rtsPointer.gameFile = "Save/slotThree.txt";
				parent.nextGameID = RTS.GAME_LOAD;
				finish();
			}
			
			//return button is clicked
			else if(bsInput.isMouseReleased(MouseEvent.BUTTON1) && loadReturnRect.contains(mx, my))
			{
				//return to main menu
				showLoadMenu = false;
			}
		}
		
		//main menu is showing
		else
		{
			//new game is clicked
			if (bsInput.isMouseReleased(MouseEvent.BUTTON1) && newRect.contains(mx, my))
			{
				//show the difficulty menu
				showDifficultyMenu = true;
			}

			//load game is clicked
			else if (bsInput.isMouseReleased(MouseEvent.BUTTON1) && loadRect.contains(mx, my))
			{
				//show the load menu
				showLoadMenu = true;
			}
			
			//instructions button is clicked
			else if (bsInput.isMouseReleased(MouseEvent.BUTTON1) && instructionRect.contains(mx, my))
			{
				//show the instructions
				showInstructions = true;
			}
		
			//exit is clicked
			else if (bsInput.isMouseReleased(MouseEvent.BUTTON1) && exitRect.contains(mx, my))
				finish();
		}
	}
}