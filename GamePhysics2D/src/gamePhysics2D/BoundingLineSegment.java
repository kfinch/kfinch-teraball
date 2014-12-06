package gamePhysics2D;

import java.util.ArrayList;
import java.util.List;

public class BoundingLineSegment extends BoundingShape{

	public double x1, y1, x2, y2;
	
	public BoundingLineSegment(double x1, double y1, double x2, double y2){
		setDimensions(x1, y1, x2, y2);
	}
	
	public BoundingLineSegment(Point2d p1, Point2d p2){
		this(p1.x, p1.y, p2.x, p2.y);
	}
	
	public void setDimensions(double x1, double y1, double x2, double y2){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		double xMin = Math.min(x1, x2);
		double xMax = Math.max(x1, x2);
		double yMin = Math.min(y1, y2);
		double yMax = Math.max(y1, y2);
		xLoc = (xMin + xMax)/2;
		yLoc = (yMin + yMax)/2;
		xBound = (xMax - xMin)/2;
		yBound = (yMax - yMin)/2;
	}
	
	@Override
	public List<Frame2d> getFrame(){
		List<Frame2d> result = new ArrayList<Frame2d>(1);
		result.add(new LineSegment2d(x1, y1, x2, y2));
		return result;
	}
	
	@Override
	public Range2d getProjectionOnLine(double angle) {
		/*
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
		*/
		
		
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		
		Vector2d rotateVector = new Vector2d(x1, y1);
		rotateVector.setAngle(rotateVector.angle() - angle);
		if(rotateVector.y > max)
			max = rotateVector.y;
		if(rotateVector.y < min)
			min = rotateVector.y;
		
		rotateVector = new Vector2d(x2, y2);
		rotateVector.setAngle(rotateVector.angle() - angle);
		if(rotateVector.y > max)
			max = rotateVector.y;
		if(rotateVector.y < min)
			min = rotateVector.y;
		
		return new Range2d(min, max);
		
	}

	@Override
	public List<Double> getSatLines(BoundingShape s) {
		List<Double> result = new ArrayList<Double>(1);
		Vector2d satLine = new Vector2d(x1-x2, y1-y2);
		result.add(satLine.angle());
		return result;
	}

	@Override
	public List<Point2d> getPoints() {
		List<Point2d> result = new ArrayList<Point2d>(2);
		result.add(new Point2d(x1,y1));
		result.add(new Point2d(x2,y2));
		return result;
	}

}
