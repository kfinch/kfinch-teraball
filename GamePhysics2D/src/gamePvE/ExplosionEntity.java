package gamePvE;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gamePhysics2D.BoundingCircle;
import gamePhysics2D.BoundingPoint;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

/*
 * This is really just a graphical effect implemented as an entity.
 */
public class ExplosionEntity extends Entity {

	List<ExplosionPoint> explosions;
	
	public ExplosionEntity(ShapeGroup shapes, String tag, List<ExplosionPoint> explosions){
		super(shapes, tag);
		this.explosions = explosions;
	}
	
	public ExplosionEntity(double xLoc, double yLoc, List<ExplosionPoint> explosions){
		this(new ShapeGroup(new BoundingPoint(xLoc, yLoc)), "explosion", explosions);
	}
	
	public ExplosionEntity(double xLoc, double yLoc, double size, String kind){
		this(xLoc, yLoc, parseExplosionKind(kind, size));
	}
	
	private static List<ExplosionPoint> parseExplosionKind(String kind, double size){
		List<ExplosionPoint> result;
		if(kind.equals("basic")){
			result = new ArrayList<ExplosionPoint>(1);
			result.add(new ExplosionPoint(0, 0, size/4, size/20,
					                      GameRunner.ENEMY_SHOT_COLOR, 15));
			return result;
		}
		else{
			throw new IllegalArgumentException("unrecognized explosion kind: " + kind);
		}
	}

	@Override
	public void preStep() {
		Iterator<ExplosionPoint> iter = explosions.iterator();
		while(iter.hasNext()){
			ExplosionPoint ep = iter.next();
			ep.remainingDuration--;
			ep.size += ep.sizeChange;
			if(ep.remainingDuration <= 0)
				iter.remove();
		}
		if(explosions.isEmpty())
			isActive = false;
	}

	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {}

	@Override
	public void postStep() {}
	
	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		//doesn't paint its actual shapes, only the explosion, so no call to super()
		float alpha;
		ShapeGroup sg;
		for(ExplosionPoint ep : explosions){
			g2d.setColor(ep.color);
			alpha = ((float)ep.remainingDuration) / ((float)ep.totalDuration);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			sg = new ShapeGroup(new BoundingCircle(ep.xLoc + shapes.xLoc, ep.yLoc + shapes.yLoc, ep.size));
			sg.paintShapes(g2d, xoffset, yoffset, scale);
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
}

class ExplosionPoint {
	protected double xLoc;
	protected double yLoc;
	protected double size;
	protected double sizeChange;
	
	protected Color color;
	
	protected int totalDuration;
	protected int remainingDuration;
	
	public ExplosionPoint(double xLoc, double yLoc, double size, double sizeChange, Color color, int totalDuration){
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		
		this.size = size;
		this.sizeChange = sizeChange;
		
		this.color = color;
		
		this.totalDuration = totalDuration;
		remainingDuration = totalDuration;
	}
}
