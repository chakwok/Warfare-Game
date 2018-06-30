package units;

import javafx.scene.image.Image;
import pa1.GameApplication;
import pa1.Player;

public class Archer extends Unit 
{	
	private static final int ATTACK_ARCHER = 9;
	private static final int DEFENSE_ARCHER = 1;
	private static final int ATTACK_RANGE_ARCHER = 4;
	private static final int MOVEMENT_RANGE_ARCHER = 4;
	
	private static final Image[] IMAGE_ARCHER_COLORS = new Image[Player.NUM_PLAYER_COLORS];
	static
	{
		IMAGE_ARCHER_COLORS[Player.RED] = new Image("unit_images/archer_red.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_ARCHER_COLORS[Player.BLUE] = new Image("unit_images/archer_blue.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_ARCHER_COLORS[Player.YELLOW] = new Image("unit_images/archer_yellow.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_ARCHER_COLORS[Player.GREEN] = new Image("unit_images/archer_green.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		IMAGE_ARCHER_COLORS[Player.GRAY] = new Image("unit_images/archer_gray.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	}
	
	public Archer(char id, int color)
	{
		super(id, color, ATTACK_ARCHER, DEFENSE_ARCHER, ATTACK_RANGE_ARCHER, MOVEMENT_RANGE_ARCHER);
	}

	@Override
	public Image getImage() 
	{
		return IMAGE_ARCHER_COLORS[COLOR];
	}
	
	@Override
	public void attackUnit(Unit defender)
	{
		double defenderRawDamageReceived = (ATTACK - defender.DEFENSE) * health * 0.1;
		double attackerRawDamageReceived = (defender.ATTACK - DEFENSE) * defender.health * 0.1;
		
		defender.receiveDamage(defenderRawDamageReceived);
		
		if ((Math.abs(locationX - defender.locationX) + Math.abs(locationY - defender.locationY)) <= defender.getAttackRange())
		{
			this.receiveDamage(attackerRawDamageReceived);
		}
	}
	
	@Override 
	public void receiveDamage(double rawDamage, Unit attacker)
	{
		receiveDamage(rawDamage);
	}
	
	@Override
	public String toString()
	{
		return String.format("[%c]  H:%-2d  A:%-2d  D:%-2d  R:%-2d  M:%-2d  x:%-2d  y:%-2d  %-8s  %-5s", ID, health, ATTACK, DEFENSE, ATTACK_RANGE, MOVEMENT_RANGE, locationX, locationY, "Archer", getStatus());
	}
}
