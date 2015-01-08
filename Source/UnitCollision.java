/*
 * Name of code: UnitCollision.java
 * Description: Handles collision between two units.
 * Programmer Name: Joel Angelone
 * Date of last modification: 4/30/06
 */

import com.golden.gamedev.object.*;
import com.golden.gamedev.object.collision.*;
import java.awt.Point;

public class UnitCollision extends CollisionGroup
{
	// BUFFER is the difference between the actual size of the sprite and
	// the rectangle used for collision
	public static final int BUFFER = 24;
	GamePlay gamePlay;
	Point dest;
	
	// UnitCollision() - class constructor
	public UnitCollision(GamePlay parent)
	{
		super();
		gamePlay = parent;
	}
	
	// getCollisionShape1()
	// Returns the sprite collision bounding box for sprite 1- this is the sprite rectangle minus
	// the BUFFER on each side
	public CollisionShape getCollisionShape1(Sprite s1)
	{
		rect1.setBounds(s1.getX() + BUFFER, s1.getY() + BUFFER, s1.getWidth() - 2*BUFFER, s1.getHeight() - 2*BUFFER);
		return rect1;
	}

	// getCollisionShape2()
	// Returns the sprite collision bounding box for sprite 2- this is the sprite rectangle minus
	// the BUFFER on each side
	public CollisionShape getCollisionShape2(Sprite s2)
	{
		rect2.setBounds(s2.getX() + BUFFER, s2.getY() + BUFFER, s2.getWidth() - 2*BUFFER, s2.getHeight() - 2*BUFFER);
		return rect2;
	}
	
	// collided()
	// This method is called whenever a collision is detected.  The collided unit
	// is ordered to path around the unit blocking its path.
	public void collided(Sprite s1, Sprite s2)
	{	
		// establish unit ID's
		int id = s1.getID();
		int id2 = s2.getID();
		
		Unit unit2;
		
		// Enemy Unit
		if (id2 < 0)
		{
			id2 = Math.abs(id2) - 1;
			unit2 = gamePlay.enemyUnits[id2];
		}
		
		// Friendly Unit
		else
		{
			unit2 = gamePlay.friendlyUnits[id2];	
		}
		
		if (!unit2.isAlive())
			return;
		
		Unit unit;
		
		// Enemy Unit
		if (id < 0)
		{
			id = Math.abs(id) - 1;
			unit = gamePlay.enemyUnits[id];
		}
		
		// Friendly Unit
		else
		{
			unit = gamePlay.friendlyUnits[id];	
		}
		
		// Check and make sure the unit should path
		if (!unit.isAlive())
			return;
		
		if (!unit.shouldPath())
			return;
		
		if (unit.getState() == Unit.STOP || unit.getState() == Unit.STAND_GROUND)
			return;
		
		revertPosition1();
		int side = getCollisionSide();
		dest = unit.getDestination();
			
		// Collision heading west
		if (side == 1)
		{
			int distanceDown = (int)s1.getY() - ((int)s2.getY() + (int)s2.getHeight() - BUFFER);
			unit.path((int)s1.getX(), (int)s1.getY() + Math.abs(distanceDown));
		}
		// Collision heading east
		else if (side == 2)
		{
			int distanceUp = (int)s1.getY() - ((int)s2.getY() - Unit.SIZE + BUFFER);
			unit.path((int)s1.getX(), (int)s1.getY() - Math.abs(distanceUp));
		}
		// Collision heading north
		else if (side == 4)
		{
			int distanceLeft = (int)s1.getX() - ((int)s2.getX() - Unit.SIZE + BUFFER);
			unit.path((int)s1.getX() - Math.abs(distanceLeft), (int)s1.getY());
		}
		//Collision heading south
		else if (side == 8)
		{
			int distanceRight = (int)s1.getX() - ((int)s2.getX() + (int)s2.getWidth() - BUFFER);
			unit.path((int)s1.getX() + Math.abs(distanceRight), (int)s1.getY());
		}
	}
}