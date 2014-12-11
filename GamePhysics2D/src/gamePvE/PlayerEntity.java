package gamePvE;

import gamePhysics2D.BoundingCircle;
import gamePhysics2D.BoundingPolygon;
import gamePhysics2D.BoundingRotatingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.EntitySimulator;
import gamePhysics2D.Entity;
import gamePhysics2D.PhysicsEntity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayerEntity extends PhysicsEntity {

	private static final long serialVersionUID = -4674818783143636930L;
	
	public double maxHealth;
	public double currHealth;
	public double maxSpeed;
	public double accel;
	
	public double thrusterPower;
	public int maxFuel;
	public int currFuel;
	
	private int points;
	
	public boolean exited;
	
	private boolean damagedThisFrame;
	
	public boolean isThrusting;
	private double thrustDirection;
	
	public boolean isShielding;
	public PlayerShieldEntity shield;
	
	private List<Item> items;
	
	private boolean upAccel;
	private boolean downAccel;
	private boolean leftAccel;
	private boolean rightAccel;
	
	private ShapeGroup thrusterFlame;
	
	private ShapeGroup upAccelFlame;
	private ShapeGroup downAccelFlame;
	private ShapeGroup leftAccelFlame;
	private ShapeGroup rightAccelFlame;
	
	public PlayerEntity(ShapeGroup shapes, String tag, double mass, double bounciness,
						double friction, double maxHealth, double maxSpeed, double accel,
						double thrusterPower, int thrusterMaxFuel) {
		super(shapes, tag, new Vector2d(), mass, bounciness, friction);
		this.maxHealth = maxHealth;
		currHealth = maxHealth;
		this.maxSpeed = maxSpeed;
		this.accel = accel;
		
		this.thrusterPower = thrusterPower;
		this.maxFuel = thrusterMaxFuel;
		this.currFuel = thrusterMaxFuel;
		
		points = GameRunner.STARTING_POINTS;
		
		exited = false;
		damagedThisFrame = false;
		
		isThrusting = false;
		thrustDirection = 0;
		
		isShielding = false;
		
		items = new LinkedList<Item>();
		
		upAccel = false;
		downAccel = false;
		leftAccel = false;
		rightAccel = false;
		
		generateFlameShapes(shapes.xBound, shapes.yBound);
	}
	
	public PlayerEntity(double startx, double starty, EntitySimulator sim){
		this(generateDefaultShapes(startx, starty), "player",
			 GameRunner.PLAYER_MASS, GameRunner.PLAYER_BOUNCE, GameRunner.PLAYER_FRICTION,
			 GameRunner.PLAYER_MAX_HEALTH, GameRunner.PLAYER_MAX_SPEED, GameRunner.PLAYER_ACCEL,
			 GameRunner.PLAYER_THRUSTER_POWER, GameRunner.PLAYER_THRUSTER_MAX_FUEL);
		addTag("solid");
		shield = new PlayerShieldEntity(this);
		sim.addEntity(shield, "terrain");
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		return new ShapeGroup(new BoundingCircle(xLoc, yLoc, GameRunner.PLAYER_SIZE), GameRunner.PLAYER_COLOR);
	}
	
	private void generateFlameShapes(double xBound, double yBound){
		int nPoints = 3;
		
		double thrusterXPoints[] = {-xBound-3, -xBound-15, -xBound-3};
		double thrusterYPoints[] = {6, 0, -6};
		thrusterFlame = new ShapeGroup(new BoundingRotatingPolygon(0, 0, nPoints, thrusterXPoints, thrusterYPoints),
				                       GameRunner.ENEMY_SHOT_COLOR);
		
		double upXPoints[] = {3, 0, -3};
		double upYPoints[] = {yBound+3, yBound+8, yBound+3};
		upAccelFlame = new ShapeGroup(new BoundingPolygon(0, 0, nPoints, upXPoints, upYPoints),
				                      GameRunner.ENEMY_SHOT_COLOR);
		
		double downXPoints[] = {3, 0, -3};
		double downYPoints[] = {-yBound-3, -yBound-8, -yBound-3};
		downAccelFlame = new ShapeGroup(new BoundingPolygon(0, 0, nPoints, downXPoints, downYPoints),
				                        GameRunner.ENEMY_SHOT_COLOR);
		
		double leftXPoints[] = {xBound+3, xBound+8, xBound+3};
		double leftYPoints[] = {3, 0, -3};
		leftAccelFlame = new ShapeGroup(new BoundingPolygon(0, 0, nPoints, leftXPoints, leftYPoints),
                                        GameRunner.ENEMY_SHOT_COLOR);
		
		double rightXPoints[] = {-xBound-3, -xBound-8, -xBound-3};
		double rightYPoints[] = {3, 0, -3};
		rightAccelFlame = new ShapeGroup(new BoundingPolygon(0, 0, nPoints, rightXPoints, rightYPoints),
                                         GameRunner.ENEMY_SHOT_COLOR);
		
	}
	
	public Entity deepCopy(){
		throw new UnsupportedOperationException("The player cannot be deep-copied. THERE CAN ONLY BE ONE");
	}

	public void damagePlayer(double damage, Vector2d knockback){
		currHealth -= damage;
		velocity.add(knockback);
		damagedThisFrame = true;
	}
	
	public void addPoints(int points){
		this.points += points;
	}
	
	public int getPoints(){
		return points;
	}
	
	public int getCurrentFuel(){
		return currFuel;
	}
	
	public void addFuel(int fuel){
		currFuel += fuel;
		if(currFuel > maxFuel)
			currFuel = maxFuel;
	}
	
	public int getMaxFuel(){
		return maxFuel;
	}
	
	
	/*
	 *               -y up
	 *                 +
	 *    -x left    + + +      +x right
	 *                 +
	 *              +y down
	 */
	public void playerAccelerate(boolean up, boolean down, boolean left, boolean right){
		upAccel = up;
		downAccel = down;
		leftAccel = left;
		rightAccel = right;
		
		Vector2d accelVector = new Vector2d();
		
		if(up)
			accelVector.add(new Vector2d(0, -accel));
		if(down)
			accelVector.add(new Vector2d(0, accel));
		if(left)
			accelVector.add(new Vector2d(-accel, 0));
		if(right)
			accelVector.add(new Vector2d(accel, 0));
		
		velocity.add(accelVector);
	}
	
	public void playerThrust(double direction){
		if(currFuel <= 0){
			isThrusting = false;
			return;
		}
		
		thrustDirection = direction;
		Vector2d accelVector = new Vector2d();
		accelVector.setAngleAndMagnitude(direction, thrusterPower);
		velocity.add(accelVector);
		currFuel--;
		isThrusting = true;
	}
	
	public void playerShield(double direction){
		if(currFuel <= 0){
			playerDropShield();
			return;
		}
		
		shield.isShieldOn = true;
		shield.setOrientation(direction);
		currFuel--;
		isShielding = true;
	}
	
	public void playerDropShield(){
		isShielding = false;
		shield.isShieldOn = false;
	}
	
	public List<Item> getItems(){
		return items;
	}
	
	public void addItem(Item item){
		items.add(item);
	}
	
	public void removeItem(Item item){
		items.remove(item);
	}
	
	public void preStep(){
		damagedThisFrame = false;
		//isThrusting = false;
		//isShielding = false;
		//upAccel = false;
		//downAccel = false;
		//leftAccel = false;
		//rightAccel = false;
	}
	
	@Override
	public void moveStep(){
		super.moveStep();
		if(velocity.magnitude() > maxSpeed){ //apply rapid braking above max speed
			velocity.setMagnitude(velocity.magnitude() - accel*3);
			if(velocity.magnitude() < maxSpeed)
				velocity.setMagnitude(maxSpeed);
		}
		shield.updateLocation();
	}
	
	@Override
	public void resolveCollision(Entity e, Vector2d cv){
		//System.out.println("player collided with object..."); //TODO: remove debugging
		//System.out.println("I'm @ " + shapes.xLoc + "," + shapes.yLoc +
		//		           " (" + shapes.xBound + "x" + shapes.yBound + ")");
		//System.out.println("Object @ " + e.shapes.xLoc + "," + e.shapes.yLoc +
		//                   " (" + e.shapes.xBound + "x" + e.shapes.yBound + ")");
		//System.out.println("Collision vector = " + cv.x + "," + cv.y);
		
		if(e.hasTag("activeexit")){
			exited = true;
		}
		if(e.hasTag("solidimmovable")){
			super.resolveCollisionWithImmovable(e, cv);
		}
	}
	
	public void postStep(){
		points -= GameRunner.POINT_DRAIN_RATE;
	}
	
	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		if(isThrusting){
			thrusterFlame.moveTo(shapes.xLoc, shapes.yLoc);
			thrusterFlame.setAngle(thrustDirection);
			thrusterFlame.paintShapes(g2d, xoffset, yoffset, scale);
		}
		if(upAccel){
			upAccelFlame.moveTo(shapes.xLoc, shapes.yLoc);
			upAccelFlame.paintShapes(g2d, xoffset, yoffset, scale);
		}
		if(downAccel){
			downAccelFlame.moveTo(shapes.xLoc, shapes.yLoc);
			downAccelFlame.paintShapes(g2d, xoffset, yoffset, scale);
		}
		if(leftAccel){
			leftAccelFlame.moveTo(shapes.xLoc, shapes.yLoc);
			leftAccelFlame.paintShapes(g2d, xoffset, yoffset, scale);
		}
		if(rightAccel){
			rightAccelFlame.moveTo(shapes.xLoc, shapes.yLoc);
			rightAccelFlame.paintShapes(g2d, xoffset, yoffset, scale);
		}
		
		if(!damagedThisFrame)
			shapes.setColor(GameRunner.PLAYER_COLOR);
		else
			shapes.setColor(GameRunner.ENEMY_SHOT_COLOR);
		super.paintEntity(g2d, xoffset, yoffset, scale);
	}
}

