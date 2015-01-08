import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.Graphics2D;
import com.golden.gamedev.object.*;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;

public class Map
{
	public static final int TREE = 1;
	public static final int BUSH = 2;
	public static final int POND = 3;
	public static final int LAKE = 4;
	public static final int PATH_NORTH = 5;
	public static final int PATH_EAST = 6;
	public static final int WALL_NORTH = 7;
	public static final int WALL_CAP = 8;
	
	public static final int MAX_OBJECTS = 200;
	public static final int SIZE = 3072;
	
	private BufferedImage backdrop;
	private BufferedImageOp op;
	
	private Sprite objects[];
	private int numObjects;
	private int typeList[];
	
	GamePlay gamePlay;
	
	public Map(GamePlay parent, String file) throws IOException
	{
		backdrop = parent.getImage("Environment/backdrop.jpg");
		objects = new Sprite[MAX_OBJECTS];
		typeList = new int[MAX_OBJECTS];
		gamePlay = parent;
		
		BufferedReader infile = new BufferedReader(new FileReader(file));
		
		numObjects = Integer.parseInt(infile.readLine());
		
		for (int i = 0; i < numObjects; i++)
		{
			infile.readLine();
			int type = Integer.parseInt(infile.readLine());
			typeList[i] = type;
			if (type == TREE)
				objects[i] = new Sprite(parent.getImage("Environment/tree.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (type == LAKE)
				objects[i] = new Sprite(parent.getImage("Environment/lake.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (type == POND)
				objects[i] = new Sprite(parent.getImage("Environment/pond.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (type == BUSH)
				objects[i] = new Sprite(parent.getImage("Environment/bush.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));		
			else if (type == WALL_NORTH)
				objects[i] = new Sprite(parent.getImage("Environment/wallNorth.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (type == WALL_CAP)
				objects[i] = new Sprite(parent.getImage("Environment/wallCap.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));

			
			else if (type == PATH_NORTH)
				objects[i] = new Sprite(parent.getImage("Environment/pathNorth.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (type == PATH_EAST)
				objects[i] = new Sprite(parent.getImage("Environment/pathEast.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
		}
		
		infile.close();
	}
	
	public void save() throws IOException
	{
		BufferedWriter outfile = new BufferedWriter(new FileWriter("current.txt"));
		outfile.write(String.valueOf(numObjects));
		
		for (int i = 0; i < numObjects; i++)
		{
			outfile.write('\n');
			outfile.write("[Object]");
			outfile.write('\n');
			outfile.write(String.valueOf(typeList[i]));
			outfile.write('\n');
			outfile.write(String.valueOf((int)objects[i].getX()));
			outfile.write('\n');
			outfile.write(String.valueOf((int)objects[i].getY()));
		}
		
		outfile.close();
	}
	
	public void add(int type, int x, int y)
	{
		if (type == TREE)
			objects[numObjects] = new Sprite(gamePlay.getImage("Environment/tree.png"), x, y);
		else if (type == BUSH)
			objects[numObjects] = new Sprite(gamePlay.getImage("Environment/bush.png"), x, y);
		else if (type == LAKE)
			objects[numObjects] = new Sprite(gamePlay.getImage("Environment/lake.png"), x, y);
		else if (type == POND)
			objects[numObjects] = new Sprite(gamePlay.getImage("Environment/pond.png"), x, y);
		else if (type == PATH_NORTH)
			objects[numObjects] = new Sprite(gamePlay.getImage("Environment/pathNorth.png"), x, y);
		else if (type == PATH_EAST)
			objects[numObjects] = new Sprite(gamePlay.getImage("Environment/pathEast.png"), x, y);
		else if (type == WALL_NORTH)
			objects[numObjects] = new Sprite(gamePlay.getImage("Environment/wallNorth.png"), x, y);
		else if (type == WALL_CAP)
			objects[numObjects] = new Sprite(gamePlay.getImage("Environment/wallCap.png"), x, y);
		
		typeList[numObjects] = type;
		numObjects++;
	}
	
	public void render(Graphics2D g, Point camera)
	{
		for (int x = 0; x < SIZE / 1024; x++)
			for (int y = 0; y < SIZE / 768; y++)
				g.drawImage(backdrop, op, x*1024 - camera.x, y*768 - camera.y);
		
		for (int i = 0; i < numObjects; i++)
			objects[i].render(g, (int)objects[i].getX() - camera.x, (int)objects[i].getY() - camera.y);			
	}
	
	public void update(long elapsedTime)
	{
	}
}