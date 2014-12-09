package gamePhysics2D;

import java.util.ArrayList;
import java.util.List;

public class BoundingPoint extends BoundingShape{

	private static final long serialVersionUID = -7676682664712473665L;

	public BoundingPoint(double xLoc, double yLoc){
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		this.xBound = 0;
		this.yBound = 0;
	}
	
	@Override
	/*
	 * A point has no frame (it can never obstruct LoS), so an empty list is returned.
	 */
	public List<Frame2d> getFrame(){
		return new ArrayList<Frame2d>();
	}
	
	@Override
	public Range2d getProjectionOnLine(double angle) {
		Vector2d rotateVector = new Vector2d(xLoc, yLoc);
		rotateVector.setAngle(rotateVector.angle() - angle);
		return new Range2d(rotateVector.y, rotateVector.y);
	}

	@Override
	/*
	 * A point has no SAT lines, and so only uses the SAT lines of the other shape when checking for collision.
	 * Note that this means that a BoundingPoint can never collide with another BoundingPoint
	 */
	public List<Double> getSatLines(BoundingShape s) {
		return new ArrayList<Double>();
	}

	@Override
	public List<Point2d> getPoints() {
		List<Point2d> result = new ArrayList<Point2d>();
		result.add(new Point2d(xLoc, yLoc));
		return result;
	}

	@Override
	public Object clone() {
		return new BoundingPoint(xLoc, yLoc);
	}
	
}
