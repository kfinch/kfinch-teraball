package gamePhysics2D;

import java.util.ArrayList;
import java.util.List;

public class Circle2d implements Frame2d {

	public double x, y, r;
	
	public Circle2d(Point2d p, double r){
		this.x = p.x;
		this.y = p.y;
		this.r = r;
	}
	
	public Circle2d(double x, double y, double r){
		this.x = x;
		this.y = y;
		this.r = r;
	}

	@Override
	public Point2d intersect(Ray2d l) {
		boolean vertical = (l.s.x == l.f.x);
		double la, lb, qa, qb, qc; //a and b for line equation, a, b, and c for quadratic formula
		double rx1, rx2, ry1, ry2; //candidate result values (at most 2 intersections between line and circle)
		Range2d range; //the x range of the line (or the y range if it's vertical)
		
		if(vertical){ //can't generate y = a*x + b for vertical line
			//given line as x = p1.x and circle as (x - xoffset)^2 + (y - yoffset)^2 = radius^2,
			//generate quadratic formula values qa, qb, and qc in order to solve for y
			qa = 1;
			qb = -2*y;
			qc = l.s.x*l.s.x + x*x - 2*l.s.x*x + y*y - r*r;
			
			//find the two solutions for y using the quadratic formula
			ry1 = (-qb + Math.sqrt(qb*qb - 4*qa*qc)) / (2*qa);
			ry2 = (-qb - Math.sqrt(qb*qb - 4*qa*qc)) / (2*qa);
			
			//now generate the corresponding x values (which are just l.s.x)
			rx1 = l.s.x;
			rx2 = l.s.x;
			
			//TODO: fold range check in to helper method?
			//generate the range of the line, and check if the intersect points are within that range
			range = new Range2d(Math.min(l.s.y, l.f.y), Math.max(l.s.y, l.f.y));
			if(!range.inRangeInclusive(ry1))
				rx1 = Double.NaN;
			if(!range.inRangeInclusive(ry2))
				rx2 = Double.NaN;
			
			//generate the result
			return generateResult(rx1, ry1, rx2, ry2, l.s);
		}
		else{
			//convert to form y = a*x + b
			la = (l.f.y - l.s.y)/(l.f.x - l.s.x);
			lb = l.s.y - la*l.s.x;
		
			//given line as y = a*x + b and circle as (x - xoffset)^2 + (y - yoffset)^2 = radius^2,
			//generate quadratic formula values qa, qb, and qc in order to solve for x
			qa = la*la + 1;
			qb = 2 * (la*lb - x - la*y);
			qc = x*x + lb*lb - 2*lb*y + y*y - r*r;
		
			//find the two solutions for x using the quadratic formula
			rx1 = (-qb + Math.sqrt(qb*qb - 4*qa*qc)) / (2*qa);
			rx2 = (-qb - Math.sqrt(qb*qb - 4*qa*qc)) / (2*qa);
		
			//now generate the corresponding y values using y = a*x + b
			ry1 = la*rx1 + lb;
			ry2 = la*rx2 + lb;
			
			//generate the range of the line, and check if the intersect points are within that range
			range = new Range2d(Math.min(l.s.x, l.f.x), Math.max(l.s.x, l.f.x));
			if(!range.inRangeInclusive(rx1))
				rx1 = Double.NaN;
			if(!range.inRangeInclusive(rx2))
				rx2 = Double.NaN;
			
			//generate the result
			return generateResult(rx1, ry1, rx2, ry2, l.s);
		}
	}
	
	private Point2d generateResult(double rx1, double ry1, double rx2, double ry2, Point2d start){
		if(Double.isNaN(rx1) && Double.isNaN(rx2)){
			return null;
		}
		else if(Double.isNaN(rx1)){
			return new Point2d(rx2, ry2);
		}
		else if(Double.isNaN(rx2)){
			return new Point2d(rx1, ry1);
		}
		else{
			Point2d r1 = new Point2d(rx1, ry1);
			Point2d r2 = new Point2d(rx2, ry2);
			if(r1.distanceTo(start) <= r2.distanceTo(start))
				return r1;
			else
				return r2;
		}
	}
	
	
}
