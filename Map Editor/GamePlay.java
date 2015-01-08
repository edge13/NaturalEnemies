import java.awt.Graphics2D;
import java.awt.image.*;
import com.golden.gamedev.GameEngine;
import com.golden.gamedev.GameObject;
import java.awt.Point;
import java.io.IOException;


public class GamePlay extends GameObject
{
	private Map		map;
	private Point	camera;
	private int type;
	
	private BufferedImage tree;
	private BufferedImage bush;
	private BufferedImage pond;
	private BufferedImage lake;
	private BufferedImage pathNorth;
	private BufferedImage pathEast;
	private BufferedImage wallNorth;
	private BufferedImage wallCap;
	
	private BufferedImageOp op;
	
	public GamePlay(GameEngine parent, RTS rts)
	{
		super(parent);
	}
	
	public void initResources()
	{
		camera = new Point(250, 300);
		try { map = new Map(this, "current.txt"); }
		catch (IOException e) {}
		
		type = Map.TREE;
		
		tree = getImage("Environment/tree.png");
		bush = getImage("Environment/bush.png");
		pond = getImage("Environment/pond.png");
		lake = getImage("Environment/lake.png");
		pathNorth = getImage("Environment/pathNorth.png");
		pathEast = getImage("Environment/pathEast.png");
		wallNorth = getImage("Environment/wallNorth.png");
		wallCap = getImage("Environment/wallCap.png");
	}
	
	public void render(Graphics2D g)
	{	
		map.render(g, camera);
	
		if (type == Map.TREE)
			g.drawImage(tree, op, getMouseX(), getMouseY());
		else if (type == Map.BUSH)
			g.drawImage(bush, op, getMouseX(), getMouseY());
		else if (type == Map.POND)
			g.drawImage(pond, op, getMouseX(), getMouseY());
		else if (type == Map.LAKE)
			g.drawImage(lake, op, getMouseX(), getMouseY());
		else if (type == Map.PATH_NORTH)
			g.drawImage(pathNorth, op, getMouseX(), getMouseY());
		else if (type == Map.PATH_EAST)
			g.drawImage(pathEast, op, getMouseX(), getMouseY());
		else if (type == Map.WALL_NORTH)
			g.drawImage(wallNorth, op, getMouseX(), getMouseY());	
		else if (type == Map.WALL_CAP)
			g.drawImage(wallCap, op, getMouseX(), getMouseY());
	}
	
	public void update(long elapsedTime)
	{	
		map.update(elapsedTime);
		processInput();
	}
	
	public void processInput()
	{
		int mx = getMouseX();
		int my = getMouseY();
		
		if (mx >= 1014) moveCamera(25, 0);
		if (my >= 758) moveCamera(0, 25);
		if (mx <= 10) moveCamera(-25, 0);
		if (my <= 10) moveCamera(0, -25);
		
		if (click())
		{
			map.add(type, mx + camera.x, my + camera.y);
		}
			
		if (rightClick())
		{
			type++;
			if (type > 8)
				type = 1;
		}
		
		if (keyDown(java.awt.event.KeyEvent.VK_ENTER))
		{
			try { map.save(); }
			catch (IOException e) {}
		}
		
		if (keyDown(java.awt.event.KeyEvent.VK_ESCAPE))
    		finish();
	}
	
	public void moveCamera(int dx, int dy)
	{	
		camera.x += dx;
		camera.y += dy;
		
		if (camera.x < 0) camera.x= 0;
		if (camera.y < 0) camera.y = 0;
		
		if (camera.x + 1024 > Map.SIZE) camera.x = Map.SIZE - 1024;
		if (camera.y + 768 > Map.SIZE) camera.y = Map.SIZE - 768;
	}	
}