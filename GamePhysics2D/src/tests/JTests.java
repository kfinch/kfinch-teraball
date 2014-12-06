package tests;

import static org.junit.Assert.*;
import gamePhysics2D.Frame2d;
import gamePhysics2D.LineSegment2d;
import gamePhysics2D.Point2d;
import gamePhysics2D.Ray2d;

import org.junit.Test;

public class JTests {
	
	@Test
	public void rayIntersectTest(){
		rayIntersectTester("basic intersect 1", new Ray2d(1,1,4,4), new LineSegment2d(2,4,3,1), new Point2d(2.5,2.5));
		rayIntersectTester("basic intersect 2", new Ray2d(4,4,1,1), new LineSegment2d(2,4,3,1), new Point2d(2.5,2.5));
		rayIntersectTester("basic intersect 3", new Ray2d(2,4,3,1), new LineSegment2d(1,1,4,4), new Point2d(2.5,2.5));
		rayIntersectTester("basic intersect 4", new Ray2d(1,1,3,3), new LineSegment2d(1,3,3,1), new Point2d(2,2));
		
		rayIntersectTester("rvert intersect",   new Ray2d(2,1,2,4), new LineSegment2d(1,1,3,2), new Point2d(2,1.5));
		rayIntersectTester("rvert no reach",    new Ray2d(5,1,5,4), new LineSegment2d(1,1,3,2), null);
		
		rayIntersectTester("lvert intersect",   new Ray2d(1,1,3,2), new LineSegment2d(2,1,2,4), new Point2d(2,1.5));
		rayIntersectTester("lvert no reach",    new Ray2d(1,1,3,2), new LineSegment2d(5,1,5,4), null);
		
		rayIntersectTester("bvert intersect 1", new Ray2d(1,1,1,3), new LineSegment2d(1,3,1,5), new Point2d(1,3));
		rayIntersectTester("bvert intersect 2", new Ray2d(1,1,1,4), new LineSegment2d(1,3,1,5), new Point2d(1,3));
		rayIntersectTester("bvert intersect 3", new Ray2d(1,4,1,1), new LineSegment2d(1,3,1,5), new Point2d(1,4));
		
		rayIntersectTester("bvert no reach",    new Ray2d(1,1,1,2), new LineSegment2d(1,3,1,5), null);
		
		
		rayIntersectTester("ray no reach 1",    new Ray2d(1,1,2,2), new LineSegment2d(1,4,4,1), null);
		rayIntersectTester("ray no reach 2",    new Ray2d(1,1,2,2), new LineSegment2d(3,2,4,1), null);
		rayIntersectTester("vertical diff x",   new Ray2d(1,1,1,3), new LineSegment2d(2,1,2,3), null);
		rayIntersectTester("horizontal diff y", new Ray2d(3,1,5,1), new LineSegment2d(3,3,5,3), null);
		rayIntersectTester("parallel",          new Ray2d(2,1,1,2), new LineSegment2d(3,1,1,3), null);
	}
	
	private void rayIntersectTester(String tag, Ray2d r, Frame2d f, Point2d p){
		assertEquals(tag, p, r.intersection(f));
	}

}
