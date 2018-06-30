package terrain;

import javafx.scene.image.Image;
import units.Unit;

// TODO: You may refer to the Unit classes (Archer, Cavalry, Infantry, Pikeman) for how to setup the Image for the other Terrain classes.
// NOTE: TerrainOutOfBounds is a special case and the code is fully given.

public abstract class Terrain {
	protected final int MOVEMENT_COST;
	protected boolean impassable = false;
	protected boolean occupied = false;
	protected Unit occupyingUnit = null;
	
	public Terrain (int movementCost) {
		MOVEMENT_COST = movementCost;
		if (movementCost == -1 ) {
			impassable = true;
		}
	}
	
	public abstract Image getImage();
	
	public int getMovementCost() {
		return MOVEMENT_COST;
	}
	
	public void occupy(Unit occupyingUnit) {
		occupied = true;
		this.occupyingUnit = occupyingUnit;
	}
	
	public void unoccupy() {
		occupied = false;
		occupyingUnit = null;
	}
	
	public Unit getOccupyingUnit() {
		return occupyingUnit;
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	
	public boolean isBlocked() {
		return (impassable || occupied);
	}
	
	
	
	
	
	
	
	
	
	
		
	
}
