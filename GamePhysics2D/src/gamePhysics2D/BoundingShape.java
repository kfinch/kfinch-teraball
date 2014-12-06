package gamePhysics2D;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An abstract representation of a 2d shape that can collide with other shapes on a coordinate plane.
 * Common code for helping detect and resolve such collisions is included.
 * Additional shape types seeking to extend this class must be able to project themselves on an axis,
 * return a list of relevant SAT lines, and return a list of relevant collision points.
 * 
 * The idea for the methods and techniques to implement this kind of collision detection was taken from
 * http://www.metanetsoftware.com/technique/tutorialA.html
 * 
 * @author Kelton Finch
 */
public abstract class BoundingShape {

	public double xLoc, yLoc, xBound, yBound;
	
	public abstract List<Frame2d> getFrame();
	
	public abstract Range2d getProjectionOnLine(double angle);
	
	public abstract List<Double> getSatLines(BoundingShape s);
	
	public abstract List<Point2d> getPoints();
	
	/**
	 * Sets size of axis aligned bounding box
	 */
	public void setSize(double xBound, double yBound){
		this.xBound = xBound;
		this.yBound = yBound;
	}
	
	/**
	 * Translates the shape by the given vector.
	 */
	public void translate(Vector2d translateVector){
		xLoc += translateVector.x;
		yLoc += translateVector.y;
	}
	
	/**
	 * Determines if this shape is colliding with s, and returns a 'correction vector' if needed.
	 * @param s Other shape to check for collision with.
	 * @return The smallest magnitude vector by which this can be translated to be no longer
	 * 	       colliding with s, or returns null if this shape is already not colliding with s.
	 */
	public Vector2d resolveCollision(BoundingShape s){
		//first checks if axis aligned bounding boxes collide, if they do not then shapes can't collide.
		if(!isCollidingSoft(s))
			return null;
		
		//if both this and the other shape are simple aabb's, then much more simple collision detection method.
		if(this instanceof BoundingAABox && s instanceof BoundingAABox)
			return resolveBoundingAABoxCollision(s);
		
		//first two tests failed to resolve collision, use this more computationally expensive method.
		return resolveCollisionHard(s);
	}
	
	/**
	 * Checks only if this and s have colliding axis aligned bounding boxes.
	 */
	public boolean isCollidingSoft(BoundingShape s){
		if(Math.abs(xLoc - s.xLoc) <= xBound + s.xBound && Math.abs(yLoc - s.yLoc) <= yBound + s.yBound)
			return true;
		return false;
	}
	
