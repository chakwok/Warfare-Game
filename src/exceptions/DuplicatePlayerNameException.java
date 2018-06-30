package exceptions;

import java.io.IOException;

public class DuplicatePlayerNameException extends IOException 
{
	public DuplicatePlayerNameException(String name)
	{
		super("Duplicate Player Name: " + name + " is not allowed.");
	}
}
