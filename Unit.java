/*
 * Name of code: Unit.java
 * Description:  Holds data and functionality for a single unit.
 * Programmer:	 Joel Angelone
 * Date of last modification: 5/02/06
 */

import com.golden.gamedev.object.*;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.awt.Point;

public class Unit
{
	// types of units
	public static final int KNIGHT = 1;
	public static final int WIZARD = 2;
	public static final int CLERIC = 3;
	public static final int ARCHER = 4;
	public static final int NINJA = 5;
	public static final int SKELETON = -1;
	public static final int SKELETON_ARCHER = -2;
	public static final int ORC = -3;
	public static final int PIRATE = -4;
	public static final int COMMANDER = -5;
	
	// constants
	public static final int SIZE = 96;
	public static final int SIGHT_RADIUS = 350;
	
	// unit states
	public static final int STOP = 0;
	public static final int MOVE = 1;
	public static final int ATTACK = 2;
	public static final int STAND_GROUND = 3;
	public static final int ATTACK_MOVE = 4;
	public static final int DEAD = -1;
	
	// special ability types
	public static final int NONE = -1;
	public static final int HEAL = 5;
	public static final int LIGHTNING = 6;
	
	// movement directions
	public static final int SOUTH = 0;
	public static final int SOUTHWEST = 1;
	public static final int WEST = 2;
	public static final int NORTHWEST = 3;
	public static final int NORTH = 4;
	public static final int NORTHEAST = 5;
	public static final int EAST = 6;
	public static final int SOUTHEAST = 7;
	
	public AnimatedSprite sprite;
	private BufferedImageOp op;
	private BufferedImage selectionCircle;
	private GamePlay gamePlay;
	
	private Projectile specialAttack;
	private Projectile projectile;
	private Unit target;
	private Point destination;
	private Point originalDest;
	private Point attackDest;
	private int state;
	private int direction;
	private boolean selected;
	private boolean swinging;
	private boolean alive;
	private boolean pathing;
	private boolean dontPath;
	private int previousState;
	private int delay;
	private boolean isAttackMove;
	private boolean isStandGround;
	private int specialType;
	private int specialCooldown;
	
	// attributes
	private int type;
	private int maxHp;
	private int currentHp;
	private int attackRange;
	private int attackDelay;
	private int armor;
	private int power;
	private String range;
	private double speed;
	private boolean hasProjectile;
	private boolean isFriendly;
	
