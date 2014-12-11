package gamePvE;

import gamePhysics2D.BoundingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class MineEntity extends Entity {

	private static final long serialVersionUID = -6539007880101324239L;
	
	double damage;
	double knockback;
	
	public MineEntity(ShapeGroup shapes, double damage, double knockback) {
		super(shapes, "terrain");
		this.damage = damage;
		this.knockback = knockback;
		addTag("solidimmovable");
	}
	
	public MineEntity(ShapeGroup shapes) {
		this(shapes, GameRunner.MINE_DAMAGE, GameRunner.MINE_KNOCKBACK);
	}
	
	//creates a default shape size and strength 'mine' at xLoc,yLoc
	public MineEntity(double xLoc, double yLoc){
		this(generateDefaultShapes(xLoc, yLoc));
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		List<BoundingShape> shapeList = new ArrayList<BoundingShape>(7);
		
		double xp1[] = {0,5,5,0,-5,-5};
		double yp1[] = {-20,-5,5,20,5,-5};
		int np1 = 6;
		BoundingPolygon piece1 = new BoundingPolygon(xLoc, yLoc, np1, xp1, yp1);
		shapeList.add(piece1);
		
		double xp2[] = {20,5,5};
		double yp2[] = {0,5,-5};
		int np2 = 3;
		BoundingPolygon piece2 = new BoundingPolygon(xLoc, yLoc, np2, xp2, yp2);
		shapeList.add(piece2);
		
		double xp3[] = {-20,-5,-5};
		double yp3[] = {0,5,-5};
		int np3 = 3;
		BoundingPolygon piece3 = new BoundingPolygon(xLoc, yLoc, np3, xp3, yp3);
		shapeList.add(piece3);
		
		double xp4[] = {20,0,5};
		double yp4[] = {20,5,0};
		int np4 = 3;
		BoundingPolygon piece4 = new BoundingPolygon(xLoc, yLoc, np4, xp4, yp4);
		shapeList.add(piece4);
		
		double xp5[] = {-20,0,-5};
		double yp5[] = {20,5,0};
		int np5 = 3;
		BoundingPolygon piece5 = new BoundingPolygon(xLoc, yLoc, np5, xp5, yp5);
		shapeList.add(piece5);
		
		double xp6[] = {20,0,5};
		double yp6[] = {-20,-5,0};
		int np6 = 3;
		BoundingPolygon piece6 = new BoundingPolygon(xLoc, yLoc, np6, xp6, yp6);
		shapeList.add(piece6);
		
		double xp7[] = {-20,0,-5};
		double yp7[] = {-20,-5,0};
		int np7 = 3;
		BoundingPolygon piece7 = new BoundingPolygon(xLoc, yLoc, np7, xp7, yp7);
		shapeList.add(piece7);
		
		return new ShapeGroup(shapeList, GameRunner.ENEMY_COLOR);
	}

	@Override
	public MineEntity deepCopy(){
		return new MineEntity(shapes.deepCopy(), damage, knockback);
	}
	
	@Override
	public void preStep() {}
	
	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {
		if(e.hasTag("player")){
			PlayerEntity pe = (PlayerEntity)e;
			Vector2d knockbackVector = new Vector2d();
			knockbackVector.setAngleAndMagnitude(cv.angle() + Math.PI, knockback);
			pe.damagePlayer(damage, knockbackVector);
		}
			
	}
	
	public void postStep() {}
}
