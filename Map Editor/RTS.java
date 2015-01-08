import java.awt.*;
import com.golden.gamedev.*;

public class RTS extends GameEngine
{
	public static final int GAME_MENU = 0;
	public static final int GAME_LOAD = 1;
	public static final int GAME_PLAY = 2;
	public static final int GAME_CREDITS = 3;
	
	public String gameFile;
	
	public void initResources()
	{
		setFPS(30);
	}
	
	public GameObject getGame(int GameID)
	{
		return new GamePlay(this, this);

	}
	
	public static void main(String[] args)
	{
		GameLoader game = new GameLoader();
		game.setup(new RTS(), new Dimension(1024, 768), true);
		game.start();
	}
}

