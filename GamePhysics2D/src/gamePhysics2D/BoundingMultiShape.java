package gamePhysics2D;

import java.util.ArrayList;
import java.util.List;

public class BoundingMultiShape extends BoundingShape {

	public List<BoundingShape> shapes;
	
	public BoundingMultiShape(List<BoundingShape> shapes){
		this.shapes = shapes;
		generateBounds();
	}
	
	private void generateBounds(){
		double xMax = Double.NEGATIVE_INFINITY;
		double xMin = Double.POSITIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		for(BoundingShape shape : shapes){
			if(shape.xLoc-shape.xBound < xMin)
				xMin = shape.xLoc-shape.xBound;
			if(shape.xLoc+shape.xBound > xMax)
				xMax = shape.xLoc+shape.xBound;
			if(shape.yLoc-shape.yBound < yMin)
				yMin = shape.yLoc-shape.yBound;
			if(shape.yLoc+shape.yBound > yMax)
				yMax = shape.yLoc+shape.yBound;
		}
		xLoc = (xMin + xMax)/2;
		yLoc = (yMin + yMax)/2;
		xBound = (xMax - xMin)/2;
		yBound = (xMax - xMin)/2;
	}
	
	@Override
	public List<Frame2d> getFrame(){
		List<Frame2d> result = new ArrayList<Frame2d>();
		for(BoundingShape bs : shapes)
			result.addAll(bs.getFrame());
		return result;
	}
	
	@Override
	public Range2d getProjectionOnLine(double angle) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		Range2d r;
		for(BoundingShape bs : shapes){
			r = bs.getProjectionOnLine(angle);
			if(r.max > max)
				max = r.max;
			if(r.min > min)
				min = r.min;
		}
		return new Range2d(min, max);
	}

	@Override
	public List<Double> getSatLines(BoundingShape s) {
		List<Double> result = new ArrayList<Double>();
		for(BoundingShape bs : shapes)
			result.addAll(bs.getSatLines(s));
		return result;
	}

	@Override
	public List<Point2d> getPoints() {
		List<Point2d> result = new ArrayList<Point2d>();
		for(BoundingShape bs : shapes)
			result.addAll(bs.getPoints());
		return result;
	}
	

}
