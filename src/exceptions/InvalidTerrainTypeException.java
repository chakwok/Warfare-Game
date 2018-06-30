package exceptions;

import java.io.IOException;

public class InvalidTerrainTypeException extends IOException 
{
	public InvalidTerrainTypeException(int terrain)
	{
		super("Invalid Terrain Type: " + terrain);
	}
}
