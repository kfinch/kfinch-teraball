package gamePvE;

import gamePhysics2D.BoundingAABox;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class FuelPickupEntity extends Entity {
	
	private int value;
	
	
	public FuelPickupEntity(ShapeGroup shapes, int value) {
		super(shapes, "coin");
		this.value = value;
	}
	
	public FuelPickupEntity(double xLoc, double yLoc, int value){
		this(generateDefaultShapes(xLoc, yLoc), value);
	}
	
	public FuelPickupEntity(double xLoc, double yLoc){
		this(xLoc, yLoc, GameRunner.FUELPICKUP_VALUE);
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		return new ShapeGroup(new BoundingAABox(xLoc, yLoc, GameRunner.FUELPICKUP_XSIZE,
				                                GameRunner.FUELPICKUP_YSIZE), GameRunner.FUELPICKUP_COLOR);
	}

	public int getValue(){
		return value;
	}

	@Override
	public void preStep() {}

	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {
		if(e instanceof PlayerEntity){
			((PlayerEntity)e).addFuel(value);
			isActive = false;
		}
	}

	@Override
	public void postStep() {}
}
