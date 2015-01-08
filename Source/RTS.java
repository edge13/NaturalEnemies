/*
 * Name of code: RTS.java
 * Description:  Starting point for the game.  Controls global data such as which level is
 *				 being loaded
 * Programmer:	 Joel Angelone
 * Date of last modification: 4/27/06
 */

import java.awt.*;
import com.golden.gamedev.*;

public class RTS extends GameEngine
{
	{ distribute = true; }
	
	public static final int GAME_MENU = 0;
	public static final int GAME_LOAD = 1;
	public static final int GAME_PLAY = 2;
	public static final int GAME_CREDITS = 3;
	
	public String gameFile;
	public boolean graphicsLoaded;
	
	public void initResources()
	{
		setFPS(30);
		graphicsLoaded = false;
		nextGameID = GAME_MENU;
		hideCursor();
	}
	
	public GameObject getGame(int GameID)
	{
		switch (GameID)
		{
			case GAME_MENU:
				return new StartMenu(this, this);
			case GAME_LOAD:
				return new LoadingScreen(this, this);
			case GAME_PLAY:
				return new GamePlay(this, this);
			case GAME_CREDITS:
				return new Credits(this);
		}
		
		return null;
	}
	
	// Game Engine Stuff
	public static void main(String[] args)
	{	
		GameLoader game = new GameLoader();
		game.setup(new RTS(), new Dimension(1024, 768), true);
		game.start();
	}
}