package terrain;

import javafx.scene.image.Image;
import pa1.GameApplication;
import units.Unit;

public class TerrainOutOfBounds extends Terrain 
{
	private static final Image IMAGE_OUT_OF_BOUNDS = new Image("terrain_images/outofbounds.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	
	public TerrainOutOfBounds()
	{
		super(-1); // OutOfBounds, impassable.
	}
	
	@Override
	public void occupy(Unit occupyingUnit) {}
	
	@Override
	public void unoccupy() {}
	
	@Override
	public Image getImage() 
	{
		return IMAGE_OUT_OF_BOUNDS;
	}
}
