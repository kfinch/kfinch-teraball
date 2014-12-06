package gamePhysics2D;

import java.util.ArrayList;
import java.util.List;

public class BoundingCircle extends BoundingShape{

	public double radius;
	
	public BoundingCircle(double xLoc, double yLoc, double radius){
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		this.xBound = radius;
		this.yBound = radius;
		this.radius = radius;
	}
	
	@Override
	public void setSize(double xBound, double yBound){
		super.setSize(xBound, yBound);
		radius = Math.min(xBound, yBound);
	}
	
	@Override
	public List<Frame2d> getFrame(){
		List<Frame2d> result = new ArrayList<Frame2d>(1);
		result.add(new Circle2d(xLoc, yLoc, radius));
		return result;
	}
	
	@Override
	public Range2d getProjectionOnLine(double angle) {
		Vector2d rotateVector = new Vector2d(xLoc, yLoc);
		rotateVector.setAngle(rotateVector.angle() - angle);
		return new Range2d(rotateVector.y - radius, rotateVector.y + radius);
	}

	@Override
	public List<Double> getSatLines(BoundingShape s) {
		List<Point2d> otherPoints = s.getPoints();
		double closestMag = Double.POSITIVE_INFINITY;
		double currMag;
		Vector2d closestVector = null;
		Vector2d currVector;
		for(Point2d p : otherPoints){
			currVector = new Vector2d(p.x - xLoc, p.y - yLoc);
			currMag = currVector.magnitude();
			if(currMag < closestMag){
				closestMag = currMag;
				closestVector = currVector;
			}
		}
		
		List<Double> result = new ArrayList<Double>();
		result.add(closestVector.angle() + Math.PI/2);
		return result;
	}

	@Override
	public List<Point2d> getPoints() {
		List<Point2d> result = new ArrayList<Point2d>();
		result.add(new Point2d(xLoc, yLoc));
		return result;
	}

}
