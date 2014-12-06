package gamePhysics2D;

import java.util.List;

public class Ray2d {

	public Point2d s, f; //starting point of ray and finish point of ray
						 //I made these one letter vars because they're used a lot t_t
	
	public Ray2d(Point2d s, Point2d f){
		this.s = s;
		this.f = f;
	}
	
	public Ray2d(double sx, double sy, double fx, double fy){
		this.s = new Point2d(sx, sy);
		this.f = new Point2d(fx, fy);
	}
	
	public Ray2d(Point2d s, double angle, double range){
		this.s = s;
		this.f = new Point2d(s.x + range*Math.cos(angle), s.y + range*Math.sin(angle)); 
	}
	
	public Point2d intersection(Frame2d x){
		return x.intersect(this);
	}
	
	/**
	 * Returns the point where this ray hits a bounding shape.
	 * @param bs Bounding shape to check for collision with
	 * @return Point of collision, or null if there is no collision
	 */
	public Point2d rayHit(BoundingShape bs){
		if(!softHit(bs))
			return null;
		
		Point2d hitLoc = null;
		Ray2d truncate = new Ray2d(new Point2d(s.x, s.y), new Point2d(f.x, f.y)); //a crude clone
		List<Frame2d> frame = bs.getFrame();
		
		for(Frame2d part : frame){
			hitLoc = truncate.intersection(part);
			if(hitLoc != null)
				truncate.f = hitLoc;
		}
		return hitLoc;
	}
	
	/**
	 * Returns the point where this ray hits a bounding shape,
	 * and modifies this ray's end to be that point of collision as a side effect.
	 * @param bs Bounding shape to check for collision with
	 * @return Point of collision, or null if there is no collision
	 */
	public Point2d rayTruncate(BoundingShape bs){
		if(!softHit(bs))
			return null;
		
		Point2d hitLoc = null;
		List<Frame2d> frame = bs.getFrame();
		
		for(Frame2d part : frame){
			hitLoc = intersection(part);
			if(hitLoc != null)
				f = hitLoc;
		}
		return hitLoc;
	}
	
	public Point2d rayTruncate(ShapeGroup shapes){
		Point2d hitLoc = null;
		Point2d testLoc;
		List<BoundingShape> shapeList = shapes.getShapeList();
		for(BoundingShape shape : shapeList){
			testLoc = rayTruncate(shape);
			if(testLoc != null)
				hitLoc = testLoc;
		}
		return hitLoc;
	}
	
	private boolean softHit(BoundingShape bs){
		BoundingAABox aabb = new BoundingAABox(Math.abs((s.x + f.x)/2), Math.abs((s.y + f.y)/2),
											   Math.abs(s.x - f.x), Math.abs(s.y - f.y));
		return aabb.isCollidingSoft(bs);
	}
	
}
