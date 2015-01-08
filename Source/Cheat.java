/*
 * Name of code: Cheat.java
 * Description:	 Cheat.java is a GUI for enabling and disabling cheats
 * Programmer Name: Adam Riha
 * Date of last modification: 5/6/06
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.awt.*;

public class Cheat
{
	private String input = "";
	private String cheat = "";
	
	private GamePlay gamePlay;
	private PauseMenu pauseMenu;
	
	private BufferedImage cheatMenu;	//cheat menu graphic
	private BufferedImage cursor;
	private BufferedImageOp op;
	
	Font cheatfont;

	/* Name: cheat(GamePlay, PauseMenu)
	 * Description: Initializes the cheat class
	 * Arguments: 	parent (GamePlay), otherparent (PauseMenu)
	 * Variables:	gamePlay: pointer to the parent GamePlay class
	 * 			 	pauseMenu: pointer to the parent PauseMenu class
	 * 				cheatMenu: BufferedImage containing the cheat menu graphics
	 * 				cursor: BufferedImage containing the cursor graphics
	 * 				cheatfont: font used in the cheat menu;
	 * Input/Output:	none
	 * Files Accessed: 	none
	 * Files Changed: 	none
	 */
	public Cheat(GamePlay parent, PauseMenu otherparent)
	{
		gamePlay = parent;
		pauseMenu = otherparent;
		
		cheatMenu = gamePlay.getImage("Graphics/Pause/enterCheatBackground.jpg");
		cursor = gamePlay.getImage("Graphics/Misc/cursor.png");
		
		cheatfont = new Font("Bradley Hand ITC", Font.BOLD, 22);
	}
	
	/* Name: render(Graphics2D)
	 * Description: Draws the save menu background and buttons on screen
	 * Arguments: 	g (Graphics2D)
	 * Variables:	cheatMenu: BufferedImage containing the cheat menu graphics
	 * 				cursor: BufferedImage containing the cursor graphics
	 * 				cheatfont: font used in the cheat menu;
	 * Input/Output:	Outputs graphics to screen
	 * Files Accessed: 	none
	 * Files Changed: 	none
	 */
	public void render(Graphics2D g)
	{			
		g.drawImage(cheatMenu, op, 312, 234);
		g.setFont(cheatfont);
		g.setColor(Color.WHITE);

		g.drawString(input, 320, 350);
		if(cheat.compareTo("IDDQD") == 0)
			g.drawString("GOD MODE ENABLED", 320, 370);
		else if(cheat.compareTo("HEADSHOT") == 0)
			g.drawString("ONE HIT KILLS ENABLED", 320, 370);
		else if(cheat.compareTo("OFF") == 0)
			g.drawString("ALL CHEATS ARE TURNED OFF", 320, 370);
		else
			g.drawString("", 320, 370);

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
		if(gamePlay.bsInput.isKeyReleased(KeyEvent.VK_ESCAPE))
			pauseMenu.HideCheatMenu();

		if(gamePlay.bsInput.getKeyPressed() != gamePlay.bsInput.NO_KEY)
		{
			int key = gamePlay.bsInput.getKeyPressed();
			
			if(gamePlay.keyPressed(KeyEvent.VK_ENTER))
			{
				cheat = input;
				input = "";
				
				if(cheat.compareTo("IDDQD") == 0)
					gamePlay.enableGodMode();
				else if(cheat.compareTo("HEADSHOT") == 0)
					gamePlay.enableOneHitKills();
				else if(cheat.compareTo("OFF") == 0)
					gamePlay.cheatsOff();
			}
			else if(gamePlay.keyPressed(KeyEvent.VK_BACK_SPACE))
			{
				if(input.length() > 0)
					input = input.substring(0, input.length()-1);
			}

			else if(key == KeyEvent.VK_SPACE && input.length() <= 25)
			{
				input += " ";
			}

			else if ((key >= KeyEvent.VK_A && key <= KeyEvent.VK_Z)
					&& input.length() <= 25)
			{
				input += KeyEvent.getKeyText(key);
			}
			
			// non alphabetic characters are thrown away
			else;
		}
	}
}