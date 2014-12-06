package tests;

import java.util.ArrayList;

import gamePhysics2D.BoundingAABox;
import gamePhysics2D.BoundingCircle;
import gamePhysics2D.BoundingRotatingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.Circle2d;
import gamePhysics2D.LineSegment2d;
import gamePhysics2D.Point2d;
import gamePhysics2D.Ray2d;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class CaseTester {

	public static void main(String args[]){
		rotatingPolygonTest();
	}
	
	private static void test1(){
		BoundingCircle circle = new BoundingCircle(3,3,2);
		BoundingAABox box = new BoundingAABox(1.5,1.5,0.5,0.5);
		Vector2d resolutionVector = box.resolveCollision(circle);
		if(resolutionVector == null)
			System.out.println("no collide");
		else
			System.out.println(resolutionVector.x + " " + resolutionVector.y);
	}
	
	private static void test2(){
		BoundingCircle circle = new BoundingCircle(4.8,1.1,1);
		BoundingAABox box = new BoundingAABox(3,3,1,1);
		Vector2d resolutionVector = circle.resolveCollision(box);
		if(resolutionVector == null)
			System.out.println("no collide");
		else
			System.out.println(resolutionVector.x + " " + resolutionVector.y);
	}
	
	private static void intersectionTest(){
		Ray2d r = new Ray2d(1,3,1,5);
		LineSegment2d l = new LineSegment2d(1,1,1,3);
		Point2d p = r.intersection(l);
		
		System.out.println("(" + p.x + "," + p.y + ")");
	}
	
	private static void deltaAngleTest(){
		double s = 0;
		double f = 0;
		System.out.println(s + " " + f + " " + Vector2d.deltaAngle(s, f));
		
		s = Math.PI/4;
		f = 0;
		System.out.println(s + " " + f + " " + Vector2d.deltaAngle(s, f));
		
		s = -Math.PI/4;
		f = Math.PI/4;
		System.out.println(s + " " + f + " " + Vector2d.deltaAngle(s, f));
		
		s = Math.PI*3/4;
		f = -Math.PI*3/4;
		System.out.println(s + " " + f + " " + Vector2d.deltaAngle(s, f));
		
		s = -Math.PI*3/4;
		f = Math.PI*3/4;
		System.out.println(s + " " + f + " " + Vector2d.deltaAngle(s, f));
	}
	
	private static void rotatingPolygonTest(){
		int nPoints = 3;
		double xPoints[] = {3, 0 ,-3};
		double yPoints[] = {10, 20, 10};
		
		BoundingRotatingPolygon brp = new BoundingRotatingPolygon(0,0,nPoints,xPoints,yPoints);
		
		brp.rotate(0.1);
		
		brp.setAngle(0);
		
		brp.setAngle(Math.PI);
	}
}
