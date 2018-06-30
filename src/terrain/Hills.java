package terrain;

import javafx.scene.image.Image;
import pa1.GameApplication;

public class Hills extends Terrain
{
	private static final Image IMAGE_HILLS = new Image("terrain_images/hills.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	
	public Hills () {
		super(3);
	}
	
	@Override
	public Image getImage() {
		return IMAGE_HILLS;
	}
}