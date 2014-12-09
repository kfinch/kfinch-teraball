package gamePhysics2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoundingPolygon extends BoundingShape {

	private static final long serialVersionUID = 2953094094154029252L;
	
	public int nPoints;
	public double xPoints[];
	public double yPoints[];
	
	public BoundingPolygon(double xLoc, double yLoc, int nPoints, double xPoints[], double yPoints[]){
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		this.nPoints = nPoints;
		this.xPoints = xPoints;
		this.yPoints = yPoints;
		calculateBounds();
	}
	
	public BoundingPolygon(int nPoints, double absXPoints[], double absYPoints[]){
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		
		for(int i=0; i<nPoints; i++){
			if(absXPoints[i] < minX)
				minX = absXPoints[i];
			if(absXPoints[i] > maxX)
				maxX = absXPoints[i];
			if(absYPoints[i] < minY)
				minY = absYPoints[i];
			if(absYPoints[i] > maxY)
				maxY = absYPoints[i];
		}
		
		this.nPoints = nPoints;
		this.xLoc = (minX+maxX)/2;
		this.yLoc = (minY+maxY)/2;
		this.xBound = Math.abs(minX-maxX)/2;
		this.yBound = Math.abs(minY-maxY)/2;
		
		this.xPoints = new double[nPoints];
		this.yPoints = new double[nPoints];
		for(int i=0; i<nPoints; i++){
			xPoints[i] = absXPoints[i] - xLoc;
			yPoints[i] = absYPoints[i] - yLoc;
		}
	}
	
	private void calculateBounds(){
		xBound = 0;
		yBound = 0;
		for(int i=0; i<nPoints; i++){
			if(Math.abs(xPoints[i]) > xBound)
				xBound = Math.abs(xPoints[i]); 
			if(Math.abs(yPoints[i]) > yBound)
				yBound = Math.abs(yPoints[i]); 
		}
	}
	
	@Override
	public List<Frame2d> getFrame(){
		List<Frame2d> result = new ArrayList<Frame2d>(nPoints);
		for(int i=0; i<nPoints-1; i++)
			result.add(new LineSegment2d(xPoints[i]+xLoc, yPoints[i]+yLoc, xPoints[i+1]+xLoc, yPoints[i+1]+yLoc));
		result.add(new LineSegment2d(xPoints[nPoints-1]+xLoc, yPoints[nPoints-1]+yLoc, xPoints[0]+xLoc, yPoints[0]+yLoc));
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
		List<Double> result = new ArrayList<Double>(nPoints);
		Vector2d satLine = new Vector2d(xPoints[nPoints-1]-xPoints[0], yPoints[nPoints-1]-yPoints[0]);
		result.add(satLine.angle());
		for(int i=0; i<nPoints-1; i++){
			satLine = new Vector2d(xPoints[i]-xPoints[i+1], yPoints[i]-yPoints[i+1]);
			result.add(satLine.angle());
		}
		return result;
	}

	@Override
	public List<Point2d> getPoints() {
		List<Point2d> result = new ArrayList<Point2d>(nPoints);
		for(int i=0; i<nPoints; i++){
			result.add(new Point2d(xPoints[i]+xLoc, yPoints[i]+yLoc));
		}
		return result;
	}

	@Override
	public Object clone() {
		return new BoundingPolygon(xLoc, yLoc, nPoints, Arrays.copyOf(xPoints, xPoints.length),
				                   Arrays.copyOf(yPoints, yPoints.length));
	}

}
