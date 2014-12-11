package gamePvE;

import java.awt.Color;
import java.awt.Graphics2D;

import gamePhysics2D.Entity;
import gamePhysics2D.Point2d;
import gamePhysics2D.Ray2d;
import gamePhysics2D.Vector2d;

public class LineOfSightEntity2 extends Entity {

	private static final long serialVersionUID = 2178526228675650236L;
	
	private Ray2d ray;
	private Color color;
	
	public LineOfSightEntity2(Point2d start, Point2d finish, Color color){
		super(null, "los");
		ray = new Ray2d(start, finish);
		this.color = color;
	}
	
	public LineOfSightEntity2(Point2d src, Point2d dst) {
		this(src, dst, Color.red);
	}
	
	@Override
	public Entity deepCopy(){
		throw new UnsupportedOperationException("Can't deep copy LOS2 Entity");
	}
	
	public void setStart(Point2d start){
		ray.s = start;
	}
	
	public void setFinish(Point2d finish){
		ray.f = finish;
	}

	@Override
	public void preStep() {}
	
	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {}
	
	@Override
	public void postStep() {}
	
	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		g2d.setColor(color);
		g2d.drawLine((int)(ray.s.x-xoffset), (int)(ray.s.y-yoffset), (int)(ray.f.x-xoffset), (int)(ray.f.y-yoffset));
	}
	
}
