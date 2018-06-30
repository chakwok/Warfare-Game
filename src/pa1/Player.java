package pa1;

import java.util.ArrayList;

import units.Unit;

public class Player 
{
	public static final int NUM_PLAYER_COLORS = 5;
	public static final int RED = 0;
	public static final int BLUE = 1;
	public static final int YELLOW = 2;
	public static final int GREEN = 3;
	public static final int GRAY = 4;
	
	private final String name;
	private final int color;
	private ArrayList<Unit> units = new ArrayList<>();
	
	public Player(String name, int color) 
	{
		this.name = name;
		this.color = color;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public String getName() 
	{ 
		return name; 
	}
	
	public ArrayList<Unit> getUnits()
	{
		return units;
	}
	
	public void addUnit(Unit unit)
	{
		units.add(unit);
	}
	
	public boolean hasUnitWithId(char id) 
	{
		return (getUnitById(id) != null);
	}
	
	public Unit getUnitById(char id)
	{
		for (Unit unit:units)
		{
			if (id == unit.getId())
			{
				return unit;
			}
		}
		
		return null;
	}
	
	public void beginTurn()
	{
		for (Unit unit:units)
		{
			if (unit.isAlive())
			{
				unit.beginTurn();
			}
		}
	}
	
	public boolean hasReadyUnits()
	{
		for (Unit unit:units)
		{
			if (unit.isReady())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean hasUnitsRemaining()
	{
		for (Unit unit:units)
		{
			if (unit.isAlive())
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (name.equals(((Player)(obj)).getName()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
