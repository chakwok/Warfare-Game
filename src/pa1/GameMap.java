package pa1;

import java.io.File;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.IOException;

import exceptions.*;
import terrain.*;
import units.*;



// Origin (0, 0) is at top-left corner; X-axis increases from left-to-right; Y-axis increases from top-to-bottom.
public class GameMap 
{
	private static final int TERRAIN_PLAINS = 0;
	private static final int TERRAIN_FOREST = 1;
	private static final int TERRAIN_HILLS = 2;
	private static final int TERRAIN_MOUNTAIN = 3;
	private static final int TERRAIN_WATER = 4;
	private static final Terrain TERRAIN_OUT_OF_BOUNDS = new TerrainOutOfBounds(); // Sentinel Terrain, prevents ArrayIndexOutOfBounds and NullPointer Exceptions.
	
	private int width = 0;
	private int height = 0;
	private Terrain[][] terrainMap = null;
	
	private boolean isLoaded = false; // For making sure that GameMap is loaded correctly.
	
	public Terrain getTerrainAtLocation(int terrainMapX, int terrainMapY)
	{
		if (((terrainMapX < 0) || (terrainMapY < 0)) || ((terrainMapX >= width) || (terrainMapY >= height)))
		{
			return TERRAIN_OUT_OF_BOUNDS;
		}
		
		return terrainMap[terrainMapY][terrainMapX];
	}
	
	public int getWidth() 
	{
		return width;
	}
	
	public int getHeight() 
	{
		return height;
	}
	
	// Use this to check if a valid Terrain Map is loaded.
	public boolean isLoaded()
	{
		return isLoaded;
	}
	
	public void loadTerrainMap(File file) throws IOException
	{
		// try-with-resources
		try (Scanner scanner = new Scanner(file))
		{
			width = scanner.nextInt();
			height = scanner.nextInt();
			
			terrainMap = new Terrain[height][width];
			
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					int currentTerrain = scanner.nextInt();
					switch (currentTerrain)
					{
					case TERRAIN_PLAINS:
						terrainMap[y][x] = new Plains();
						break;
						
					case TERRAIN_FOREST:
						terrainMap[y][x] = new Forest();
						break;
						
					case TERRAIN_HILLS:
						terrainMap[y][x] = new Hills();
						break;
						
					case TERRAIN_MOUNTAIN:
						terrainMap[y][x] = new Mountain();
						break;	
						
					case TERRAIN_WATER:
						terrainMap[y][x] = new Water();
						break;	
						
					default:
						throw new InvalidTerrainTypeException(currentTerrain);
					}
				}
			}
			
