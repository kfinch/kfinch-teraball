package gamePhysics2D;

import java.util.Arrays;

public class BoundingRotatingPolygon extends BoundingPolygon {
	
	private static final long serialVersionUID = -2690699836988454167L;
	
	private double startingXPoints[];
	private double startingYPoints[];
	
	public BoundingRotatingPolygon(double xLoc, double yLoc, int nPoints, double xPoints[], double yPoints[]){
		super(xLoc,yLoc,nPoints,xPoints,yPoints);
		startingXPoints = new double[nPoints];
		startingYPoints = new double[nPoints];
		for(int i=0; i<nPoints; i++){
			startingXPoints[i] = xPoints[i];
			startingYPoints[i] = yPoints[i];
		}
		calculateBounds();
	}
	
	private void calculateBounds(){
		double dist;
		double max = 0;
		for(int i=0; i<nPoints; i++){
			dist = Math.sqrt(Math.pow(xPoints[i],2) + Math.pow(yPoints[i],2));
			if(dist > max)
				max = dist;
		}
		xBound = max;
		yBound = max;
	}
	
	public void rotate(double radians){
		Vector2d rot;
		for(int i=0; i<nPoints; i++){
			rot = new Vector2d(xPoints[i],yPoints[i]);
			rot.setAngle(rot.angle() + radians);
			xPoints[i] = rot.x;
			yPoints[i] = rot.y;
		}
	}
	
	public void setAngle(double radians){
		Vector2d rot;
		for(int i=0; i<nPoints; i++){
			rot = new Vector2d(startingXPoints[i], startingYPoints[i]);
			rot.setAngle(rot.angle() + radians);
			xPoints[i] = rot.x;
			yPoints[i] = rot.y;
		}
	}
	
	@Override
	public Object clone(){
		BoundingRotatingPolygon result =
				new BoundingRotatingPolygon(xLoc, yLoc, nPoints, Arrays.copyOf(xPoints, xPoints.length),
						                    Arrays.copyOf(yPoints, yPoints.length));
		result.startingXPoints = Arrays.copyOf(startingXPoints, startingXPoints.length);
		result.startingYPoints = Arrays.copyOf(startingYPoints, startingYPoints.length);
		return result;
	}
}
