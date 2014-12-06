package gamePhysics2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ShapeGroup {

	private List<ColoredShape> coloredShapes;
	private List<BoundingShape> shapes;
	public double xLoc, yLoc, xBound, yBound;
	
	public ShapeGroup(List<ColoredShape> coloredShapes){
		shapes = new ArrayList<BoundingShape>(coloredShapes.size());
		for(ColoredShape cs : coloredShapes)
			shapes.add(cs.shape);
		
		this.coloredShapes = coloredShapes;
		
		updateDimensions();
	}
	
	public ShapeGroup(List<BoundingShape> shapes, Color color){
		this.shapes = shapes;
		
		coloredShapes = new ArrayList<ColoredShape>(shapes.size());
		for(BoundingShape shape : shapes){
			coloredShapes.add(new ColoredShape(shape, color));
		}
		
		updateDimensions();
	}
	
	public ShapeGroup(BoundingShape shape, Color color){
		shapes = new ArrayList<BoundingShape>(1);
		shapes.add(shape);
		
		coloredShapes = new ArrayList<ColoredShape>(1);
		coloredShapes.add(new ColoredShape(shape, color));
		
		xLoc = shape.xLoc;
		yLoc = shape.yLoc;
		xBound = shape.xBound;
		yBound = shape.yBound;
	}
	
	public ShapeGroup(BoundingShape shape){
		this(shape, null);
	}
	
	/**
	 * Updates the location and bounds of the ShapeGroup.
	 * These values are automatically updated when the ShapeGroup is permuted via its own methods,
	 * however if the user changes underlying shapes without using ShapeGroup's methods,
	 * it may be necessary to call this method to ensure collisions are still handled properly.
	 */
	public void updateDimensions(){
		double xMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
		for(BoundingShape shape : shapes){
			if(shape.xLoc - shape.xBound < xMin)
				xMin = shape.xLoc - shape.xBound;
			if(shape.xLoc + shape.xBound > xMax)
				xMax = shape.xLoc + shape.xBound;
			if(shape.yLoc - shape.yBound < yMin)
				yMin = shape.yLoc - shape.yBound;
			if(shape.yLoc + shape.yBound > yMax)
				yMax = shape.yLoc + shape.yBound;
		}
		xLoc = (xMax + xMin) / 2;
		yLoc = (yMax + yMin) / 2;
		xBound = (xMax - xMin) / 2;
		yBound = (yMax - yMin) / 2;
	}
	
	public int getNumShapes(){
		return coloredShapes.size();
	}
	
	public List<BoundingShape> getShapeList(){
		return shapes;
	}
	
	public void setColor(Color color){
		for(ColoredShape coloredShape : coloredShapes){
			coloredShape.color = color;
		}
	}
	
	public void add(BoundingShape shape, Color color){
		coloredShapes.add(new ColoredShape(shape, color));
		shapes.add(shape);
		
		double xMin = xLoc - xBound;
		double xMax = xLoc + xBound;
		double yMin = yLoc - yBound;
		double yMax = yLoc + yBound;
		if(shape.xLoc - shape.xBound < xMin)
			xMin = shape.xLoc - shape.xBound;
		if(shape.xLoc + shape.xBound > xMax)
			xMax = shape.xLoc + shape.xBound;
		if(shape.yLoc - shape.yBound < yMin)
			yMin = shape.yLoc - shape.yBound;
		if(shape.yLoc + shape.yBound > yMax)
			yMax = shape.yLoc + shape.yBound;
		xLoc = (xMax + xMin) / 2;
		yLoc = (yMax + yMin) / 2;
		xBound = (xMax - xMin) / 2;
		yBound = (yMax - yMin) / 2;
	}
	
	public void addAll(ShapeGroup sg){
		coloredShapes.addAll(sg.coloredShapes);
		shapes.addAll(sg.shapes);
		updateDimensions();
	}
	
	public void translate(Vector2d v){
		xLoc += v.x;
		yLoc += v.y;
		for(BoundingShape shape : shapes){
			shape.translate(v);
		}
	}
	
	public void moveTo(double newXLoc, double newYLoc){
		Vector2d translateVector = new Vector2d(newXLoc - xLoc, newYLoc - yLoc);
		translate(translateVector);
	}
	
	public void moveTo(Point2d p){
		moveTo(p.x, p.y);
	}
	
	/*
	 * Rotates only rotatable pieces
	 */
	public void rotate(double radians){
		for(BoundingShape shape : shapes){
			if(shape instanceof BoundingRotatingPolygon)
				((BoundingRotatingPolygon)shape).rotate(radians);
		}
	}
	
	/*
	 * Rotates only rotatable pieces
	 */
	public void setAngle(double radians){
		for(BoundingShape shape : shapes){
			if(shape instanceof BoundingRotatingPolygon)
				((BoundingRotatingPolygon)shape).setAngle(radians);
		}
	}
	
	public Vector2d resolveCollision(ShapeGroup sg){
		if(Math.abs(xLoc - sg.xLoc) > xBound + sg.xBound && Math.abs(yLoc - sg.yLoc) > yBound + sg.yBound)
			return null;
		
		Vector2d part, total;
		boolean hit = false;
		total = new Vector2d(0,0);
		for(BoundingShape shape1 : shapes){
			for(BoundingShape shape2 : shapes){
				part = shape1.resolveCollision(shape2);
				if(part != null){
					hit = true;
					total.add(part);
					translate(part);
				}
			}
		}
		
		if(!hit)
			return null;
		
		total.multiply(-1);
		translate(total);
		total.multiply(-1);
		return total;
	}
	
	/*
	 * Generates a "pipe", a segmented line with thickness.
	 * Returns a ShapeGroup representing the requested pipe.
	 * Adding sharp turns or particularly short segments may result in odd looking shapes.
	 * A full 180 degree turn in between segments is not possible and results in undefined behavior. 
	 * nPoints must be >= 2, or >= 3 if it wraps around.
	 */
	public static ShapeGroup generatePipe(double thickness, boolean wrapsAround, int nPoints,
			                              double xPoints[], double yPoints[], Color color){
		int nShapes = (wrapsAround) ? nPoints : nPoints-1;
		List<BoundingShape> resultShapes = new ArrayList<BoundingShape>(nShapes);
		
		double jointThickness[] = new double[nPoints]; //(secant of half angle) * (thickness) = (thickness at joint)
		double jointAngles[] = new double[nPoints]; //angle of joint (facing left along the pipe)
		int prevIndex, nextIndex;
		Vector2d prevVec, nextVec;
		double prevAngle, nextAngle;
		
		//populate jointThickness[] and jointAngles[]
		for(int i=0; i<nPoints; i++){
			prevIndex = (i==0) ? nPoints-1 : i-1;
			nextIndex = (i==nPoints-1) ? 0 : i+1;
			prevVec = new Vector2d(xPoints[i] - xPoints[prevIndex], yPoints[i] - yPoints[prevIndex]);
			nextVec = new Vector2d(xPoints[nextIndex] - xPoints[i], yPoints[nextIndex] - yPoints[i]);
			prevAngle = prevVec.angle();
			nextAngle = nextVec.angle();
				
			if(!wrapsAround && (i==0 || i==nPoints-1)){
				jointThickness[i] = thickness;
				jointAngles[i] = (i==0) ? nextAngle : prevAngle;
				jointAngles[i] += Math.PI/2;
			}
			else{
				jointThickness[i] = Math.abs(prevAngle - nextAngle);
				if(jointThickness[i] > Math.PI)
					jointThickness[i] = Math.PI*2 - jointThickness[i];
				jointThickness[i] /= 2;
				jointThickness[i] = thickness / Math.cos(jointThickness[i]);
				
				if(Math.abs(nextAngle-prevAngle) >= Math.PI)
					nextAngle += Math.PI*2;
				jointAngles[i] = (nextAngle+prevAngle)/2;
				jointAngles[i] += Math.PI/2;
			}
		}
		
		int polyNPoints = 4;
		Vector2d thicknessVector = new Vector2d();
		int j;
		
		//now generate the polygonal pieces
		for(int i=0; i<nPoints; i++){
			j = i+1;
			if(j == nPoints){
				if(!wrapsAround)
					continue;
				j = 0;
			}
			
			double polyXPoints[] = new double[polyNPoints];
			double polyYPoints[] = new double[polyNPoints];
			
			thicknessVector.setAngleAndMagnitude(jointAngles[i], jointThickness[i]);
			polyXPoints[0] = xPoints[i] + thicknessVector.x;
			polyYPoints[0] = yPoints[i] + thicknessVector.y;
			polyXPoints[1] = xPoints[i] - thicknessVector.x;
			polyYPoints[1] = yPoints[i] - thicknessVector.y;
			thicknessVector.setAngleAndMagnitude(jointAngles[j], jointThickness[j]);
			polyXPoints[2] = xPoints[j] - thicknessVector.x;
			polyYPoints[2] = yPoints[j] - thicknessVector.y;
			polyXPoints[3] = xPoints[j] + thicknessVector.x;
			polyYPoints[3] = yPoints[j] + thicknessVector.y;
			
			resultShapes.add(new BoundingPolygon(4, polyXPoints, polyYPoints));
		}
		
		return new ShapeGroup(resultShapes, color);
	}
	
	/**
	 * Paints this group of shapes on a graphics context.
	 * @param g2d The graphics object to paint with
	 * @param xoffset The (games) x coordinate of the left edge of the graphical display 
	 * @param yoffset The (games) y coordinate of the right edge of the graphical display
	 * @param scale The scale at which it is displayed.
	 * 				This number is the ratio of one game distance unit to one pixel on the display
	 */
	public void paintShapes(Graphics2D g2d, double xoffset, double yoffset, double scale){
		for(ColoredShape coloredShape : coloredShapes){
			BoundingShape shape = coloredShape.shape;
			
			//if shape's color isn't set, will use whatever color the graphics are currently set to
			//this functionality is used to paint alpha composites if needed.
			//TODO: Would like to handle such painting more gracefully
			if(coloredShape.color != null)
				g2d.setColor(coloredShape.color);
			
			if(shape instanceof BoundingAABox){
				int rectXLoc = (int) ((shape.xLoc - shape.xBound - xoffset) * scale);
				int rectYLoc = (int) ((shape.yLoc - shape.yBound - yoffset) * scale);
				int rectXSize = (int) (shape.xBound * 2 * scale);
				int rectYSize = (int) (shape.yBound * 2 * scale);
				g2d.fillRect(rectXLoc, rectYLoc, rectXSize, rectYSize);
			}
			else if(shape instanceof BoundingCircle){
				int circleXLoc = (int) ((shape.xLoc - shape.xBound - xoffset) * scale);
				int circleYLoc = (int) ((shape.yLoc - shape.yBound - yoffset) * scale);
				int circleXSize = (int) (shape.xBound * 2 * scale);
				int circleYSize = (int) (shape.yBound * 2 * scale);
				g2d.fillOval(circleXLoc, circleYLoc, circleXSize, circleYSize);
			}
			else if(shape instanceof BoundingPolygon){
				BoundingPolygon bp = ((BoundingPolygon) shape);
				int polyXPoints[] = new int[bp.nPoints];
				int polyYPoints[] = new int[bp.nPoints];
				for(int i=0; i<bp.nPoints; i++){
					polyXPoints[i] = (int) ((bp.xPoints[i] + bp.xLoc - xoffset) * scale);
					polyYPoints[i] = (int) ((bp.yPoints[i] + bp.yLoc - yoffset) * scale);
				}
				g2d.fillPolygon(polyXPoints, polyYPoints, bp.nPoints);
			}
			else if(shape instanceof BoundingLineSegment){
				BoundingLineSegment bls = (BoundingLineSegment) shape;
				int lineX1 = (int) ((bls.x1 - xoffset) * scale);
				int lineY1 = (int) ((bls.y1 - yoffset) * scale);
				int lineX2 = (int) ((bls.x2 - xoffset) * scale);
				int lineY2 = (int) ((bls.y2 - yoffset) * scale);
				g2d.drawLine(lineX1, lineY1, lineX2, lineY2);
			}
		}
	}
	
}

class ColoredShape {
	 public BoundingShape shape;
	 public Color color;
	 public ColoredShape(BoundingShape shape, Color color){
		 this.shape = shape;
		 this.color = color;
	 }
}