package units;

import javafx.scene.image.Image;
import pa1.GameApplication;
import pa1.Player;

public class Cavalry extends Unit 
{
	private static final int ATTACK_CAVALRY = 14;
	private static final int DEFENSE_CAVALRY = 3;
	private static final int ATTACK_RANGE_CAVALRY = 1;
	private static final int MOVEMENT_RANGE_CAVALRY = 6;
	
	private static final Image[] IMAGE_CAVALRY_COLORS = new Image[Player.NUM_PLAYER_COLORS];
	static
	{
		IMAGE_CAVALRY_COLORS[Player.RED] = new Image("unit_images/cavalry_red.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_CAVALRY_COLORS[Player.BLUE] = new Image("unit_images/cavalry_blue.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_CAVALRY_COLORS[Player.YELLOW] = new Image("unit_images/cavalry_yellow.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_CAVALRY_COLORS[Player.GREEN] = new Image("unit_images/cavalry_green.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_CAVALRY_COLORS[Player.GRAY] = new Image("unit_images/cavalry_gray.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	}	
	
	public Cavalry(char id, int color)
	{
		super(id, color, ATTACK_CAVALRY, DEFENSE_CAVALRY, ATTACK_RANGE_CAVALRY, MOVEMENT_RANGE_CAVALRY);
	}
	
	@Override
	public Image getImage() 
	{
		return IMAGE_CAVALRY_COLORS[COLOR];
	}	
	
	@Override 
	public void receiveDamage(double rawDamage, Unit attacker)
	{
		if (attacker instanceof Pikeman)
		{
			receiveDamage(rawDamage * 1.5);
		}
		else if (attacker instanceof Infantry)
		{
			receiveDamage(rawDamage / 2.0);
		}
		else
		{
			receiveDamage(rawDamage);
		}
	}
	
	@Override
	public String toString()
	{
		return String.format("[%c]  H:%-2d  A:%-2d  D:%-2d  R:%-2d  M:%-2d  x:%-2d  y:%-2d  %-8s  %-5s", ID, health, ATTACK, DEFENSE, ATTACK_RANGE, MOVEMENT_RANGE, locationX, locationY, "Cavalry", getStatus());
	}
}