class PlayerShieldEntity extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8813073743512620663L;
	private PlayerEntity player;
	public boolean isShieldOn;
	
	public PlayerShieldEntity(ShapeGroup sg, PlayerEntity player) {
		super(sg, "playershield");
		this.player = player;
	}
	
	public PlayerShieldEntity(PlayerEntity player){
		this(generateDefaultShapes(player.shapes.xLoc, player.shapes.yLoc, player.shapes.xBound), player);
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc, double size){
		List<BoundingShape> shapeList = new ArrayList<BoundingShape>(2);
		
		double xp1[] = {1.5*size, 2*size, 1.5*size, 1*size};
		double yp1[] = {0, 0, 1*size, 1*size};
		int np1 = 4;
		BoundingRotatingPolygon piece1 = new BoundingRotatingPolygon(xLoc, yLoc, np1, xp1, yp1);
		shapeList.add(piece1);
		
		double xp2[] = {1.5*size, 2*size, 1.5*size, 1*size};
		double yp2[] = {0, 0, -1*size, -1*size};
		int np2 = 4;
		BoundingRotatingPolygon piece2 = new BoundingRotatingPolygon(xLoc, yLoc, np2, xp2, yp2);
		shapeList.add(piece2);
		
		return new ShapeGroup(shapeList, GameRunner.PLAYER_SHIELD_COLOR);
	}
	
	@Override
	public Entity deepCopy(){
		throw new UnsupportedOperationException("can't deep copy player shields. THERE CAN ONLY BE ONE.");
	}
	
	public void updateLocation(){
		shapes.moveTo(player.shapes.xLoc, player.shapes.yLoc);
	}
	
	public void setOrientation(double radians){
		shapes.setAngle(radians);
	}

	@Override
	public void preStep() {}

	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {
		if(e.hasTag("projectile") && isShieldOn) //shield destroys projectiles when on
			e.isActive = false;
	}

	@Override
	public void postStep() {}
	
	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		if(isShieldOn)
			super.paintEntity(g2d, xoffset, yoffset, scale);
	}
}