	// Unit() - class constructor
	// initializes data and attributes based on the type parameter
	public Unit(GamePlay parent, int id, int unitType)
	{
		direction = SOUTH;
		selected = false;
		target = null;
		swinging = false;
		alive = true;
		pathing = false;
		gamePlay = parent;
		delay = 0;
		previousState = -1;
		type = unitType;
		dontPath = false;
		isAttackMove = false;
		isStandGround = false;
		specialType = NONE;
		
		destination = new Point();
		originalDest = new Point();
		attackDest = new Point();
		selectionCircle = parent.getImage("Graphics/Misc/selectionCircle.png");
		sprite = new AnimatedSprite();
		sprite.setLoopAnim(true);
		sprite.setAnimate(true);
		sprite.setAnimationTimer(new Timer(100));
		sprite.setID(id);
		
		// attributes are based on type
		if (type == KNIGHT)
		{
			sprite.setImages(parent.getImages("Graphics/Units/knight.png", 43, 8));
			maxHp = 240;
			speed = 0.1;
			attackRange = 40;
			attackDelay = 80;
			hasProjectile = false;
			power = 20;
			armor = 10;
			isFriendly = true;
			range = "Melee";
		}
		
		else if (type == WIZARD)
		{
			specialAttack = new Projectile(this, Projectile.LIGHTNING, parent);
			projectile = new Projectile(this, Projectile.FIREBALL, parent);
			sprite.setImages(parent.getImages("Graphics/Units/wizard.png", 43, 8));
			maxHp = 95;
			speed = 0.1;
			attackRange = 185;
			attackDelay = 95;
			hasProjectile = true;
			power = 45;
			armor = 2;
			isFriendly = true;
			specialType = LIGHTNING;
			range = "Medium";
		}
		
		else if (type == CLERIC)
		{
			specialAttack = new Projectile(this, Projectile.HEAL, parent);
			sprite.setImages(parent.getImages("Graphics/Units/cleric.png", 43, 8));
			maxHp = 70;
			speed = 0.1;
			attackRange = 40;
			attackDelay = 80;
			hasProjectile = false;
			power = 10;
			armor = 3;
			isFriendly = true;
			specialType = HEAL;
			range = "Melee";
		}
		
		else if (type == ARCHER)
		{
			projectile = new Projectile(this, Projectile.ARROW, parent);
			sprite.setImages(parent.getImages("Graphics/Units/archer.png", 43, 8));
			maxHp = 125;
			speed = 0.1;
			attackRange = 270;
			attackDelay = 65;
			hasProjectile = true;
			power = 15;
			armor = 2;
			isFriendly = true;
			range = "Long";
		}
		
		if (type == NINJA)
		{
			sprite.setImages(parent.getImages("Graphics/Units/ninja.png", 43, 8));
			maxHp = 170;
			speed = 0.1;
			attackRange = 40;
			attackDelay = 50;
			hasProjectile = false;
			power = 20;
			armor = 4;
			isFriendly = true;
			range = "Melee";
		}
		
		else if (type == SKELETON)
		{
			sprite.setImages(parent.getImages("Graphics/Units/skeleton.png", 43, 8));
			maxHp = 185;
			speed = 0.1;
			attackRange = 40;
			attackDelay = 80;
			hasProjectile = false;
			power = 25;
			armor = 8;
			isFriendly = false;
		}
		
		else if (type == SKELETON_ARCHER)
		{
			projectile = new Projectile(this, Projectile.ARROW, parent);
			sprite.setImages(parent.getImages("Graphics/Units/skeletonArcher.png", 43, 8));
			maxHp = 145;
			speed = 0.1;
			attackRange = 300;
			attackDelay = 65;
			hasProjectile = true;
			power = 13;
			armor = 2;
			isFriendly = false;
		}
		
		if (type == ORC)
		{
			sprite.setImages(parent.getImages("Graphics/Units/orc.png", 43, 8));
			maxHp = 340;
			speed = 0.1;
			attackRange = 40;
			attackDelay = 80;
			hasProjectile = false;
			power = 5;
			armor = 6;
			isFriendly = false;
		}
		
		if (type == PIRATE)
		{
			sprite.setImages(parent.getImages("Graphics/Units/pirate.png", 43, 8));
			maxHp = 200;
			speed = 0.1;
			attackRange = 40;
			attackDelay = 70;
			hasProjectile = false;
			power = 18;
			armor = 8;
			isFriendly = false;
		}
		
		if (type == COMMANDER)
		{
			sprite.setImages(parent.getImages("Graphics/Units/commander.png", 43, 8));
			maxHp = 1840;
			speed = 0.1;
			attackRange = 40;
			attackDelay = 120;
			hasProjectile = false;
			power = 85;
			armor = 36;
			isFriendly = false;
		}
		
		// higher difficulty level gives enemies higher hp
		// 15% for medium, 30% for hard
		if (isFriendly == false && gamePlay.getDifficulty() == GamePlay.NORMAL)
			maxHp += maxHp*.15;
		if (isFriendly == false && gamePlay.getDifficulty() == GamePlay.HARD)
			maxHp += maxHp*.3;
		
		currentHp = maxHp;
		stop();
	}
	
