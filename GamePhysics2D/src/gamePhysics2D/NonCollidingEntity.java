package gamePhysics2D;

import java.util.List;

/**
 * An entity that can never collide with another.
 * I'm actually not sure why I'd want this... but here it is anyway!
 * 
 * @author Kelton Finch
 */
public abstract class NonCollidingEntity extends Entity {

	public NonCollidingEntity(ShapeGroup shapes, String id) {
		super(shapes, id);
	}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {}

}
