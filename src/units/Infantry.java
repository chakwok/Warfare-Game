package units;

import javafx.scene.image.Image;
import pa1.GameApplication;
import pa1.Player;

public class Infantry extends Unit 
{
	private static final int ATTACK_INFANTRY = 10;
	private static final int DEFENSE_INFANTRY = 5;
	private static final int ATTACK_RANGE_INFANTRY = 1;
	private static final int MOVEMENT_RANGE_INFANTRY = 4;
	
	private static final Image[] IMAGE_INFANTRY_COLORS = new Image[Player.NUM_PLAYER_COLORS];
	static
	{
		IMAGE_INFANTRY_COLORS[Player.RED] = new Image("unit_images/infantry_red.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_INFANTRY_COLORS[Player.BLUE] = new Image("unit_images/infantry_blue.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_INFANTRY_COLORS[Player.YELLOW] = new Image("unit_images/infantry_yellow.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_INFANTRY_COLORS[Player.GREEN] = new Image("unit_images/infantry_green.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_INFANTRY_COLORS[Player.GRAY] = new Image("unit_images/infantry_gray.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	}	
	
	public Infantry(char id, int color)
	{
		super(id, color, ATTACK_INFANTRY, DEFENSE_INFANTRY, ATTACK_RANGE_INFANTRY, MOVEMENT_RANGE_INFANTRY);
	}
	
	@Override
	public Image getImage() 
	{
		return IMAGE_INFANTRY_COLORS[COLOR];
	}	
	
	@Override 
	public void receiveDamage(double rawDamage, Unit attacker)
	{
		if (attacker instanceof Cavalry)
		{
			receiveDamage(rawDamage * 1.5);
		}
		else if (attacker instanceof Pikeman)
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
		return String.format("[%c]  H:%-2d  A:%-2d  D:%-2d  R:%-2d  M:%-2d  x:%-2d  y:%-2d  %-8s  %-5s", ID, health, ATTACK, DEFENSE, ATTACK_RANGE, MOVEMENT_RANGE, locationX, locationY, "Infantry", getStatus());
	}
}
