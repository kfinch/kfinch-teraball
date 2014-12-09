package gamePvE;

import gamePhysics2D.BoundingCircle;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class CoinEntity extends Entity {

	private int value;
	
	public CoinEntity(ShapeGroup shapes, int value) {
		super(shapes, "coin");
		this.value = value;
	}
	
	public CoinEntity(double xLoc, double yLoc, double size, int value){
		this(generateDefaultShapes(xLoc, yLoc, size), value);
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc, double size){
		return new ShapeGroup(new BoundingCircle(xLoc, yLoc, size), GameRunner.COIN_COLOR);
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
			((PlayerEntity)e).addPoints(value);
			isActive = false;
		}
	}

	@Override
	public void postStep() {}
}
