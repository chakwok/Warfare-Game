package units;

import javafx.scene.image.Image;
import pa1.GameApplication;
import pa1.Player;

public class Pikeman extends Unit 
{
	private static final int ATTACK_PIKEMAN = 8;
	private static final int DEFENSE_PIKEMAN = 7;
	private static final int ATTACK_RANGE_PIKEMAN = 1;
	private static final int MOVEMENT_RANGE_PIKEMAN = 3;
	
	private static final Image[] IMAGE_PIKEMAN_COLORS = new Image[Player.NUM_PLAYER_COLORS];
	static
	{
		IMAGE_PIKEMAN_COLORS[Player.RED] = new Image("unit_images/pikeman_red.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_PIKEMAN_COLORS[Player.BLUE] = new Image("unit_images/pikeman_blue.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_PIKEMAN_COLORS[Player.YELLOW] = new Image("unit_images/pikeman_yellow.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_PIKEMAN_COLORS[Player.GREEN] = new Image("unit_images/pikeman_green.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_PIKEMAN_COLORS[Player.GRAY] = new Image("unit_images/pikeman_gray.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	}		
	
	public Pikeman(char id, int color)
	{
		super(id, color, ATTACK_PIKEMAN, DEFENSE_PIKEMAN, ATTACK_RANGE_PIKEMAN, MOVEMENT_RANGE_PIKEMAN);
	}
	
	@Override
	public Image getImage() 
	{
		return IMAGE_PIKEMAN_COLORS[COLOR];
	}		
	
	@Override 
	public void receiveDamage(double rawDamage, Unit attacker)
	{
		if (attacker instanceof Infantry)
		{
			receiveDamage(rawDamage * 1.5);
		}
		else if (attacker instanceof Cavalry)
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
		return String.format("[%c]  H:%-2d  A:%-2d  D:%-2d  R:%-2d  M:%-2d  x:%-2d  y:%-2d  %-8s  %-5s", ID, health, ATTACK, DEFENSE, ATTACK_RANGE, MOVEMENT_RANGE, locationX, locationY, "Pikeman", getStatus());
	}
}
