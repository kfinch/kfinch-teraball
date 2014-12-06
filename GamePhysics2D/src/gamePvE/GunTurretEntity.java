package gamePvE;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import gamePhysics2D.BoundingCircle;
import gamePhysics2D.BoundingRotatingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.DynamicEntity;
import gamePhysics2D.Entity;
import gamePhysics2D.EntityAdder;
import gamePhysics2D.EntitySimulator;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class GunTurretEntity extends Entity {
	
	private LineOfSightEntity los;
	
	private ShapeGroup turret;
	
	private int gunCDTimer;
	private int gunCD;
	
	private double facing;
	private double turningSpeed;
	
	private EntityAdder adder;
	
	public GunTurretEntity(ShapeGroup shapes, ShapeGroup turret,
			               double turningSpeed, int gunCD, EntityAdder adder) {
		super(shapes, "gunturret");
		addTag("solidimmovable");
		facing = 0;
		this.turret = turret;
		this.turningSpeed = turningSpeed;
		this.gunCD = gunCD;
		gunCDTimer = -1;
		this.adder = adder;
	}
	
	public GunTurretEntity(double xLoc, double yLoc, double initialFacing, EntityAdder adder){
		this(generateDefaultShapes(xLoc, yLoc), null,
			 GameRunner.TURRET_TURN_SPEED, GameRunner.TURRET_GUN_CD, adder);
		turret = generateDefaultTurret(xLoc, yLoc);
		setTurretRotation(initialFacing);
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		return new ShapeGroup(new BoundingCircle(xLoc, yLoc, 20), GameRunner.ENEMY_COLOR);
	}
	
	private static ShapeGroup generateDefaultTurret(double xLoc, double yLoc){
		double xp[] = {15,-5,-5,-15,-15,-5,-5,15};
		double yp[] = {3,7,10,10,-10,-10,-7,-3};
		int np = 8;
		return new ShapeGroup(new BoundingRotatingPolygon(xLoc, yLoc, np, xp, yp), Color.black);
	}
	
	public void setLOS(LineOfSightEntity los){
		this.los = los;
	}
	
	public double getFacing(){
		return facing;
	}
	
	private void rotateTurret(double delta){
		turret.rotate(delta);
		facing += delta;
	}
	
	private void setTurretRotation(double angle){
		turret.setAngle(angle);
		facing = angle;
	}

	@Override
	public void preStep() {
		if(los != null && los.hasLOS){
			double delta = Vector2d.deltaAngle(facing, los.angle());
			
			if(turningSpeed - Math.abs(delta) >= 0)
				setTurretRotation(los.angle());
			else if(delta > 0)
				rotateTurret(turningSpeed);
			else
				rotateTurret(-turningSpeed);
			
			//if facing target and gun ready to fire, then fire! Currently fires in bursts of 3 shots.
			if(facing == los.angle() && gunCDTimer == -1){
				gunCDTimer = 0;
			}
			if(gunCDTimer == 0 || gunCDTimer == 4 || gunCDTimer == 8){
				GunTurretProjectileEntity shot = new GunTurretProjectileEntity(shapes.xLoc, shapes.yLoc,
						                                                  GameRunner.TURRET_SHOT_SPEED, facing, adder);
				Vector2d shotDisplace = new Vector2d();
				shotDisplace.setAngleAndMagnitude(facing, Math.max(shapes.xBound, shapes.yBound));
				shot.shapes.translate(shotDisplace);
				adder.addEntity(shot, "projectiles");
			}
		}
		
		//gunCD is Gun's Cooldown. "gunCD = n" implies that the gun will fire every n frames (if it has target)
		if(gunCDTimer != -1)
			gunCDTimer++;
		if(gunCDTimer >= gunCD)
			gunCDTimer = -1;
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
		turret.paintShapes(g2d, xoffset, yoffset, scale);
	}

}

class GunTurretProjectileEntity extends DynamicEntity {
	
	private Color color;
	
	private double damage;
	private double knockback;
	
	private EntityAdder adder;
	
	public GunTurretProjectileEntity(ShapeGroup shapes, Color color, double projSpeed, double projDirection,
			                         double projDamage, double projKnockback, EntityAdder adder){
		super(shapes, "gunturretproj");
		addTag("projectile");
		this.color = color;
		this.damage = projDamage;
		this.knockback = projKnockback;
		this.velocity = new Vector2d();
		velocity.setAngleAndMagnitude(projDirection, projSpeed);
		this.adder = adder;
	}
	
	public GunTurretProjectileEntity(double startx, double starty,
			                         double projSpeed, double projDirection, EntityAdder adder){
		this(generateDefaultShapes(startx, starty), GameRunner.ENEMY_SHOT_COLOR, projSpeed, projDirection,
		     GameRunner.TURRET_SHOT_DAMAGE, GameRunner.TURRET_SHOT_KNOCKBACK, adder);
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		return new ShapeGroup(new BoundingCircle(xLoc, yLoc, GameRunner.TURRET_SHOT_SIZE),
				              GameRunner.ENEMY_SHOT_COLOR);
	}

	@Override
	public void preStep() {}

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
}