	// update()
	// updates movement and animation based on the current state of the unit
	public void update(long elapsedTime)
	{
		// boundary check
		if (elapsedTime > 100)
		{
			if (sprite.getX() < 0)
				sprite.setX(0);
			if (sprite.getY() < 0)
				sprite.setY(0);
			if (sprite.getX() > (Map.SIZE - SIZE))
				sprite.setX(Map.SIZE - SIZE);
			if (sprite.getY() > (Map.SIZE - SIZE));
				sprite.setY(Map.SIZE - SIZE);
		}
		
		switch (state)
		{
			case MOVE:
				// reset the dontPath flag - we always want to path when moving
				if (dontPath)
					dontPath = false;
				
				// if the unit is attack-moving then check for nearby enemies
				if (isAttackMove && !pathing)
					if (attackProximityEnemy())
						break;
				
				// continue toward the destination
				if (sprite.moveTo(elapsedTime, destination.x, destination.y, speed))
				{
					// if our destination is a waypoint (we're pathing) then continue the
					// previous action once the pathing is complete
					if (pathing)
					{
						pathing = false;
						state = previousState;
						if (state == MOVE) move(originalDest.x, originalDest.y);
						if (state == ATTACK) attack(target);
					}
					
					// otherwise we've reached our goal and can stop
					else
					{
						stop();
						
						// we're done attack-moving in this case
						if (isAttackMove)
							if (destination.x == attackDest.x && destination.y == attackDest.y)
								isAttackMove = false;
					}
				}
			break;
				
			case ATTACK:
				// if the target is dead, stop and check for nearby enemies
				if (!target.isAlive())
				{
					stop();
					break;
				}
				
				// refuse to path (move out of the way for other units) if the unit is in range
				// of its target
				if (distanceFromTarget(target) <= attackRange + 64)
					dontPath = true;
				else
					dontPath = false;
				
				// attack is in progress
				if (swinging)
				{
					// wait for the last frame of attack animation
					if (sprite.getFrame() == direction*43 + 29)
					{
						// spawn a projectile if necessary, otherwise just do damage
						if (hasProjectile)
							projectile.spawn(target, direction);
						else
						{
							gamePlay.playSound("Sounds/Effects/sword.wav");
							target.reduceHp(calcDamage());
						}
						
						// dont move if the unit is standing ground
						if (!isStandGround)
						{
							move((int)target.sprite.getX(), (int)target.sprite.getY());
							state = ATTACK;
						}
						else
							stop();
					}
				}
				
				// attack is just starting
				if (!swinging)
				{
					// check if the target is in range
					if (distanceFromTarget(target) <= attackRange + 64)
					{
						// Hold next attack until delay finishes
						if (delay < attackDelay)
						{
							sprite.setAnimationFrame(direction*43 + 17, direction*43 + 17);
						}
						
						// start swinging at the enemy
						else
						{
							delay = 0;
							sprite.setAnimationFrame(direction*43 + 17, direction*43 + 29);
							swinging = true;
						}
					}
					
					// if the unit isn't in range, move toward the target
					else
					{
						// unless the unit is standing ground
						if (!isStandGround)
							sprite.moveTo(elapsedTime, target.sprite.getX(), target.sprite.getY(), speed);
						else
							stop();
					}
				}
			break;
			
			case STOP:
				// if the unit stops, make sure it doesn't have an attack-move assignment
				if (isAttackMove)
					move(attackDest.x, attackDest.y);
				
				// check for a nearby enemy to attack
				attackProximityEnemy();
			break;
			
			// special abilities
			// same functionality as attack
			case LIGHTNING:
			case HEAL:
				if (!target.isAlive())
				{
					stop();
					break;
				}
				
				if (distanceFromTarget(target) <= 200)
					dontPath = true;
				else
					dontPath = false;
				
				if (swinging)
				{
					if (sprite.getFrame() == direction*43 + 29)
					{
						specialAttack.spawn(target, direction);
						stop();
					}
				}
			
				else if (!swinging)
				{
					if (distanceFromTarget(target) <= 200)
					{
						specialCooldown = 450;
						sprite.setAnimationFrame(direction*43 + 17, direction*43 + 29);
						swinging = true;
					}
					else
					{
						if (!isStandGround)
							sprite.moveTo(elapsedTime, target.sprite.getX(), target.sprite.getY(), speed);
						else
							stop();
					}
				}
			break;		
		}
		
		// if the unit is dead, hold it's animation frame
		if (sprite.getFrame() == direction*43 + 42)
			sprite.setAnimationFrame(direction*43 + 42, direction*43 + 42);
		
		// update special ability cooldown and attack delay
		if (specialCooldown > 0)
			specialCooldown--;
		if (delay < attackDelay)
			delay++;
		
		// sprite update routines
		sprite.update(elapsedTime);
		
		if (hasProjectile)
			projectile.update(elapsedTime);
		if (specialType != NONE)
			specialAttack.update(elapsedTime);
	}
	
	// render()
	// draws the unit on the screen
	public void render(Graphics2D g, Point camera)
	{
		int x = (int)sprite.getX() - camera.x;
		int y = (int)sprite.getY() - camera.y;
		
		// selected units appear with a green selection circle underneath them
		if (selected)
			g.drawImage(selectionCircle, op, x + 18, y + SIZE - 36);
		
		sprite.render(g, x, y);	
		
		// draw any projectiles or special abilities
		if (hasProjectile)
			projectile.render(g, camera);
		if (specialType != NONE)
			specialAttack.render(g, camera);
	}
	
