package gamePvE;

import java.awt.Graphics2D;
import gamePhysics2D.BoundingLineSegment;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

/*
 * A kinda hacky way of implementing line of sight checking.
 */
public class LineOfSightEntity extends Entity{

	public Entity src, dst;
	private BoundingLineSegment losLine;
	
	public boolean hasLOS;
	
	public LineOfSightEntity(Entity src, Entity dst) {
		super(null, "los");
		this.src = src;
		this.dst = dst;
		this.losLine = new BoundingLineSegment(src.shapes.xLoc, src.shapes.yLoc,
                                               dst.shapes.xLoc, dst.shapes.yLoc);
		shapes = new ShapeGroup(losLine);
		hasLOS = true;
	}
	
	public double angle(){
		return Math.atan2(losLine.y2-losLine.y1, losLine.x2-losLine.x1);
	}

	@Override
	public void preStep() {}
	
	@Override
	public void moveStep() {
		losLine.setDimensions(src.shapes.xLoc, src.shapes.yLoc, dst.shapes.xLoc, dst.shapes.yLoc);
		shapes.updateDimensions();
		hasLOS = true;
	}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {
		if(e != src && e != dst && (e.hasTag("solid") || e.hasTag("solidimmovable"))){
			hasLOS = false;
		}
	}
	
	@Override
	public void postStep() {}
	
	//line of sight objects should not be drawn
	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){}
}
