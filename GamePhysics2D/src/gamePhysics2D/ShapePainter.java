package gamePhysics2D;

import java.awt.Graphics2D;

/**
 * Class handles painting given shapes to the graphics context.
 * 
 * Currently this class is unused, its function is handled by ShapeGroup.
 * 
 * @author Kelton Finch
 */
public class ShapePainter {

	public static void paintShape(BoundingShape shape, Graphics2D g2d, int xoffset, int yoffset, double scale){
		if(shape instanceof BoundingAABox){
			g2d.fillRect((int)(shape.xLoc-shape.xBound-xoffset), (int)(shape.yLoc-shape.yBound-yoffset),
						 (int)(shape.xBound*2), (int)(shape.yBound*2));
		}
		else if(shape instanceof BoundingCircle){
			g2d.fillOval((int)(shape.xLoc-shape.xBound-xoffset), (int)(shape.yLoc-shape.yBound-yoffset),
				     	 (int)(shape.xBound*2), (int)(shape.yBound*2));
		}
		//else if(shape instanceof BoundingRotatingPolygon){
		//	BoundingRotatingPolygon bp = ((BoundingRotatingPolygon) shape);
		//	int xPoints[] = new int[bp.nPoints];
		//	int yPoints[] = new int[bp.nPoints];
		//	for(int i=0; i<bp.nPoints; i++){
		//		xPoints[i] = (int)(bp.xPoints[i]+bp.xLoc-xoffset);
		//		yPoints[i] = (int)(bp.yPoints[i]+bp.yLoc-yoffset);
		//	}
		//	g2d.fillPolygon(xPoints, yPoints, bp.nPoints);
		//}
		else if(shape instanceof BoundingPolygon){
			BoundingPolygon bp = ((BoundingPolygon) shape);
			int xPoints[] = new int[bp.nPoints];
			int yPoints[] = new int[bp.nPoints];
			for(int i=0; i<bp.nPoints; i++){
				xPoints[i] = (int)(bp.xPoints[i]+bp.xLoc-xoffset);
				yPoints[i] = (int)(bp.yPoints[i]+bp.yLoc-yoffset);
			}
			g2d.fillPolygon(xPoints, yPoints, bp.nPoints);
		}
		else if(shape instanceof BoundingLineSegment){
			BoundingLineSegment bls = (BoundingLineSegment) shape;
			g2d.drawLine((int)bls.x1-xoffset, (int)bls.y1-yoffset, (int)bls.x2-xoffset, (int)bls.y2-yoffset);
		}
	}
	
}
