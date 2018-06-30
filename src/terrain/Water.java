package terrain;

import javafx.scene.image.Image;
import pa1.GameApplication;

// TODO: Also setup the Images for Water Animation.
// Use "static {}" to initialize static fields.
// You may change ANIM_TIME_PER_FRAME to your own preference.
// Refer to Lab 12.

public class Water extends Terrain
{
	private static final Image IMAGE_WATER = new Image("terrain_images/water1.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
	public static final int NUM_ANIM_FRAMES = 4;
	private static final Image[] ANIM_FRAMES;
	public static final int ANIM_TIME_PER_FRAME = 500;
	static {
		ANIM_FRAMES = new Image[NUM_ANIM_FRAMES];
		for (int i = 0 ; i <= 3 ; i++) {
			//String imagePath = "\"terrain_images/water" + (i+1) + ".png\"";
			//System.out.println(imagePath);
			ANIM_FRAMES[i] = new Image("terrain_images/water" + (i+1) + ".png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
			//ANIM_FRAMES[i] = new Image("terrain_images/water1.png", GameApplication.TILE_WIDTH, GameApplication.TILE_HEIGHT, true, true);
		}
	}
	
	
	public Water () {
		super(-1);
	}
	
	@Override
	public Image getImage() {
		return IMAGE_WATER;
	}
	
	public Image getAnimFrame(int index) {
		return ANIM_FRAMES[index];
	}
	
}