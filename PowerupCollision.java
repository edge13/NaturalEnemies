/*
 * Name of code: PowerupCollision.java
 * Description: Handles collision between units and powerups.
 * Programmer Name: Joel Angelone
 * Date of last modification: 4/28/06
 */

import com.golden.gamedev.object.*;
import com.golden.gamedev.object.collision.*;

public class PowerupCollision extends CollisionGroup
{
	GamePlay gamePlay;
	
	// PowerupCollision() - class constructor
	public PowerupCollision(GamePlay parent)
	{
		gamePlay = parent;
	}
	
//	 collided()
	// This method is called whenever a collision is detected.  The powerup is used up
	// and the bonus is applied to the colliding unit.
	public void collided(Sprite s1, Sprite s2)
	{	
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
		
		int id2 = s2.getID();
		
		// dead units can't get powerups
		if (!gamePlay.powerups[id2].isAlive())
			return;
		
		// full heal
		if (gamePlay.powerups[id2].getType() == Powerup.HEALTH)
			unit.reduceHp(-999);
		else
			unit.doublePower();
		
		gamePlay.powerups[id2].kill();
		
		gamePlay.playSound("Sounds/Effects/powerup.wav");
	}
}