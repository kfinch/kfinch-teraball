package gamePhysics2D;

import java.util.Set;

/**
 * An entity that can move.
 * 
 * @author Kelton Finch
 */
public abstract class DynamicEntity extends Entity {

	private static final long serialVersionUID = -7875904286933372690L;
	
	public Vector2d velocity;
	
	public DynamicEntity(ShapeGroup shapes, Set<String> tags) {
		super(shapes, tags);
		this.velocity = new Vector2d();
	}
	
	
	public DynamicEntity(ShapeGroup shapes, Set<String> tags, Vector2d velocity){
		super(shapes, tags);
		this.velocity = velocity;
	}
	
	public DynamicEntity(ShapeGroup shapes, String tag) {
		super(shapes, tag);
		this.velocity = new Vector2d();
	}
	
	public DynamicEntity(ShapeGroup shapes, String tag, Vector2d velocity){
		super(shapes, tag);
		this.velocity = velocity;
	}
	
	@Override
	public void moveStep(){
		shapes.translate(velocity);
	}
	
	@Override
	public void resolveCollision(Entity e, Vector2d cv){
		if(e instanceof DynamicEntity)
			cv.multiply(0.5);
		shapes.translate(cv);
		velocity.add(cv);
	}

}
