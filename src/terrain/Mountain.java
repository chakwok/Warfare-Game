package terrain;

import javafx.scene.image.Image;
import pa1.GameApplication;

public class Mountain extends Terrain
{
	private static final Image IMAGE_MOUNTAIN = new Image("terrain_images/mountain.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	
	public Mountain () {
		super(-1);
	}
	
	@Override
	public Image getImage() {
		return IMAGE_MOUNTAIN;
	}
}