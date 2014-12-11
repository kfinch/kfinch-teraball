package gamePvE;

import gamePhysics2D.BoundingPolygon;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class LevelExitEntity extends Entity {
	
	private static final long serialVersionUID = -5280395074194523361L;

	public LevelExitEntity(ShapeGroup shapes) {
		super(shapes, "basicexit");
		addTag("activeexit");
	}
	
	public LevelExitEntity(double xLoc, double yLoc){
		this(generateDefaultShapes(xLoc, yLoc));
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		double xp[] = {-20,-20,20,20,0};
		double yp[] = {-30,30,30,-30,-35};
		int np = 5;
		return new ShapeGroup(new BoundingPolygon(xLoc, yLoc, np, xp, yp), GameRunner.EXIT_COLOR);
	}
	
	@Override
	public LevelExitEntity deepCopy(){
		return new LevelExitEntity(shapes.deepCopy());
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