			isLoaded = true;
		}
		
		catch (IOException e)
		{
			unloadTerrainMap();
			throw e;
		}
		
		catch (NoSuchElementException e)
		{
			unloadTerrainMap();
			throw new InvalidInputFormatException(); // This occurs if the GameMap textfile has format errors.
		}
	}
	
	public void unloadTerrainMap()
	{
		width = 0;
		height = 0;
		terrainMap = null;
		isLoaded = false;
	}
	
	public int canvasToTerrainMapX(double canvasX) 
	{
		return (int)(canvasX / GameApplication.TILE_WIDTH);
	}

	public int canvasToTerrainMapY(double canvasY) 
	{
		return (int)(canvasY / GameApplication.TILE_HEIGHT);
	}

	public double terrainMapToCanvasX(int terrainMapX) 
	{
		return terrainMapX * GameApplication.TILE_WIDTH;
	}

	public double terrainMapToCanvasY(int terrainMapY) 
	{
		return terrainMapY * GameApplication.TILE_HEIGHT;
	}
	
	// Updates Unit's movementMap. Uses dynamic programming and iterative recursion.
	public void checkPathfinding(Unit unit)
	{
		// Local class just for combining xy-coordinate pairs into a single object, for algorithm coding convenience.
		class Tile
		{
			public final int movementMapX;
			public final int movementMapY;
			public int movementCost;
			
			public Tile(int movementMapX, int movementMapY, int movementCost)
			{
				this.movementMapX = movementMapX;
				this.movementMapY = movementMapY;
				this.movementCost = movementCost;
			}
		}
		
		int movementRange = unit.getMovementRange();
		int movementMapCenter = movementRange; // int ((movementRange * 2) + 1)/2 == movementRange;
		boolean[][] movementMap = unit.getMovementMap();
		boolean[][] visited = new boolean[(movementRange * 2) + 1][(movementRange * 2) + 1]; // default all false.
		
		// Reset Unit's movementMap to all false.
		for (int movementMapY = 0; movementMapY < (movementRange * 2) + 1; movementMapY++)
		{
			for (int movementMapX = 0; movementMapX < (movementRange * 2) + 1; movementMapX++)
			{
				movementMap[movementMapY][movementMapX] = false;
			}
		}
		
		// Two queues to keep track of the iterative recursion depth.
		ArrayDeque<Tile> currentRecursionDepthQueue = new ArrayDeque<>();
		ArrayDeque<Tile> nextRecursionDepthQueue = new ArrayDeque<>();
		movementMap[movementMapCenter][movementMapCenter] = true;
		visited[movementMapCenter][movementMapCenter] = true;
		
		// Coordinate Transformations.
		int terrainMapCenterX = unit.movementMapToTerrainMapX(movementMapCenter);
		int terrainMapCenterXLeft = unit.movementMapToTerrainMapX(movementMapCenter - 1);
		int terrainMapCenterXRight = unit.movementMapToTerrainMapX(movementMapCenter + 1);
		
		int terrainMapCenterY = unit.movementMapToTerrainMapY(movementMapCenter);
		int terrainMapCenterYUp = unit.movementMapToTerrainMapY(movementMapCenter - 1);
		int terrainMapCenterYDown = unit.movementMapToTerrainMapY(movementMapCenter + 1);
		
		// Left
		currentRecursionDepthQueue.addLast(new Tile(movementMapCenter - 1, movementMapCenter, 
				getTerrainAtLocation(terrainMapCenterXLeft, terrainMapCenterY).getMovementCost()));
		visited[movementMapCenter][movementMapCenter - 1] = true;
		
		// Right
		currentRecursionDepthQueue.addLast(new Tile(movementMapCenter + 1, movementMapCenter, 
				getTerrainAtLocation(terrainMapCenterXRight, terrainMapCenterY).getMovementCost()));
		visited[movementMapCenter][movementMapCenter + 1] = true;
		
		// Up
		currentRecursionDepthQueue.addLast(new Tile(movementMapCenter, movementMapCenter - 1, 
				getTerrainAtLocation(terrainMapCenterX, terrainMapCenterYUp).getMovementCost()));
		visited[movementMapCenter - 1][movementMapCenter] = true;
		
		// Down
		currentRecursionDepthQueue.addLast(new Tile(movementMapCenter, movementMapCenter + 1, 
				getTerrainAtLocation(terrainMapCenterX, terrainMapCenterYDown).getMovementCost()));
		visited[movementMapCenter + 1][movementMapCenter] = true;
		
		// Iterative recursion. Max Recursion Depth == movementRange.
		for (int i = 1; i <= movementRange; i++)
		{
			while (!currentRecursionDepthQueue.isEmpty())
			{
				Tile currentTile = currentRecursionDepthQueue.pollFirst();
				
				// If movementCost is more than 1, deal with it in the next recursion depth, but remember to decrement movementCost.
				if (currentTile.movementCost > 1)
				{
					currentTile.movementCost--;
					nextRecursionDepthQueue.addLast(currentTile);
					
					continue;
				}
				
				int terrainLocationX = unit.movementMapToTerrainMapX(currentTile.movementMapX);
				int terrainLocationY = unit.movementMapToTerrainMapY(currentTile.movementMapY);
					
				if (!getTerrainAtLocation(terrainLocationX, terrainLocationY).isBlocked())
				{
					movementMap[currentTile.movementMapY][currentTile.movementMapX] = true;
					
					// No need to add next MovementSquares to queue, if we are already at the last iteration.
					if (i >= movementRange)
					{
						continue;
					}
					
					// Coordinate Transformations.
					int terrainMapCurrentTileX = unit.movementMapToTerrainMapX(currentTile.movementMapX);
					int terrainMapCurrentTileXLeft = unit.movementMapToTerrainMapX(currentTile.movementMapX - 1);
					int terrainMapCurrentTileXRight = unit.movementMapToTerrainMapX(currentTile.movementMapX + 1);
					
					int terrainMapCurrentTileY = unit.movementMapToTerrainMapY(currentTile.movementMapY);
					int terrainMapCurrentTileYUp = unit.movementMapToTerrainMapY(currentTile.movementMapY - 1);
					int terrainMapCurrentTileYDown = unit.movementMapToTerrainMapY(currentTile.movementMapY + 1);
					
					// Left
					if (!visited[currentTile.movementMapY][currentTile.movementMapX - 1])
					{
						nextRecursionDepthQueue.addLast(new Tile(currentTile.movementMapX - 1, currentTile.movementMapY, 
								getTerrainAtLocation(terrainMapCurrentTileXLeft, terrainMapCurrentTileY).getMovementCost()));
						visited[currentTile.movementMapY][currentTile.movementMapX - 1] = true;
					}
					
					// Right
					if (!visited[currentTile.movementMapY][currentTile.movementMapX + 1])
					{
						nextRecursionDepthQueue.addLast(new Tile(currentTile.movementMapX + 1, currentTile.movementMapY, 
								getTerrainAtLocation(terrainMapCurrentTileXRight, terrainMapCurrentTileY).getMovementCost()));
						visited[currentTile.movementMapY][currentTile.movementMapX + 1] = true;
					}
					
					// Up
					if (!visited[currentTile.movementMapY - 1][currentTile.movementMapX])
					{
						nextRecursionDepthQueue.addLast(new Tile(currentTile.movementMapX, currentTile.movementMapY - 1, 
								getTerrainAtLocation(terrainMapCurrentTileX, terrainMapCurrentTileYUp).getMovementCost()));
						visited[currentTile.movementMapY - 1][currentTile.movementMapX] = true;
					}
					
					// Down
					if (!visited[currentTile.movementMapY + 1][currentTile.movementMapX])
					{
						nextRecursionDepthQueue.addLast(new Tile(currentTile.movementMapX, currentTile.movementMapY + 1, 
								getTerrainAtLocation(terrainMapCurrentTileX, terrainMapCurrentTileYDown).getMovementCost()));
						visited[currentTile.movementMapY + 1][currentTile.movementMapX] = true;
					}
				}
			}
			
			// Swap the queues. Only swapping references is enough, no need deep-copy swaps.
			ArrayDeque<Tile> temp = currentRecursionDepthQueue;
			currentRecursionDepthQueue = nextRecursionDepthQueue;
			nextRecursionDepthQueue = temp;
		}
	}	
}
