package exceptions;

import java.io.IOException;

public class InvalidInputFormatException extends IOException 
{
	public InvalidInputFormatException()
	{
		super("Invalid Input Format.");
	}
}