	// move()
	// Starts moving to the specified location
	public void move(int destX, int destY)
	{
		if (isStandGround)
			return;
		
		state = MOVE;
		destination.x = destX;
		destination.y = destY;
		swinging = false;
		
		// calculate the direction the unit is going
		int dx = destination.x - (int)sprite.getX();
		int dy = destination.y - (int)sprite.getY();
		
		double ratio = (double)Math.abs(dx) / (double) Math.abs(dy);
		
		if (dx == 0)
		{
			if (dy < 0)
				direction = NORTH;
			else if (dy > 0)
				direction = SOUTH;
		}
		
		else if (dx < 0)
		{
			if (dy == 0)
				direction = WEST;
			
			else if (dy < 0)
			{
				if (ratio < .4)
					direction = NORTH;
				else if (ratio > 1.6)
					direction = WEST;
				else
					direction = NORTHWEST;
			}
			else if (dy > 0)
			{
				if (ratio < .4)
					direction = SOUTH;
				else if (ratio > 1.6)
					direction = WEST;
				else
					direction = SOUTHWEST;
			}
		}
		
		else if (dx > 0)
		{
			if (dy == 0)
				direction = EAST;
			
			else if (dy < 0)
			{
				if (ratio < .4)
					direction = NORTH;
				else if (ratio > 1.6)
					direction = EAST;
				else
					direction = NORTHEAST;
			}
			else if (dy > 0)
			{
				if (ratio < .5)
					direction = SOUTH;
				else if (ratio > 1.6)
					direction = EAST;
				else
					direction = SOUTHEAST;
			}
		}
		
		// set the animation appropriately
		sprite.setAnimationFrame(direction*43+9,direction*43+16);
	}
	
	// path()
	// Flags the unit for pathing and starts moving to the specified location
	public void path(int destX, int destY)
	{
		// Keep track of what the unit was previously doing
		if (!pathing)
		{
			pathing = true;
			previousState = state;
			originalDest.x = destination.x;
			originalDest.y = destination.y;
		}
		
		move(destX, destY);
	}
	
	// shouldPath()
	// Returns true if the unit is allowed to path, false otherwise
	public boolean shouldPath()
	{
		return !dontPath;
	}
	
	// stop()
	// orders the unit to stop
	public void stop()
	{
		state = STOP;
		swinging = false;
		sprite.setAnimationFrame(direction*43, direction*43 + 8);
	}
	
	// standGround()
	// orders the unit to stand ground
	public void standGround()
	{
		isStandGround = true;
		if (state == MOVE) stop();
	}
	
	// attack()
	// orders the unit to attack the specified target
	public void attack(Unit t)
	{
		target = t;
		if (!swinging)
			move((int)target.sprite.getX(), (int)target.sprite.getY());
		state = ATTACK;
	}
	
	// attackMove()
	// orders the unit to attack-move to the specified location
	public void attackMove(int destX, int destY)
	{
		attackDest.x = destX;
		attackDest.y = destY;
		isAttackMove = true;
		move(attackDest.x, attackDest.y);
	}
	
	// heal()
	// orders the unit to perform the heal special ability
	public void heal(Unit t)
	{
		target = t;
		swinging = false;
		move((int)target.sprite.getX(), (int)target.sprite.getY());
		state = HEAL;
	}
	
	// lightning()
	// orders the unit to perform the lightning special ability
	public void lightning(Unit t)
	{
		target = t;
		swinging = false;
		move((int)target.sprite.getX(), (int)target.sprite.getY());
		state = LIGHTNING;
	}
	
	// reset()
	// clears all movement related flags for the unit so a fresh command can be issued
	public void reset()
	{
		pathing = false;
		isAttackMove = false;
		isStandGround = false;
	}
	
	// select()
	// flags the unit as selected
	public void select()
	{
		selected = true;
	}
	
	// unSelect()
	// resets the selected flag
	public void unSelect()
	{
		selected = false;
	}
	
	// setCurrentHp()
	// sets the unit's current hp to the value specified
	public void setCurrentHp(int hp)
	{
		currentHp = hp;
		
		// safeguard
		if (currentHp < 0) currentHp = 0;
		if (currentHp > maxHp) currentHp = maxHp;
	}
	
