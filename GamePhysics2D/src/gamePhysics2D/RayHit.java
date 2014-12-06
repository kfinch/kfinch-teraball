package gamePhysics2D;

/**
 * Encapsulates a "ray hit" within the simulator.
 * This is effectively a container class for an Entity (the entity hit) and a Point2d (the point of the hit)
 * 
 * @author Kelton Finch
 */
public class RayHit {

	public Entity e;
	public Point2d p;
	
	public RayHit(Entity e, Point2d p){
		this.e = e;
		this.p = p;
	}
	
	//yaaay I did it...
	
}
