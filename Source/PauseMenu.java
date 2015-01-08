/*
 * Name of code: PauseMenu.java
 * Description:  PauseMenu.java is a GUI pausing the game, entering cheats,
 * 				 saving the game, and exiting the game
 * Programmer:	 Adam Riha
 * Date of last modification: 5/6/06
 */

import java.awt.*;
import java.awt.image.*;
import java.awt.event.MouseEvent;

public class PauseMenu
{
	//width and length of button images
	private static final int buttonlengthx = 170;
	private static final int buttonlengthy = 51;
	
	private GamePlay gamePlay;  //used for bsInput functions
	private Cheat cheatMenu;	//cheat menu
	private SaveMenu saveMenu;	//save menu
	
	private BufferedImage menu;  //menu background
	private BufferedImage cursor; //cursor image

	//button images
	private BufferedImage saveButton;
	private BufferedImage cheatButton;
	private BufferedImage cancelButton;
	private BufferedImage exitButton;
	
	private BufferedImageOp op;	//buffered image filter
	
	//button rectangles.  Used to determine when a button is clicked
	private Rectangle saveRect;
	private Rectangle cheatRect;
	private Rectangle cancelRect;
	private Rectangle exitRect;
	
	//Boolean variables to determine menu's current state
	private boolean cheatMenuShowing;
	private boolean saveMenuShowing;
	
	/*
	 * Name: PauseMenu(GamePlay)
	 * Description: Constructor for pause menu.  loads background and button graphics.
	 * 				sets rectangle position/width and length
	 * Arguments: parent(GamePlay)
	 * Variables:	cancelButton: cancel button graphic
	 * 				cheatButton: cheat button graphic
	 * 				cheatMenu: cheat menu
	 * 				cheatMenuShowing: current state of pause menu.  If true show cheat menu
	 * 				cursor: mouse cursor graphic
	 * 				exitButton: exit button graphic
	 * 				gamePlay: GamePlay object to provide bsInput functions
	 * 				menu: pause menu background graphic
	 * 				saveButton: save button graphic
	 * 				saveMenu: save menu
	 * 				saveMenuShowing: current state of pause menu.  If true show save menu
	 * 				*Rect: rectangle covering area where buttons will be
	 * Input/Output: None
	 * Files Accessed:  Graphics/Pause/pauseMenuBackground.jpg
	 * 					Graphics/Pause/savegame.png
	 * 					Graphics/Pause/cheat.png
	 * 					Graphics/Pause/cancel.png
	 * 					Graphics/Pause/exit.png
	 * 					Graphics/Misc/cursor.png
	 * Files Changed:	None		
	 */
	public PauseMenu(GamePlay parent)
	{
		gamePlay = parent;
		
		//read in menu background
		menu = gamePlay.getImage("Graphics/Pause/pauseMenuBackground.jpg");
		
		//create cheat and save menu in case they are called
		cheatMenu = new Cheat(gamePlay, this);
		saveMenu = new SaveMenu(gamePlay, this);
		
		//read in button graphics
		saveButton = gamePlay.getImage("Graphics/Pause/savegame.png");
		cheatButton = gamePlay.getImage("Graphics/Pause/cheat.png");
		cancelButton = gamePlay.getImage("Graphics/Pause/cancel.png");
		exitButton = gamePlay.getImage("Graphics/Pause/exit.png");
		
		//read in cursor graphic
		cursor = gamePlay.getImage("Graphics/Misc/cursor.png");

		//sets rectangle position, width, and length
		saveRect = new Rectangle(427, 170, buttonlengthx, buttonlengthy);
		cheatRect = new Rectangle(427, 250, buttonlengthx, buttonlengthy);
		cancelRect = new Rectangle(427, 330, buttonlengthx, buttonlengthy);
		exitRect = new Rectangle(427, 410, buttonlengthx, buttonlengthy);
		
		//don't show cheat or save menu on creation
		cheatMenuShowing = false;
		saveMenuShowing = false;
	}
	
