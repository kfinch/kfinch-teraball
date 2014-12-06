package gamePvE;

import gamePhysics2D.BoundingCircle;
import gamePhysics2D.BoundingPolygon;
import gamePhysics2D.BoundingRotatingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.EntityAdder;
import gamePhysics2D.EntitySimulator;
import gamePhysics2D.DynamicEntity;
import gamePhysics2D.Entity;
import gamePhysics2D.Point2d;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MissileTurretEntity extends Entity{

	private ShapeGroup detail;
	
	private LineOfSightEntity los;
	
	private int missileCDTimer;
	private int missileCD;
	
	private EntityAdder adder;
	
	public MissileTurretEntity(ShapeGroup shapes, ShapeGroup detail, 
			                   int missileCD, EntityAdder adder){
		super(shapes, "gunturret");
		addTag("solidimmovable");
		this.missileCD = missileCD;
		missileCDTimer = -1;
		this.adder = adder;
	}
	
	public MissileTurretEntity(double xLoc, double yLoc, EntityAdder adder){
		this(generateDefaultShapes(xLoc, yLoc), null, GameRunner.TURRET_MISSILE_CD, adder);
		detail = generateDefaultDetail(xLoc, yLoc);
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		return new ShapeGroup(new BoundingCircle(xLoc, yLoc, 20), GameRunner.ENEMY_COLOR);
	}
	
	private static ShapeGroup generateDefaultDetail(double xLoc, double yLoc){
		double xp[] = {0,5,15,5,0,-5,-15,-5};
		double yp[] = {15,5,0,-5,-15,-5,0,5};
		int np = 8;
		return new ShapeGroup(new BoundingPolygon(xLoc, yLoc, np, xp, yp), Color.black);
	}
	
	public void setLOS(LineOfSightEntity los){
		this.los = los;
	}
	
	@Override
	public void preStep() {
		if(los != null && los.hasLOS && missileCDTimer == -1){
			missileCDTimer = 0;
			MissileEntity missile = new MissileEntity(shapes.xLoc, shapes.yLoc, los.dst,
					                                  GameRunner.TURRET_MISSILE_SPEED, los.angle(), adder);
			Vector2d missileDisplace = new Vector2d();
			missileDisplace.setAngleAndMagnitude(los.angle(), Math.max(shapes.xBound, shapes.yBound) + 5); //TODO: lol
			missile.shapes.translate(missileDisplace);
			adder.addEntity(missile, "projectiles");
		}
	
		if(missileCDTimer != -1)
			missileCDTimer++;
		if(missileCDTimer >= missileCD)
			missileCDTimer = -1;
	}

	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {}

	@Override
	public void postStep() {}
	
	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		super.paintEntity(g2d, xoffset, yoffset, scale);
		detail.paintShapes(g2d, xoffset, yoffset, scale);
	}
}

class MissileEntity extends DynamicEntity {
	
	private double damage;
	private double knockback;
	
	private Entity target;
	private double turnSpeed;
	
	private LinkedList<Point2d> prevLocs;
	
	private double accel;
	private double turnSpeedDegrade;
	private int changeDuration;
	
	private EntityAdder adder;
	
	private int steps;
	
	private static int MISSILE_TRAIL_SIZE = 10;
	
	public MissileEntity(ShapeGroup shapes, Entity target, double initialSpeed, double initialDirection,
			             double turnSpeed, double damage, double knockback, double accel,
			             double turnSpeedDegrade, int changeDuration, EntityAdder adder){
		super(shapes, "missileturretproj");
		addTag("projectile");
		this.target = target;
		this.damage = damage;
		this.knockback = knockback;
		this.turnSpeed = turnSpeed;
		this.velocity = new Vector2d();
		velocity.setAngleAndMagnitude(initialDirection, initialSpeed);
		this.accel = accel;
		this.turnSpeedDegrade = turnSpeedDegrade;
		this.changeDuration = changeDuration;
		prevLocs = new LinkedList<Point2d>();
		steps = 0;
		this.adder = adder;
	}
	
	public MissileEntity(double startx, double starty, Entity target,
			             double projSpeed, double projDirection, EntityAdder adder){
		this(generateDefaultShapes(startx, starty, projDirection), target, projSpeed, projDirection,
		     GameRunner.TURRET_MISSILE_TURN_SPEED, GameRunner.TURRET_MISSILE_DAMAGE,
		     GameRunner.TURRET_MISSILE_KNOCKBACK, GameRunner.TURRET_MISSILE_ACCEL,
		     GameRunner.TURRET_MISSILE_TURN_SPEED_DEGRADE, GameRunner.TURRET_MISSILE_CHANGE_DURATION, adder);
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc, double direction){
		int nPoints = 5;
		double xPoints[] = {10,3,-4,-4,3};
		double yPoints[] = {0,3,3,-3,-3};
		ShapeGroup result = new ShapeGroup(new BoundingRotatingPolygon(xLoc, yLoc, nPoints, xPoints, yPoints),
				                           GameRunner.ENEMY_MISSILE_COLOR);
		result.rotate(direction);
		return result;
	}

	@Override
	public void preStep(){
		Vector2d targetVector = new Vector2d(target.shapes.xLoc - shapes.xLoc, target.shapes.yLoc - shapes.yLoc);
		double targetDir = targetVector.angle();
		double thisDir = velocity.angle();
		double delta = Vector2d.deltaAngle(thisDir, targetDir);
		if(Math.abs(delta) <= turnSpeed){
			velocity.setAngle(targetDir);
			shapes.rotate(delta);
		}
		else if(delta > 0){
			velocity.setAngle(thisDir + turnSpeed);
			shapes.rotate(turnSpeed);
		}
		else{
			velocity.setAngle(thisDir - turnSpeed);
			shapes.rotate(-turnSpeed);
		}
	}
	
	@Override
	public void moveStep(){
		prevLocs.addFirst(new Point2d(shapes.xLoc, shapes.yLoc));
		if(prevLocs.size() >= MISSILE_TRAIL_SIZE)
			prevLocs.removeLast();
		
		steps++;
		if(steps <= changeDuration){
			velocity.setMagnitude(velocity.magnitude() + accel);
			turnSpeed -= turnSpeedDegrade;
		}
		
		super.moveStep();
	}

	@Override
	public void resolveCollision(Entity e, Vector2d cv){
		if(e.hasTag("solid") || e.hasTag("solidimmovable")){
			adder.addEntity(new ExplosionEntity(shapes.xLoc, shapes.yLoc, shapes.xBound*4, "basic"), "nocollide");
			isActive = false;
		}
		
		if(e instanceof PlayerEntity){
			PlayerEntity pe = (PlayerEntity)e;
			Vector2d knockbackVector = new Vector2d();
			knockbackVector.setAngleAndMagnitude(cv.angle() + Math.PI, knockback);
			pe.damagePlayer(damage, knockbackVector);
		}
	}
	
	@Override
	public void postStep() {}
	
	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		//TODO: deal with this in a non dumb way
		g2d.setColor(GameRunner.ENEMY_MISSILE_TRAIL_COLOR);
		float alpha = 1;
		ShapeGroup sg;
		for(Point2d p : prevLocs){
			alpha -= 0.1; //TODO: deal with hard code constants
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			sg = new ShapeGroup(new BoundingCircle(p.x, p.y, 5)); //TODO: deal with hard code constants
			sg.paintShapes(g2d, xoffset, yoffset, scale);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		super.paintEntity(g2d, xoffset, yoffset, scale);
	}
}

