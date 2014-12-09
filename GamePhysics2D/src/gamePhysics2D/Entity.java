package gamePhysics2D;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An entity in the game's simulator.
 * Has a size, shape, and location encapsulated by a list of BoundingShapes,
 * an identifying string, and can flag itself for deletion by setting isActive to false.
 * 
 * Steps in order: Remove, Pre-step, Move-step, Collision, Post-step
 * 
 * @author Kelton Finch
 */
public abstract class Entity implements Serializable {
	
	private static final long serialVersionUID = 2051214311013789538L;
	
	public ShapeGroup shapes;
	public Set<String> tags;
	public boolean isActive;
	
	
	public Entity(ShapeGroup shapes, Set<String> tags){
		this.shapes = shapes;
		this.tags = tags;
		isActive = true;
	}
	
	public Entity(ShapeGroup shapes, String tag){
		this.shapes = shapes;
		tags = new HashSet<String>();
		tags.add(tag);
		isActive = true;
	}

	public void addTag(String tag){
		tags.add(tag);
	}
	
	public void removeTag(String tag){
		tags.remove(tag);
	}
	
	public boolean hasTag(String tag){
		return tags.contains(tag);
	}
	
	public void link(Entity e){
		throw new IllegalArgumentException("These entity types cannot be linked: " +
	                                       this.getClass() + " " + e.getClass());
	}
	
	public abstract void preStep();
	
	public abstract void moveStep();
	
	public abstract void resolveCollision(Entity e, Vector2d cv);
	
	public abstract void postStep();
	
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		shapes.paintShapes(g2d, xoffset, yoffset, scale);
	}
}
