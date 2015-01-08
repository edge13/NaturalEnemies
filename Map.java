/*
 * Name of code: Map.java
 * Description: Contains all the data for a single map.  Also contains the loading procedure
 * 				for loading a map from a file.
 * Programmer Name: Joel Angelone
 * Date of last modification: 4/28/06
 */

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.Graphics2D;
import com.golden.gamedev.object.*;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Map
{
	// environment object types
	public static final int TREE = 1;
	public static final int BUSH = 2;
	public static final int POND = 3;
	public static final int LAKE = 4;
	public static final int PATH_NORTH = 5;
	public static final int PATH_EAST = 6;
	public static final int WALL_NORTH = 7;
	public static final int WALL_CAP = 8;
	
	// constants
	public static final int MAX_OBJECTS = 600;
	public static final int SIZE = 3072;
	
	// images
	private BufferedImage backdrop;
	private BufferedImageOp op;
	
	public SpriteGroup objectGroup;
	private Sprite objects[];
	private int numObjects;
	private int typeList[];
	
	// Map() - class constructor
	// Initializes all the member variables and loads the map from a file (specified
	// by the file parameter)
	public Map(GamePlay parent, String file) throws IOException
	{
		objectGroup = new SpriteGroup("Objects");
		backdrop = parent.getImage("Graphics/Environment/backdrop.jpg");
		objects = new Sprite[MAX_OBJECTS];
		typeList = new int[MAX_OBJECTS];
		
		BufferedReader infile = new BufferedReader(new FileReader(file));
		
		numObjects = Integer.parseInt(infile.readLine());
		
		for (int i = 0; i < numObjects; i++)
		{
			infile.readLine();
			typeList[i] = Integer.parseInt(infile.readLine());
			if (typeList[i] == TREE)
				objects[i] = new Sprite(parent.getImage("Graphics/Environment/tree.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (typeList[i] == LAKE)
				objects[i] = new Sprite(parent.getImage("Graphics/Environment/lake.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (typeList[i] == POND)
				objects[i] = new Sprite(parent.getImage("Graphics/Environment/pond.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (typeList[i] == BUSH)
				objects[i] = new Sprite(parent.getImage("Graphics/Environment/bush.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));		
			else if (typeList[i] == WALL_NORTH)
				objects[i] = new Sprite(parent.getImage("Graphics/Environment/wallNorth.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (typeList[i] == WALL_CAP)
				objects[i] = new Sprite(parent.getImage("Graphics/Environment/wallCap.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));		
			else if (typeList[i] == PATH_NORTH)
				objects[i] = new Sprite(parent.getImage("Graphics/Environment/pathNorth.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
			else if (typeList[i] == PATH_EAST)
				objects[i] = new Sprite(parent.getImage("Graphics/Environment/pathEast.png"), Integer.parseInt(infile.readLine()), Integer.parseInt(infile.readLine()));
		
			// Roads do not obstruct unit pathing so don't add them to the collision group
			if (typeList[i] != PATH_NORTH && typeList[i] != PATH_EAST)
				objectGroup.add(objects[i]);
		}
		
		infile.close();
	}
	
	// render()
	// Draws the map.
	public void render(Graphics2D g, Point camera)
	{
		for (int x = 0; x < SIZE / 1024; x++)
			for (int y = 0; y < SIZE / 768; y++)
				g.drawImage(backdrop, op, x*1024 - camera.x, y*768 - camera.y);
		
		for (int i = 0; i < numObjects; i++)
			objects[i].render(g, (int)objects[i].getX() - camera.x, (int)objects[i].getY() - camera.y);			
	}
	
	// ******
	// Getters
	// ******
	
	public int getNumObjects()
	{
		return numObjects;
	}
	
	public Sprite getObject(int index)
	{
		if (index < 0 || index >= numObjects)
			return null;
		else
			return objects[index];
	}
	
	public int getType(int index)
	{
		if (index < 0 || index >= numObjects)
			return -1;
		else
			return typeList[index];
	}
}