package gamePhysics2D;

public interface Frame2d {

	/**
	 * Returns point of intersection of ray with this frame.
	 * @param r Ray to check for intersection with.
	 * @return Point of intersection closest to the start of the ray (r.s),
	 *         or null if there is no intersection.
	 */
	public Point2d intersect(Ray2d r);
	
}
