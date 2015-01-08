/*
 * Name of code: ObjectCollision.java
 * Description: Handles collision between units and objects.
 * Programmer Name: Joel Angelone
 * Date of last modification: 4/28/06
 */

import com.golden.gamedev.object.*;
import com.golden.gamedev.object.collision.*;
import java.awt.Point;

public class ObjectCollision extends CollisionGroup
{
	// BUFFER is the difference between the actual size of the sprite and
	// the rectangle used for collision
	public static final int BUFFER = 24;
	GamePlay gamePlay;
	Point	 dest;
	
	// ObjectCollision() - class constructor
	public ObjectCollision(GamePlay parent)
	{
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
	// is ordered to path around the object blocking its path.
	public void collided(Sprite s1, Sprite s2)
	{	
		// identify the unit based on the sprite ID
		int id = s1.getID();
	
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
		
		// don't worry about dead units
		if (!unit.isAlive())
			return;
		
		// stopped/standground units don't need to path
		if (unit.getState() == Unit.STOP || unit.getState() == Unit.STAND_GROUND)
			return;
		
		revertPosition1();
		int side = getCollisionSide();
		dest = unit.getDestination();
		
		// no need to path if the destination is unreachable - inside the object blocking the path
		if (dest.x >= s2.getX() - Unit.SIZE && dest.x <= s2.getX() + s2.getWidth() &&
			dest.y >= s2.getY() - Unit.SIZE && dest.y <= s2.getY() + s2.getHeight())
				unit.stop();
		
		// Collision heading west or east
		if (side == 1 || side == 2)
		{
			int distanceUp = (int)s1.getY() - ((int)s2.getY() - Unit.SIZE);
			int distanceDown = (int)s1.getY() - ((int)s2.getY() + (int)s2.getHeight());
			
			if (dest.y > s1.getY())
				unit.path((int)s1.getX(), (int)s1.getY() + Math.abs(distanceDown));
			else
				unit.path((int)s1.getX(), (int)s1.getY() - Math.abs(distanceUp));
		}
		
		// Collision heading north or south
		else if (side == 4 || side == 8)
		{
			int distanceLeft = (int)s1.getX() - ((int)s2.getX() - Unit.SIZE);
			int distanceRight = (int)s1.getX() - ((int)s2.getX() + (int)s2.getWidth());
			
			if (dest.x > s1.getX())
				unit.path((int)s1.getX() + Math.abs(distanceRight), (int)s1.getY());
			else
				unit.path((int)s1.getX() - Math.abs(distanceLeft), (int)s1.getY());
		}
	}
}