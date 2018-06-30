package units;

import javafx.scene.image.Image;
import pa1.GameApplication;
//import pa1.Player;

public abstract class Unit 
{
	protected static final int MAX_HEALTH = 10;
	protected static final int HEAL_RATE = 3;
	
	protected final char ID;
	protected final int COLOR;
	protected int health = MAX_HEALTH;
	protected final int ATTACK;
	protected final int DEFENSE;
	protected final int ATTACK_RANGE;
	protected final int MOVEMENT_RANGE;
	protected int locationX = -1;
	protected int locationY = -1;
	protected boolean isReady = false;
	protected final boolean[][] movementMap;
	protected final boolean[][] attackMap;
	
	protected Unit(char id, int color, int attack, int defense, int attackRange, int movementRange)
	{
		this.ID = id;
		this.COLOR = color;
		this.ATTACK = attack;
		this.DEFENSE = defense;
		this.ATTACK_RANGE = attackRange;
		this.MOVEMENT_RANGE = movementRange;
				
		// range * 2 + 1, because we can go range-distance to up/left AND down-right, plus the square we are currently on.
		this.movementMap = new boolean[(MOVEMENT_RANGE * 2) + 1][(MOVEMENT_RANGE * 2) + 1]; // default is false.
		
		this.attackMap = new boolean[(ATTACK_RANGE * 2) + 1][(ATTACK_RANGE * 2) + 1];
		int center = ATTACK_RANGE; // int ((ATTACK_RANGE * 2) + 1)/2 == ATTACK_RANGE;
		
		for (int y = 0; y < (ATTACK_RANGE * 2) + 1; y++)
		{
			for (int x = 0; x < (ATTACK_RANGE * 2) + 1; x++)
			{
				if ((Math.abs(x - center) + Math.abs(y - center)) <= ATTACK_RANGE)
				{
					attackMap[y][x] = true;
				}
			}
		}
	}
	
	protected String getStatus()
	{
		if (!isAlive())
		{
			return "DEAD";
		}
		
		if (isReady)
		{
			return "READY";
		}
		else
		{
			return "DONE";
		}
	}
	
	public abstract Image getImage();
	
	public char getId() 
	{
		return ID;
	}

	public int getAttackRange()
	{
		return ATTACK_RANGE;
	}
	
	public int getMovementRange() 
	{
		return MOVEMENT_RANGE;
	}

	public boolean[][] getAttackMap()
	{
		return attackMap;
	}
	
	public boolean[][] getMovementMap()
	{
		return movementMap;
	}
	
	public int getLocationX() 
	{
		return locationX;
	}

	public int getLocationY()
	{
		return locationY;
	}
	
	
	
	//============================
	// Coordinate Transformations. // int ((MOVEMENT_RANGE * 2) + 1)/2 == MOVEMENT_RANGE; Same for ATTACK_RANGE.
	//============================
	public int movementMapToTerrainMapX(int movementMapX)
	{
		return (movementMapX - MOVEMENT_RANGE + locationX);
	}
	
	public int movementMapToTerrainMapY(int movementMapY)
	{
		return (movementMapY - MOVEMENT_RANGE + locationY);
	}
	
	public int terrainMapToMovementMapX(int terrainMapX)
	{
		return (terrainMapX + MOVEMENT_RANGE - locationX);
	}
	
	public int terrainMapToMovementMapY(int terrainMapY)
	{
		return (terrainMapY + MOVEMENT_RANGE - locationY);
	}
	
	public int attackMapToTerrainMapX(int attackMapX)
	{
		return (attackMapX - ATTACK_RANGE + locationX);
	}
	
	public int attackMapToTerrainMapY(int attackMapY)
	{
		return (attackMapY - ATTACK_RANGE + locationY);
	}
	
	public int terrainMapToAttackMapX(int terrainMapX)
	{
		return (terrainMapX + ATTACK_RANGE - locationX);
	}
	
	public int terrainMapToAttackMapY(int terrainMapY)
	{
		return (terrainMapY + ATTACK_RANGE - locationY);
	}
	//============================
	
	

	public boolean isReady() 
	{
		return isReady;
	}
	
	public void beginTurn()
	{
		isReady = true;
	}
	
	public void endTurn()
	{
		isReady = false;
	}
	
	public int getHealth() 
	{
		return health;
	}
	
	public boolean isAlive()
	{
		return (health > 0);
	}
	
	public void setStartingLocation(int locationX, int locationY)
	{
		this.locationX = locationX;
		this.locationY = locationY;
		GameApplication.gameMap.getTerrainAtLocation(locationX, locationY).occupy(this);
	}
	
	public void resetStartingLocation()
	{
		this.locationX = -1;
		this.locationY = -1;
	}
	
	public void move(int locationX, int locationY)
	{
		GameApplication.gameMap.getTerrainAtLocation(this.locationX, this.locationY).unoccupy();
		this.locationX = locationX;
		this.locationY = locationY;
		GameApplication.gameMap.getTerrainAtLocation(locationX, locationY).occupy(this);
	}
	
	// Damage linearly scaled based on health.
	public void attackUnit(Unit defender)
	{
		double defenderRawDamageReceived = (ATTACK - defender.DEFENSE) * health * 0.1;
		double attackerRawDamageReceived = (defender.ATTACK - DEFENSE) * defender.health * 0.1;
		
		defender.receiveDamage(defenderRawDamageReceived, this);
		this.receiveDamage(attackerRawDamageReceived, defender);
	}
	
	// 50% less/more damage (rounded) received by Advantaged/Disadvantaged TroopType.
	public abstract void receiveDamage(double rawDamage, Unit attacker);
	protected void receiveDamage(double rawDamage)
	{
		int damage = (int)(Math.round(rawDamage));
		health -= damage;
		if (health <= 0)
		{
			health = 0;
			isReady = false;
			GameApplication.gameMap.getTerrainAtLocation(this.locationX, this.locationY).unoccupy();
		}
	}
	
	public void heal()
	{
		health += HEAL_RATE;
		if (health > 10)
		{
			health = 10;
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (ID == ((Unit)(obj)).getId())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
