package exceptions;

import java.io.IOException;

public class InvalidNumberOfPlayersException extends IOException 
{
	public InvalidNumberOfPlayersException(int num)
	{
		super("Invalid Number of Players: " + num + ". This game can only handle 2 - 5 Players.");
	}
}
