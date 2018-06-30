package terrain;

import javafx.scene.image.Image;
import pa1.GameApplication;

public class Forest extends Terrain
{
	private static final Image IMAGE_FOREST = new Image("terrain_images/forest.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	
	public Forest () {
		super(2);
	}
	
	@Override
	public Image getImage() {
		return IMAGE_FOREST;
	}
}