package gamePhysics2D;

import java.util.ArrayList;
import java.util.List;

public class LineSegment2d implements Frame2d {

	public Point2d p1, p2;
	
	public LineSegment2d(Point2d p1, Point2d p2){
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public LineSegment2d(double x1, double y1, double x2, double y2){
		this.p1 = new Point2d(x1, y1);
		this.p2 = new Point2d(x2, y2);
	}
	
	@Override
	public Point2d intersect(Ray2d r) {
		boolean mvertical = (p1.x == p2.x);
		boolean lvertical = (r.s.x == r.f.x);
		double ma, mb, la, lb, rx, ry;
		
		if(mvertical && lvertical){ //both segments are vertical
			if(p1.x != r.s.x) //can't possibly intersect if on different x
				return null; 
			ry = lineIntersectionHelper(r.s.y, r.f.y, p1.y, p2.y);
			if(Double.isNaN(ry))
				return null;
			rx = p1.x;
			return new Point2d(rx, ry);
		}
		else if(mvertical){ //only this segment is vertical
			la = (r.f.y - r.s.y)/(r.f.x - r.s.x);
			rx = p1.x;
			ry = r.s.y + (p1.x-r.s.x)*la;
			if((ry > p1.y && ry > p2.y) || (ry < p1.y && ry < p2.y))
				return null;
			if((rx > r.s.x && rx > r.f.x) || (rx < r.s.x && rx < r.f.x))
				return null;
			return new Point2d(rx, ry);
		}
		else if(lvertical){ //only the other segment is vertical
			la = (p2.y - p1.y)/(p2.x - p1.x);
			rx = r.s.x;
			ry = p1.y + (r.s.x-p1.x)*la;
			if((ry > r.s.y && ry > r.f.y) || (ry < r.s.y && ry < r.f.y))
				return null;
			if((rx > p1.x && rx > p2.x) || (rx < p1.x && rx < p2.x))
				return null;
			return new Point2d(rx, ry);
		}
		
		//neither are vertical..
		
		//convert to form y = a*x + b
		ma = (p2.y - p1.y)/(p2.x - p1.x);
		la = (r.f.y - r.s.y)/(r.f.x - r.s.x);
		mb = p1.y - ma*p1.x;
		lb = r.s.y - la*r.s.x;
		
		if(ma == la){ //parallel
			if(mb != lb) //can't possibly intersect if have different intercepts
				return null;
			rx = lineIntersectionHelper(r.s.x, r.s.y, p1.x, p2.x);
			if(Double.isNaN(rx))
				return null;
			ry = ma*rx + mb;
			return new Point2d(rx, ry);
		}
		else{ //not parallel
			rx = (lb - mb)/(ma - la);
			ry = ma*rx + mb;
			if((rx > r.s.x && rx > r.f.x) || (rx < r.s.x && rx < r.f.x))
				return null;
			if((rx > p1.x && rx > p2.x) || (rx < p1.x && rx < p2.x))
				return null;
			return new Point2d(rx, ry);
		}
	}
	
	/* helper for intersection, when lines are parallel */
	//TODO: clean up this code
	private double lineIntersectionHelper(double r1, double r2, double l1, double l2){	
		double result;
		
		boolean reverse = false;
		if(r1 > r2){
			reverse = true;
			r1 *= -1;
			r2 *= -1;
			l1 *= -1;
			l2 *= -1;
		}
		
		if((r1 > l1 && r1 > l2) || (r2 < l1 && r2 < l2)) //no intersect
			return Double.NaN;
		
		if(((l1 < r1) && (r1 < l2)) || ((l2 < r1) && (r1 < l1)))
			result = r1;
		else if(l1 < l2)
			result = l1;
		else result = l2;
		
		if(result < r1)
			result = r1;
		
		if(reverse)
			result *= -1;
			
		return result;
	}
	
	
	//
	//
	//
	//

	
	/**
	 * Generates a point of intersection between this line segment and a given line segment.
	 * @param l A line segment to check for intersection with this line segment
	 * @return A point of intersection between this line segment and l, or null if they don't intersect.
	 * 		If there are multiple points of intersection, i.e. parallel and same y-intercept,
	 * 		then the intersection point closest to this.p1 will always be the one returned.
	 */
	public Point2d intersection(LineSegment2d l){
		boolean mvertical = (p1.x == p2.x);
		boolean lvertical = (l.p1.x == l.p2.x);
		double ma,la,mb,lb,rx,ry;
		
		if(mvertical && lvertical){ //both segments are vertical
			if(p1.x != l.p1.x) //can't possibly intersect if on different x
				return null; 
			ry = lineIntersectionHelper(p1.y, p2.y, l.p1.y, l.p2.y);
			if(Double.isNaN(ry))
				return null;
			rx = p1.x;
			return new Point2d(rx, ry);
		}
		else if(mvertical){ //only this segment is vertical
			la = (l.p2.y - l.p1.y)/(l.p2.x - l.p1.x);
			rx = p1.x;
			ry = l.p1.y + (p1.x-l.p1.x)*la;
			if((ry > p1.y && ry > p2.y) || (ry < p1.y && ry < p2.y))
				return null;
			if((rx > l.p1.x && rx > l.p2.x) || (rx < l.p1.x && rx < l.p2.x))
				return null;
			return new Point2d(rx, ry);
		}
		else if(lvertical){ //only the other segment is vertical
			la = (p2.y - p1.y)/(p2.x - p1.x);
			rx = l.p1.x;
			ry = p1.y + (l.p1.x-p1.x)*la;
			if((ry > l.p1.y && ry > l.p2.y) || (ry < l.p1.y && ry < l.p2.y))
				return null;
			if((rx > p1.x && rx > p2.x) || (rx < p1.x && rx < p2.x))
				return null;
			return new Point2d(rx, ry);
		}
		
		//convert to form y = a*x + b
		ma = (p2.y - p1.y)/(p2.x - p1.x);
		la = (l.p2.y - l.p1.y)/(l.p2.x - l.p1.x);
		mb = p1.y - ma*p1.x;
		lb = l.p1.y - la*l.p1.x;
		
		if(ma == la){ //parallel
			rx = lineIntersectionHelper(p1.x, p2.x, l.p1.x, l.p2.x);
			if(Double.isNaN(rx))
				return null;
			ry = ma*rx + mb;
			return new Point2d(rx, ry);
		}
		else{ //not parallel
			rx = (lb - mb)/(ma - la);
			ry = ma*rx + mb;
			if((rx > l.p1.x && rx > l.p2.x) || (rx < l.p1.x && rx < l.p2.x))
				return null;
			if((rx > p1.x && rx > p2.x) || (rx < p1.x && rx < p2.x))
				return null;
			return new Point2d(rx, ry);
		}
	}
	
	/**
	 * Generates a point of intersection between this line segment and a given circle.
	 * @param l A circle to check for intersection with this line segment
	 * @return A point of intersection between this line segment and c, or null if they don't intersect.
	 * 		If there are multiple points of intersection,
	 * 		then the intersection point closest to this.p1 will always be the one returned.
	 */
	public Point2d intersection(Circle2d c){
		List<Point2d> results = intersections(c);
		Point2d result = null;
		for(Point2d p : results){
			if(result == null || p1.distanceTo(p) < p1.distanceTo(result)){
				result = p;
			}
		}
		return result;
	}
	
	/**
	 * Generates a point of intersection between this line segment and a given circle.
	 * @param l A circle to check for intersection with this line segment
	 * @return A list of points of intersection between this line segment and c,
	 * 		   or an empty list if they don't intersect.
	 */
	public List<Point2d> intersections(Circle2d c){
		boolean vertical = (p1.x == p2.x);
		double ma, mb, qa, qb, qc; //a and b for line equation, a, b, and c for quadratic formula
		double rx1, rx2, ry1, ry2; //candidate result values (at most 2 intersections between line and circle)
		double min, max; //the x range of the line (or the y range if it's vertical)
		ArrayList<Point2d> result = new ArrayList<Point2d>(2); //list to be returned
		
		if(vertical){ //can't generate y = a*x + b for vertical line
			//given line as x = p1.x and circle as (x - xoffset)^2 + (y - yoffset)^2 = radius^2,
			//generate quadratic formula values qa, qb, and qc in order to solve for y
			qa = 1;
			qb = -2*c.y;
			qc = p1.x*p1.x + c.x*c.x - 2*p1.x*c.x + c.y*c.y - c.r*c.r;
			
			//find the two solutions for y using the quadratic formula
			ry1 = (-qb + Math.sqrt(qb*qb - 4*qa*qc)) / (2*qa);
			ry2 = (-qb - Math.sqrt(qb*qb - 4*qa*qc)) / (2*qa);
			
			//now generate the corresponding x values (which are just p1.x)
			rx1 = p1.x;
			rx2 = p1.x;
			
			//generate the range of the line
			min = Math.min(p1.y, p2.y);
			max = Math.max(p1.y, p2.y);
			
			//generate the results
			if(!Double.isNaN(ry1) && ry1 >= min && ry1 <= max)
				result.add(new Point2d(rx1, ry1));
			
			if(!Double.isNaN(ry2) && ry2 >= min && ry2 <= max)
				result.add(new Point2d(rx2, ry2));
		}
		
		else{
			//convert to form y = a*x + b
			ma = (p2.y - p1.y)/(p2.x - p1.x);
			mb = p1.y - ma*p1.x;
		
			//given line as y = a*x + b and circle as (x - xoffset)^2 + (y - yoffset)^2 = radius^2,
			//generate quadratic formula values qa, qb, and qc in order to solve for x
			qa = ma*ma + 1;
			qb = 2 * (ma*mb - c.x - ma*c.y);
			qc = c.x*c.x + mb*mb - 2*mb*c.y + c.y*c.y - c.r*c.r;
		
			//find the two solutions for x using the quadratic formula
			rx1 = (-qb + Math.sqrt(qb*qb - 4*qa*qc)) / (2*qa);
			rx2 = (-qb - Math.sqrt(qb*qb - 4*qa*qc)) / (2*qa);
		
			//now generate the corresponding y values using y = a*x + b
			ry1 = ma*rx1 + mb;
			ry2 = ma*rx2 + mb;
			
			//generate the range of the line
			min = Math.min(p1.x, p2.x);
			max = Math.max(p1.x, p2.x);
			
			//generate the results
			if(!Double.isNaN(rx1) && rx1 >= min && rx1 <= max)
				result.add(new Point2d(rx1, ry1));
			
			if(!Double.isNaN(rx2) && rx2 >= min && rx2 <= max)
				result.add(new Point2d(rx2, ry2));
		}
	
		return result;
	}

	
}
