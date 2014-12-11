package gamePvE;

import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class TerrainEntity extends Entity {
	
	private static final long serialVersionUID = 8496900945587301602L;

	public TerrainEntity(ShapeGroup shapes) {
		super(shapes, "terrain");
		shapes.setColor(GameRunner.TERRAIN_COLOR);
		addTag("solidimmovable");
	}

	@Override
	public TerrainEntity deepCopy(){
		return new TerrainEntity(shapes.deepCopy());
	}
	
	@Override
	public void preStep() {}
	
	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {}
	
	@Override
	public void postStep() {}
}
