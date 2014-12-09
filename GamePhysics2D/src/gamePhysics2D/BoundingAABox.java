package gamePhysics2D;

import java.util.ArrayList;
import java.util.List;

public class BoundingAABox extends BoundingShape {
	
	public BoundingAABox(double xLoc, double yLoc, double xBound, double yBound){
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		this.xBound = xBound;
		this.yBound = yBound;
	}

	//could change this to generate the list of segments in the constructor,
	//and just return it on calling this, but that will cause problems if xLoc, yLoc, xBound, or yBound
	//are ever changed, or if the calling object messes with the returned list.
	@Override
	public List<Frame2d> getFrame(){
		List<Frame2d> result = new ArrayList<Frame2d>(4);
		result.add(new LineSegment2d(xLoc-xBound, yLoc-yBound, xLoc+xBound, yLoc-yBound));
		result.add(new LineSegment2d(xLoc+xBound, yLoc-yBound, xLoc+xBound, yLoc+yBound));
		result.add(new LineSegment2d(xLoc+xBound, yLoc+yBound, xLoc-xBound, yLoc+yBound));
		result.add(new LineSegment2d(xLoc-xBound, yLoc+yBound, xLoc-xBound, yLoc-yBound));
		return result;
	}
	
	@Override
	public Range2d getProjectionOnLine(double angle) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		Vector2d rotateVector;
		for(Point2d p : getPoints()){
			rotateVector = new Vector2d(p.x, p.y);
			rotateVector.setAngle(rotateVector.angle() - angle);
			if(rotateVector.y > max)
				max = rotateVector.y;
			if(rotateVector.y < min)
				min = rotateVector.y;
		}
		return new Range2d(min, max);
	}

	@Override
	public List<Double> getSatLines(BoundingShape s) {
		List<Double> result = new ArrayList<Double>(2);
		result.add((double) 0);
		result.add(Math.PI/2);
		return result;
	}

	@Override
	public List<Point2d> getPoints() {
		List<Point2d> result = new ArrayList<Point2d>(4);
		result.add(new Point2d(xLoc + xBound, yLoc + yBound));
		result.add(new Point2d(xLoc - xBound, yLoc + yBound));
		result.add(new Point2d(xLoc + xBound, yLoc - yBound));
		result.add(new Point2d(xLoc - xBound, yLoc - yBound));
		return result;
	}
	
	public BoundingAABox merge(BoundingAABox other){
		double xMin = Math.min(this.xLoc - this.xBound, other.xLoc - other.xBound);
		double xMax = Math.max(this.xLoc + this.xBound, other.xLoc + other.xBound);
		double yMin = Math.min(this.yLoc - this.yBound, other.yLoc - other.yBound);
		double yMax = Math.max(this.yLoc + this.yBound, other.yLoc + other.yBound);
		
		double mergeXLoc = (xMax - xMin) / 2;
		double mergeYLoc = (yMax - yMin) / 2;
		double mergeXBound = (xMax + xMin) / 2;
		double mergeYBound = (yMax + yMin) / 2;
		
		return new BoundingAABox(mergeXLoc, mergeYLoc, mergeXBound, mergeYBound);
	}

	@Override
	public Object clone() {
		return new BoundingAABox(xLoc, yLoc, xBound, yBound);
	}

}