	// reduceHp()
	// Reduces the unit's hp by the given amount.  Checks if the unit is dead and initiates the
	// death sequence if so
	public void reduceHp(int delta)
	{
		// friendly units can't be damaged in god mode
		if (gamePlay.isGodMode())
		{
			if (isFriendly && delta > 0)
				return;
		}
		
		// enemy units immediately die if one-hit-kills is enabled
		if (gamePlay.isOneHitKills())
		{
			if (!isFriendly)
				currentHp -= 9999;
		}
		
		currentHp -= delta;
		if (currentHp <= 0)
		{
			currentHp = 0;
			alive = false;
			sprite.setLoopAnim(false);
			sprite.setAnimationFrame(direction*43 + 30, direction*43 + 42);
			gamePlay.playSound("Sounds/Effects/death.wav");
			state = DEAD;
			
			if (selected)
			{
				selected = false;
				gamePlay.unSelect();
			}
		}
		
		if (currentHp >= maxHp)
			currentHp = maxHp;
	}
	
	// calcDamage()
	// calculates and returns the amount of damage the unit should do to it's target
	public int calcDamage()
	{
		int damage = power;
		float reduction = (float)power*(float)target.getArmor()/(float)100;
		damage -= reduction;
		return damage;
	}
	
	// distanceFromTarget()
	// returns the distance between the unit and the target unit passed as a parameter (in pixels)
	private int distanceFromTarget(Unit targ)
	{
		if (targ == null)
			return -1;
		
		double dx = sprite.getX() + (SIZE/2) - (targ.sprite.getX() + (SIZE/2));
		double dy = sprite.getY() + (SIZE/2) - (targ.sprite.getY() + (SIZE/2));
		
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	// distanceFromPoint()
	// returns the distance between the unit and the specified location
	public int distanceFromPoint(int x, int y)
	{
		double dx = sprite.getX() + (SIZE/2) - x;
		double dy = sprite.getY() + (SIZE/2) - y;
		
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	// attackProximityEnemy()
	// Rinds a nearby enemy to attack if possible.  Returns true if an enemy is found, false otherwise.
	private boolean attackProximityEnemy()
	{
		int closest = 0;
		
		// friendly units look for enemy units
		if (isFriendly == true)
		{
			for (int i = 0; i < gamePlay.getNumEnemyUnits(); i++)
			{
				// ignore dead units
				if (!gamePlay.enemyUnits[i].isAlive())
					continue;
				
				int dist = distanceFromTarget(gamePlay.enemyUnits[i]);
				
				if (dist < closest || closest == 0)
				{
					closest = dist;
					target = gamePlay.enemyUnits[i];
				}
			}
			
			// If the closest unit is in sight, attack it
			if (closest < SIGHT_RADIUS && closest != 0)
			{
				attack(target);
				return true;
			}
		}
		
		// enemy units search for friendly units
		else
		{
			for (int i = 0; i < gamePlay.getNumFriendlyUnits(); i++)
			{
				// ignore dead units
				if (!gamePlay.friendlyUnits[i].isAlive())
					continue;
				
				int dist = distanceFromTarget(gamePlay.friendlyUnits[i]);
				
				if (dist < closest || closest == 0)
				{
					closest = dist;
					target = gamePlay.friendlyUnits[i];
				}
			}
			
//			 If the closest unit is in sight, attack it
			if (closest < SIGHT_RADIUS && closest != 0)
			{
				attack(target);
				return true;
			}				
		}
		
		return false;
	}
	
	// ******
	// Getters
	// ******
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public int getMaxHp()
	{
		return maxHp;
	}
	
	public int getCurrentHp()
	{
		return currentHp;
	}
	
	public int getType()
	{
		return type;
	}
	
	public boolean isAlive()
	{
		return alive;
	}
	
	public int getState()
	{
		return state;
	}
	
	public Point getDestination()
	{
		return destination;
	}
	
	public boolean isSwinging()
	{
		return swinging;
	}
	
	public boolean isStandingGround()
	{
		return isStandGround;
	}
	
	public int getSpecialType()
	{
		return specialType;
	}
	
	public int getCooldown()
	{
		return specialCooldown;
	}
	
	public int getArmor()
	{
		return armor;
	}
	
	public int getPower()
	{
		return power;
	}
	
	public String getRange()
	{
		return range;
	}
	
	public void doublePower()
	{
		power *= 2;
	}
}