/*Name of code: StartMenu.java
 * Description: Handles the save menu.  Calls gamePlay's save function when a save slot is chosen
 * Programmer Name: Adam Riha
 */

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.*;
import java.io.IOException;

public class SaveMenu
{
	// width and length of buttons
	private static final int buttonlengthx = 170;
	private static final int buttonlengthy = 51;
	
	private GamePlay gamePlay;
	private PauseMenu pauseMenu;

	private BufferedImage saveMenu;	// save menu background
	private BufferedImage cursor;	// cursor graphic
	private BufferedImageOp op;
	
	// save game button images
	private BufferedImage saveButton1;
	private BufferedImage saveButton2;
	private BufferedImage saveButton3;
	private BufferedImage cancelButton;
	
	// rectangles to be used for determing if a certain button is clicked
	private Rectangle save1Rect;
	private Rectangle save2Rect;
	private Rectangle save3Rect;
	private Rectangle cancelRect;
	
	/* Name: SaveMenu(GamePlay, PauseMenu)
	 * Description: SaveMenu constructor
	 * Arguments: 	parent(GamePlay), otherparent(PauseMenu)
	 * Variables: 	cancelButton: cancel button graphics
	 * 				cancelRect:	location where cancel button is drawn
	 * 				cursor:	cursor graphic
	 * 				gamePlay: object with basic input functions
	 * 				pauseMenu: Pause menu object
	 * 				saveButton(1 - 3): save button graphics
	 * 				saveMenu: save menu background
	 * 				save(1-3)Rect: rectangle covering area where save buttons are
	 * Input/Output:	None
	 * Files Accessed: 	Graphics/Menu/pauseMenuBackground.jpg
	 * 					Graphics/Menu/save1.png
	 * 					Graphics/Menu/save2.png
	 * 					Graphics/Menu/save3.png
	 * 					Graphics/Menu/cancel.png
	 * 					Graphics/Menu/cursor.png
	 * Files Changed: 	none
	 */ 
	public SaveMenu(GamePlay parent, PauseMenu otherparent)
	{
		gamePlay = parent;
		pauseMenu = otherparent;
		
		saveMenu = gamePlay.getImage("Graphics/Pause/pauseMenuBackground.jpg");

		// Initialize menu and button graphics
		saveButton1 = gamePlay.getImage("Graphics/Pause/save1.png");
		saveButton2 = gamePlay.getImage("Graphics/Pause/save2.png");
		saveButton3 = gamePlay.getImage("Graphics/Pause/save3.png");
		cancelButton = gamePlay.getImage("Graphics/Pause/cancel.png");
	
		cursor = gamePlay.getImage("Graphics/Misc/cursor.png");
		
		// Initialize location and length of rectangles
		save1Rect = new Rectangle(427, 170, buttonlengthx, buttonlengthy);
		save2Rect = new Rectangle(427, 250, buttonlengthx, buttonlengthy);
		save3Rect = new Rectangle(427, 330, buttonlengthx, buttonlengthy);
		cancelRect = new Rectangle(427, 410, buttonlengthx, buttonlengthy);
	}
	
	/* Name: render(Graphics2D)
	 * Description: Draws the save menu background and buttons on screen
	 * Arguments: 	g (Graphics2D)
	 * Variables: 	cancelButton: cancel button graphic
	 * 				cancelRect: rectangle covering area where cancel button is rendered
	 * 				cursor: cursor graphic
	 * 				gamePlay: gives the function access to basic GamePlay methods
	 * 				op: image filter
	 * 				saveButton1, saveButton2, saveButton3: Save button graphics
	 * 				saveMenu: save menu background
	 * 				save1Rect, save2Rect, save3Rect: rectangle covering area where buttons are rendered
	 * Input/Output:	Outputs graphics to screen
	 * Files Accessed: 	none
	 * Files Changed: 	none
	 */
	public void render(Graphics2D g)
	{
		g.drawImage(saveMenu, op, 337, 100);
		
		g.drawImage(saveButton1, op, save1Rect.x, save1Rect.y);
		g.drawImage(saveButton2, op, save2Rect.x, save2Rect.y);
		g.drawImage(saveButton3, op, save3Rect.x, save3Rect.y);
		g.drawImage(cancelButton, op, cancelRect.x, cancelRect.y);	
		
		g.drawImage(cursor,op,gamePlay.getMouseX(),gamePlay.getMouseY());
	}
	
	/* Name: update(long)
	 * Description: Reads in user input
	 * Arguments: 	elapsedTime(long)
	 * Variables:	gamePlay: basic input functions from GamePlay
	 * 			 	mx: x location of mouse
	 * 				my: y location of mouse
	 * 				save(1-3)Rect: rectangle where save buttons are located
	 * 				cancelRect: rectangle where cancel button is located
	 * 				pauseMenu: pause menu parent passed in order to call hideSaveMenu() funct.
	 * Input/Output:	Mouse click and position
	 * Files Accessed: 	none
	 * Files Changed: 	none
	 */
	public void update(long elapsedTime)
	{
		// location of mouse pointer
		int mx = gamePlay.getMouseX();
		int my = gamePlay.getMouseY();
		
		// if the left mouse button is released over the save 1 button, save game in slot 1
 		if (gamePlay.bsInput.isMouseReleased(MouseEvent.BUTTON1) && save1Rect.inside(mx, my))
 		{
 			try { gamePlay.save(1); }
 			catch (IOException e) {}
 			pauseMenu.HideSaveMenu();
 		}
 		
 		// if the left mouse button is released over the save 2 button, save game in slot 2
 		else if (gamePlay.bsInput.isMouseReleased(MouseEvent.BUTTON1) && save2Rect.inside(mx, my))
 		{
 			try { gamePlay.save(2); }
 			catch (IOException e) {}
 			pauseMenu.HideSaveMenu();
 		}

 		// if the left mouse button is released over the save 3 button, save game in slot 3
 		else if (gamePlay.bsInput.isMouseReleased(MouseEvent.BUTTON1) && save3Rect.inside(mx, my))
 		{
 			try { gamePlay.save(3); }
 			catch (IOException e) {}
 			pauseMenu.HideSaveMenu();
 		}
 		
 		// if the left mouse button is released over the cancel button, return to the pause menu
 		else if (gamePlay.bsInput.isMouseReleased(MouseEvent.BUTTON1) && cancelRect.inside(mx, my))
			pauseMenu.HideSaveMenu();
	}
}