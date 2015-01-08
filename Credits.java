/*
 * Name of code: Cheat.java
 * Description:	 Displays a scrolling credits screen.
 * Programmer Name: Joel Angelone
 * Date of last modification: 4/28/06
 */

import java.awt.*;
import java.awt.image.*;
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;

public class Credits extends GameObject
{
	private BufferedImage credits;
	private BufferedImageOp op;
	private int counter;
	private Timer counterTimer;
	
	private Font font;
	private boolean done;
	
	// Credits() - class constructor
	public Credits(GameEngine parent)
	{
		super(parent);
	}
	
	// initResources()
	// Load the graphics, the timer, and play credits music
	public void initResources()
	{
		counterTimer = new Timer(5);
		font = new Font("Bradley Hand ITC", Font.BOLD | Font.ITALIC, 20);
		counter = 0;
		credits = getImage("Graphics/Misc/credits.jpg");
		done = false;

		bsMusic.setVolume(0.4f);
		playMusic("Sounds/Music/credits.mid");
	}
	
	// render()
	// Draws the credits to the screen
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 1024, 768);
		
		if (!done)
			g.drawImage(credits, op, 260, 768 - counter);
		else
		{
			g.setFont(font);
			g.setColor(Color.WHITE);
			g.drawString("Thanks for playing.", 400, 360);
		}
	}
	
	// update()
	// Increments the counter that controls the text position
	public void update(long elapsedTime)
	{
		if (counterTimer.action(elapsedTime))
		{
			counter++;
			if (done && counter == 170)
			{
				parent.nextGameID = RTS.GAME_MENU;
				finish();
			}
				
		}
			
		if (counter == 1550)
		{
			done = true;
			counter = 0;
		}
		
		if(keyDown(java.awt.event.KeyEvent.VK_ESCAPE))
		{
			parent.nextGameID = RTS.GAME_MENU;
			finish();
		}
	}
}