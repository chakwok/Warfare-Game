package exceptions;

import java.io.IOException;

public class InvalidUnitTypeException extends IOException 
{
	public InvalidUnitTypeException(String type)
	{
		super("Invalid Unit Type: " + type);
	}
}
