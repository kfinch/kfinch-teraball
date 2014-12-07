package gamePhysics2D;

import java.util.Set;

public abstract class PhysicsEntity extends DynamicEntity {

	public double mass;
	public double bounciness;
	public double friction;
	
	public PhysicsEntity(ShapeGroup shapes, Set<String> tags, Vector2d velocity,
			             double mass, double bounciness, double friction){
		super(shapes,tags,velocity);
		this.mass = mass;
		this.bounciness = bounciness;
		this.friction = friction;
	}
	
	public PhysicsEntity(ShapeGroup shapes, Set<String> tags, double mass, double bounciness){
		this(shapes, tags, new Vector2d(), mass, bounciness, 0);
	}
	
	public PhysicsEntity(ShapeGroup shapes, Set<String> tags, double mass){
		this(shapes, tags, new Vector2d(), mass, 0, 0);
	}
	
	public PhysicsEntity(ShapeGroup shapes, String tag, Vector2d velocity, double mass, double bounciness, double friction){
		super(shapes, tag, velocity);
		this.mass = mass;
		this.bounciness = bounciness;
		this.friction = friction;
	}
	
	public PhysicsEntity(ShapeGroup shapes, String tag, double mass, double bounciness){
		this(shapes, tag, new Vector2d(), mass, bounciness, 0);
	}
	
	public PhysicsEntity(ShapeGroup shapes, String tag, double mass){
		this(shapes, tag, new Vector2d(), mass, 0, 0);
	}
	
	@Override
	public void moveStep(){
		if(velocity.magnitude() >= friction)
			velocity.setMagnitude(velocity.magnitude() - friction);
		else
			velocity.setMagnitude(0);
		super.moveStep();
	}
	
	protected void resolveCollisionWithImmovable(Entity e, Vector2d cv){
		shapes.translate(cv);
		Vector2d bounceVector = velocity.vectorProjection(cv.angle());
		bounceVector.multiply(1 + bounciness);
		velocity.subtract(bounceVector);
	}
	
	protected void resolveCollisionWithPhysics(Entity e, Vector2d cv){
		//TODO: Implement
	}
	
}