	/**
	 * Resolves a collision between this shape and s. This method is only called when this shape and s
	 * have intersecting aabb's, and so more computationally intensive methods are needed to determine
	 * if they are actually colliding.
	 * This method uses the "Seperating Axis Theorem" to detect and resolve collision.
	 * http://en.wikipedia.org/wiki/Hyperplane_separation_theorem
	 */
	public Vector2d resolveCollisionHard(BoundingShape s){
		Vector2d resolutionVector = null; //this is the result, is set when appropriate
		Set<Double> anglesProjected = new HashSet<Double>();
		Range2d thisProj, sProj;
		double minimumDisplace = Double.POSITIVE_INFINITY;
		
		for(double projectionAngle : s.getSatLines(this)){ //loop through this shape's sat lines
			//if we've already checked sat line, no reason to check again
			if(anglesProjected.contains(projectionAngle) ||
			   anglesProjected.contains(projectionAngle+Math.PI) ||
			   anglesProjected.contains(projectionAngle-Math.PI))
				continue;
			
			//if we haven't checked this sat line, add it to the list of checked things for future loops
			anglesProjected.add(projectionAngle);
			
			//generate the each shape's projection on the sat line
			thisProj = getProjectionOnLine(projectionAngle);
			sProj = s.getProjectionOnLine(projectionAngle);
			
			//the projections don't touch. If even one projection doesn't touch, there is no collision
			if(thisProj.min > sProj.max || sProj.min > thisProj.max)
				return null;

			//projections do touch. Find the 'shortest way out' along this axis,
			//and set it as best way out if it's shorter than previously found vectors.
			if(thisProj.max - sProj.min < minimumDisplace){
				minimumDisplace = thisProj.max - sProj.min;
				resolutionVector = new Vector2d();
				resolutionVector.setAngleAndMagnitude(projectionAngle - Math.PI/2, thisProj.max-sProj.min);
			}
			if(sProj.max - thisProj.min < minimumDisplace){
				minimumDisplace = sProj.max - thisProj.min;
				resolutionVector = new Vector2d();
				resolutionVector.setAngleAndMagnitude(projectionAngle + Math.PI/2, sProj.max-thisProj.min);
			}
			
			//TODO: remove debugging
			/*
			System.out.println("Angle: " + String.format("%.4f", projectionAngle/Math.PI) + " pi");
			System.out.println("My poly from " + String.format("%.2f", thisProj.min) + " to " + String.format("%.2f", thisProj.max));
			System.out.println("BP poly from " + String.format("%.2f", sProj.min) + " to " + String.format("%.2f", sProj.max));
			System.out.println("Resolution Vector: " + String.format("%.2f", resolutionVector.x) + " " + String.format("%.2f", resolutionVector.y) 
							   + "(mag = " + String.format("%.2f", resolutionVector.magnitude()) + ")\n");
			*/
			
		}
		
		for(double projectionAngle : getSatLines(s)){ //repeat the above process for the other shape's sat lines
			if(anglesProjected.contains(projectionAngle) ||
			   anglesProjected.contains(projectionAngle+Math.PI) ||
			   anglesProjected.contains(projectionAngle-Math.PI))
				continue;

			anglesProjected.add(projectionAngle);
			
			thisProj = getProjectionOnLine(projectionAngle);
			sProj = s.getProjectionOnLine(projectionAngle);
			
			if(thisProj.min > sProj.max || sProj.min > thisProj.max)
				return null;

			if(thisProj.max - sProj.min < minimumDisplace){
				minimumDisplace = thisProj.max - sProj.min;
				resolutionVector = new Vector2d();
				resolutionVector.setAngleAndMagnitude(projectionAngle - Math.PI/2, thisProj.max-sProj.min);
			}
			if(sProj.max - thisProj.min < minimumDisplace){
				minimumDisplace = sProj.max - thisProj.min;
				resolutionVector = new Vector2d();
				resolutionVector.setAngleAndMagnitude(projectionAngle + Math.PI/2, sProj.max-thisProj.min);
			}
			
			//TODO: remove debugging
			/*
			System.out.println("Angle: " + String.format("%.4f", projectionAngle/Math.PI) + " pi");
			System.out.println("My poly from " + String.format("%.2f", thisProj.min) + " to " + String.format("%.2f", thisProj.max));
			System.out.println("BP poly from " + String.format("%.2f", sProj.min) + " to " + String.format("%.2f", sProj.max));
			System.out.println("Resolution Vector: " + String.format("%.2f", resolutionVector.x) + " " + String.format("%.2f", resolutionVector.y) 
							   + "(mag = " + String.format("%.2f", resolutionVector.magnitude()) + ")\n");
			*/
		}
		
		return resolutionVector;
	}
	
	/**
	 * Determines if this shape is colliding with aabb "s", and returns a 'correction vector' if needed.
	 * Assumes that this bounding shape is an AABB.
	 * @param s Other aabb to check for collision with.
	 * @return The smallest magnitude vector by which this can be translated to be no longer
	 * 	       colliding with s, or returns null if this aabb is already not colliding with s.
	 */
	public Vector2d resolveBoundingAABoxCollision(BoundingShape s){
		double xDist = s.xLoc - xLoc;
		double yDist = s.yLoc - yLoc;
		double xInfringe = (xBound + s.xBound) - Math.abs(xDist);
		double yInfringe = (yBound + s.yBound) - Math.abs(yDist);
		Vector2d resolutionVector;
		
		if(xInfringe < yInfringe){
			if(xDist > 0)
				resolutionVector = new Vector2d(-xInfringe, 0);
			else
				resolutionVector = new Vector2d(xInfringe, 0);
		}
		else{
			if(yDist > 0)
				resolutionVector = new Vector2d(0, -yInfringe);
			else
				resolutionVector = new Vector2d(0, yInfringe);
		}
		
		return resolutionVector;
	}
}