	/*
	 * render(Graphics2D)
	 * Description: Draws the graphics on the screen.  The graphics that are drawn depend
	 * 				on the current state of the menu.
	 * Variables:	cancelButton: cancel button graphic
	 * 				cheatButton: cheat button graphic
	 * 				cheatMenu: cheat menu
	 * 				cheatMenuShowing: current state of pause menu.  If true show cheat menu
	 * 				exitButton: exit button graphic
	 * 				menu: pause menu background graphic
	 * 				op: buffered image filter
	 * 				saveMenu: save menu
	 * 				saveMenuShowing: current state of pause menu.  If true show save menu
	 * Input/Output: None
	 * Files Accessed: None
	 * Files Changed: None
	 */
	public void render (Graphics2D g)
	{
		//draw menu
		g.drawImage(menu, op, 337, 100);
		
		//draw buttons
		g.drawImage(saveButton, op, saveRect.x, saveRect.y);
		g.drawImage(cheatButton, op, cheatRect.x, cheatRect.y);
		g.drawImage(cancelButton, op, cancelRect.x, cancelRect.y);
		g.drawImage(exitButton, op, exitRect.x,exitRect.y);	

		//if the cheat menu is showing, render the cheat menu
		if(cheatMenuShowing == true)
			cheatMenu.render(g);
		
		//if the save menu is showing, render the save menu
		if(saveMenuShowing == true)
			saveMenu.render(g);
		
		//draw the mouse cursor
		g.drawImage(cursor,op,gamePlay.getMouseX(),gamePlay.getMouseY());

	}

	/*
	 * Name: update(long)
	 * Description: updates what menu is to be showing or closing based on user input.
	 * 				Input is read in through the clicking of the left mouse button.
	 * Variable:	cheatMenu: cheat menu
	 * 				cheatMenuShowing: current menu state.  If true, shows cheat menu
	 * 				gamePlay: GamePlay object to provide bsInput functions
	 * 				mx, my: current mouse positions
	 * 				saveMenu: save menu
	 * 				saveMenuShowing: current menu state.  If true, shows save menu
	 * 				*Rect: rectangle covering area where buttons will be
	 * Input/Output:	Input: user moving and clicking the mouse
	 * Files Accessed:	None
	 * Files Changed:	None
	 */
	public void update(long elapsedTime)
	{
		//get current mouse location
		int mx = gamePlay.getMouseX();
		int my = gamePlay.getMouseY();
		
		//cheat menu is showing, update cheat menu only
		if (cheatMenuShowing == true)
		{
			cheatMenu.update(elapsedTime);
			return;
		}
		
		//save menu is showing, update save menu only
		if (saveMenuShowing == true)
		{
			saveMenu.update(elapsedTime);
			return;
		}
		
		//save button is clicked
		if (gamePlay.bsInput.isMouseReleased(MouseEvent.BUTTON1) && saveRect.inside(mx, my))
 			ShowSaveMenu();

		//cheat button is clicked
		else if (gamePlay.bsInput.isMouseReleased(MouseEvent.BUTTON1) && cheatRect.inside(mx, my))
			ShowCheatMenu();

		//cancel button is clicked
 		else if (gamePlay.bsInput.isMouseReleased(MouseEvent.BUTTON1) && cancelRect.inside(mx, my))		
			gamePlay.unPause();
			
		//exit button is clicked
 		else if (gamePlay.bsInput.isMouseReleased(MouseEvent.BUTTON1) && exitRect.inside(mx, my))
    		gamePlay.finish();
	}

	/*
	 * Name: ShowCheatMenu
	 * Description: sets cheatMenuShowing state variable to true.
	 * Variables:	cheatMenuShowing: current menu state.  If true, shows cheat menu
	 * Input/Output:	None
	 * Files Accessed:	None
	 * Files Changed:	None 
	 */
	public void ShowCheatMenu()
	{
		cheatMenuShowing = true;
	}
	
	/*
	 * Name: HideCheatMenu
	 * Description: sets cheatMenuShowing state variable to false.
	 * Variables:	cheatMenuShowing: current menu state.  If true, shows cheat menu
	 * Input/Output:	None
	 * Files Accessed:	None
	 * Files Changed:	None 
	 */
	public void HideCheatMenu()
	{
		cheatMenuShowing = false;
	}
	
	/*
	 * Name: ShowSaveMenu
	 * Description: sets saveMenuShowing state variable to true.
	 * Variables:	saveMenuShowing: current menu state.  If true, shows save menu
	 * Input/Output:	None
	 * Files Accessed:	None
	 * Files Changed:	None 
	 */
	public void ShowSaveMenu()
	{
		saveMenuShowing = true;
	}
	
	/*
	 * Name: HideSaveMenu
	 * Description: sets saveMenuShowing state variable to false.
	 * Variables:	saveMenuShowing: current menu state.  If true, shows save menu
	 * Input/Output:	None
	 * Files Accessed:	None
	 * Files Changed:	None 
	 */
	public void HideSaveMenu()
	{
		saveMenuShowing = false;
	}
}
