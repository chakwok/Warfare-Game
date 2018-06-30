package pa1;

import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import exceptions.*;
import units.*;
import terrain.*;



public class GameEngine 
{
	private ArrayList<Player> players = new ArrayList<>();
	private Player currentPlayer = null;
	private int currentPlayerIndex = 0;
	private boolean isLoaded = false; // For making sure that GameEngine is loaded correctly with Players and Units.
	
	public boolean isLoaded()
	{
		return isLoaded;
	}
	
	public Player getCurrentPlayer() 
	{
		return currentPlayer;
	}

	public ArrayList<Player> getPlayers() 
	{
		return players;
	}
	
	public void loadPlayersAndUnits(File file) throws IOException
	{
		// try-with-resources
		try (Scanner scanner = new Scanner(file))
		{
			// Temporary Global Unit List for Duplicate Unit ID Checking.
			ArrayList<Unit> globalUnitList = new ArrayList<Unit>();
			
			int numPlayers = scanner.nextInt();
			if ((numPlayers < 2) || (numPlayers > 5))
			{
				throw new InvalidNumberOfPlayersException(numPlayers);
			}
			
			for (int n = 0; n < numPlayers; n++)
			{
				String name = scanner.next();
				Player player = new Player(name, n);
				
				if (players.contains(player))
				{
					throw new DuplicatePlayerNameException(name);
				}
				
				int numUnits = scanner.nextInt();
				for (int u = 0; u < numUnits; u++)
				{
					char unitId = (scanner.next()).charAt(0);
					String unitType = scanner.next();
					Unit unit;
					
					switch (unitType)
					{
					case "Infantry":
						unit = new Infantry(unitId, n);
						break;
					case "Pikeman":
						unit = new Pikeman(unitId, n);
						break;
					case "Cavalry":
						unit = new Cavalry(unitId, n);
						break;
					case "Archer":
						unit = new Archer(unitId, n);
						break;
					default:
						throw new InvalidUnitTypeException(unitType);
					}
					
					if (globalUnitList.contains(unit))
					{
						throw new DuplicateUnitIDException(unitId);
					}
					
					globalUnitList.add(unit);
					player.addUnit(unit);
				}
				
				players.add(player);
			}
			
			currentPlayer = players.get(0);
			currentPlayer.beginTurn();
			isLoaded = true;
		}
		
		catch (IOException e)
		{
			unloadPlayersAndUnits();
			throw e;
		}
		
		catch (NoSuchElementException e)
		{
			unloadPlayersAndUnits();
			throw new InvalidInputFormatException(); // This occurs if the PlayersAndUnits textfile has format errors.
		}
	}
	
	public void unloadPlayersAndUnits()
	{
		players.clear();
		currentPlayer = null;
		currentPlayerIndex = 0;
		isLoaded = false;
	}
	
	public boolean isEnemyUnitInRange(Unit currentUnit)
	{
		boolean[][] attackMap = currentUnit.getAttackMap();
		for (int attackMapY = 0; attackMapY < attackMap.length; attackMapY++)
		{
			for (int attackMapX = 0; attackMapX < attackMap[attackMapY].length; attackMapX++)
			{
				if (attackMap[attackMapY][attackMapX] == true)
				{
					int terrainMapX = currentUnit.attackMapToTerrainMapX(attackMapX);
					int terrainMapY = currentUnit.attackMapToTerrainMapY(attackMapY);
					Terrain currentTile = GameApplication.gameMap.getTerrainAtLocation(terrainMapX, terrainMapY);
					
					if (currentTile.isOccupied())
					{
						if (!currentPlayer.getUnits().contains(currentTile.getOccupyingUnit()))
						{
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isEnemyUnit(Unit targetUnit)
	{
		return (!currentPlayer.getUnits().contains(targetUnit));
	}
	
	public void nextPlayerTurn() 
	{		
		do
		{
			currentPlayerIndex++;
			currentPlayerIndex %= players.size();
			currentPlayer = players.get(currentPlayerIndex);
		}
		while (!currentPlayer.hasUnitsRemaining());
		
		currentPlayer.beginTurn();
	}
	
	public boolean isGameOver() 
	{
		int playersRemaining = 0;
		
		for (Player player:players) 
		{
			if (player.hasUnitsRemaining())
			{
				playersRemaining += 1;
			}
		}
		
		return (playersRemaining <= 1);
	}
}
