package terrain;

import javafx.scene.image.Image;
import pa1.GameApplication;

public class Plains extends Terrain
{
	private static final Image IMAGE_PLAINS = new Image("terrain_images/plains.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	
	public Plains () {
		super(1);
	}
	
	@Override
	public Image getImage() {
		return IMAGE_PLAINS;
	}
}