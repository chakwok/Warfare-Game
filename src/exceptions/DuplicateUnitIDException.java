package exceptions;

import java.io.IOException;

public class DuplicateUnitIDException extends IOException 
{
	public DuplicateUnitIDException(char id)
	{
		super("Duplicate Unit ID: " + id + " is not allowed.");
	}
}
