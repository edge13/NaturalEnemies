/*
 * Name of code: Powerup.java
 * Description:  Contains data and functionality for a single powerup item
 * Programmer:	 Joel Angelone
 * Date of last modification: 5/4/06
 */

import java.awt.*;
import com.golden.gamedev.object.*;

public class Powerup
{
	// powerup types
	public static final int HEALTH = 0;
	public static final int POWER = 1;
	
	public AnimatedSprite sprite;
	private int type;
	private boolean alive;
	
	// Powerup() - class constructor
	// Loads the graphics and animation based on the type
	public Powerup(GamePlay parent, int id, int powerupType)
	{
		alive = true;
		type = powerupType;
		
		if (type == HEALTH)
		{
			sprite = new AnimatedSprite(parent.getImages("Graphics/Powerups/health.png", 16, 1));
			sprite.setAnimationFrame(0, 15);
			sprite.setAnimationTimer(new Timer(120));
		}
		else
		{
			sprite = new AnimatedSprite(parent.getImages("Graphics/Powerups/power.png", 2, 1));
			sprite.setAnimationFrame(0, 1);
			sprite.setAnimationTimer(new Timer(240));
		}
		
		sprite.setID(id);
		sprite.setAnimate(true);
		sprite.setActive(true);
		sprite.setLoopAnim(true);
	}
	
	// render()
	// Draws the powerup to the screen
	public void render(Graphics2D g, Point camera)
	{
		if (alive)
			sprite.render(g, (int)sprite.getX() - camera.x, (int)sprite.getY() - camera.y);
	}
	
	// update()
	// Updates the powerup's animated sprite
	public void update(long elapsedTime)
	{
		sprite.update(elapsedTime);
	}
	
	// kill()
	// Destroys the powerup (it's used up)
	public void kill()
	{
		alive = false;
	}
	
	// ******
	// Getters
	// ******
	
	public boolean isAlive()
	{
		return alive;
	}
	
	public int getType()
	{
		return type;
	}
}
