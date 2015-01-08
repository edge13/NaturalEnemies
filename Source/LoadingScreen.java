/*
 * Name of code: LoadingScreen.java
 * Description: Displays a loading screen to the user while the GamePlay class loads
 * 				the game.
 * Programmer Name: Joel Angelone
 * Date of last modification: 5/8/06
 */

import java.awt.*;
import java.awt.image.*;
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LoadingScreen extends GameObject
{
	BufferedImage image;
	BufferedImageOp op;
	RTS rtsPointer;
	Timer timer;
	boolean rendered;
	
	// LoadingScreen() - class constructor
	// Sets the pointers to the parent classes
	public LoadingScreen(GameEngine parent, RTS rts)
	{
		super(parent);
		rtsPointer = rts;
	}
	
	// Initializes the graphics and peeks at the game file to find out which
	// level is being loaded so the correct screen is displayed.
	public void initResources()
	{
		timer = new Timer(700);
		rendered = false;
		BufferedReader infile;
		
		// read the first line of the file to find out which level is being loaded
		try
		{
			infile = new BufferedReader(new FileReader(rtsPointer.gameFile));
			String which = infile.readLine();
			if (which.equals("Data/mapOne.txt"))
				image = getImage("Graphics/Misc/loadingOne.jpg");
			else if (which.equals("Data/mapTwo.txt"))
				image = getImage("Graphics/Misc/loadingTwo.jpg");
			else
				image = getImage("Graphics/Misc/loadingThree.jpg");
			infile.close();
		}
		catch (IOException e) { }
		
		// music is choppy during loading so just stop it
		bsMusic.stopAll();
	}
	
	// render()
	// displays the loading screen
	public void render(Graphics2D g)
	{
		g.drawImage(image, op, 0, 0);
		rendered = true;
	}
	
	// update()
	// if the graphics have already been loaded into memory, loading is fast, so a
	// delay is placed on the return time for the loading screen if the graphics have
	// already been loaded
	public void update(long elapsedTime)
	{
		// make sure the screen is displayed before loading
		if (!rendered)
			return;
		
		// if the graphics haven't been loaded, no need for a timer
		if (!rtsPointer.graphicsLoaded)
		{
			rtsPointer.graphicsLoaded = true;
			parent.nextGameID = RTS.GAME_PLAY;
			finish();
		}
		
		// otherwise wait for the timer
		else if(timer.action(elapsedTime))
		{
			parent.nextGameID = RTS.GAME_PLAY;
			finish();
		}
	}
}