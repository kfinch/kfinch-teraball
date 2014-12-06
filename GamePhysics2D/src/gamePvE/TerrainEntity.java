package gamePvE;

import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;

public class TerrainEntity extends Entity {
	
	public TerrainEntity(ShapeGroup sg) {
		super(sg, "terrain");
		sg.setColor(GameRunner.TERRAIN_COLOR);
		addTag("solidimmovable");
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