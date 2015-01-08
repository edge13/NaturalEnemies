/*
 * Name of code: Projectile.java
 * Description:  Contains data and functionality for a single projectile
 * Programmer:	 Joel Angelone
 * Date of last modification: 4/16/06
 */

import com.golden.gamedev.object.*;
import java.awt.Graphics2D;
import java.awt.Point;

public class Projectile
{
	// types of projectiles
	public static final int ARROW = 0;
	public static final int FIREBALL = 1;
	public static final int HEAL = 2;
	public static final int LIGHTNING = 3;
	
	private GamePlay gamePlay;
	private Unit ownerUnit;
	private Unit targetUnit;
	private AnimatedSprite sprite;
	private boolean alive;
	private int type;
	
	// Projectile() - class constructor
	// Initializes the sprite based on the type. Sets up the animation
	public Projectile(Unit owner, int projectileType, GamePlay p)
	{
		sprite = new AnimatedSprite();
		sprite.setLoopAnim(true);
		sprite.setAnimate(true);
		alive = false;
		ownerUnit = owner;
		gamePlay = p;
		type = projectileType;
		
		if (type == ARROW)
			sprite.setImages(gamePlay.getImages("Graphics/Projectiles/arrow.png", 8, 1));
		else if (type == FIREBALL)
			sprite.setImages(gamePlay.getImages("Graphics/Projectiles/fireball.png", 8, 1));
		else if (type == HEAL)
			sprite.setImages(gamePlay.getImages("Graphics/Abilities/heal.png", 8, 1));
		else if (type == LIGHTNING)
			sprite.setImages(gamePlay.getImages("Graphics/Abilities/lightning.png", 3, 1));
	}
	
	// spawn()
	// Spawns the projectile heading toward the specified target
	public void spawn(Unit target, int direction)
	{	
		alive = true;
		targetUnit = target;
		
		int x = (int)ownerUnit.sprite.getX();
		int y = (int)ownerUnit.sprite.getY();
		
		if (type == FIREBALL)
		{
			sprite.setAnimationFrame(direction, direction);
			gamePlay.playSound("Sounds/Effects/fireball.wav");
		}
		else if (type == ARROW)
		{
			sprite.setAnimationFrame(direction, direction);
			gamePlay.playSound("Sounds/Effects/arrow.wav");
		}
		else if (type == HEAL)
		{
			sprite.setAnimationFrame(0, 7);
			gamePlay.playSound("Sounds/Effects/heal.wav");
		}
		
		else if (type == LIGHTNING)
		{
			sprite.setAnimationFrame(0, 2);
			gamePlay.playSound("Sounds/Effects/lightning.wav");
		}
		
		sprite.setX(x + 32);
		sprite.setY(y + 20);
	}
	
	// update()
	// Moves the projectile toward it's target.  Performs the damage calculation once the projectile
	// reaches it's target.
	public void update(long elapsedTime)
	{
		if (alive)
		{
			if (sprite.moveTo(elapsedTime, targetUnit.sprite.getX() + Unit.SIZE/2, targetUnit.sprite.getY() + Unit.SIZE/2, .3))
			{
				if (type == HEAL)
					targetUnit.reduceHp(-50);
				else if (type == LIGHTNING)
					targetUnit.reduceHp(60);
				else
					targetUnit.reduceHp(ownerUnit.calcDamage());
				alive = false;
			}
			
			sprite.update(elapsedTime);
		}	
	}
	
	// render()
	// draws the projectile to the screen;
	public void render(Graphics2D g, Point camera)
	{
		if (alive)
			sprite.render(g, (int)sprite.getX() - camera.x, (int)sprite.getY() - camera.y);
	}
	
	// ******
	// Getters
	// ******
	
	public boolean isAlive()
	{
		return alive;
	}
}
