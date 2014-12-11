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
	
	
	public Entity(){}
	
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
	
	/*
	 * Helper for deep copy.
	 * TODO: do I really need this?
	 */
	protected void copyDefaultFields(Entity e){
		shapes = e.shapes.deepCopy();
		
		tags = new HashSet<String>();
		for(String tag : e.tags)
			tags.add(tag); //String is immutable, so this is effectively a deep copy
		
		isActive = e.isActive;
	}
	
	/**
	 * Generates and returns a deep copy of this Entity.
	 * "state" variables need not be copied, but everything else must.
	 * Links should NOT be copied.
	 * @return A 'deep copy' (fields are copied too) of this Entity.
	 */
	public abstract Entity deepCopy();

	/**
	 * Adds a "tag" to this entity.
	 * Entities can see one another's tags when they collide, and can perform resolution behavior based on that information.
	 * @param tag Tag to be added
	 * @return true iff this entity did not already have that tag.
	 */
	public boolean addTag(String tag){
		return tags.add(tag);
	}
	
	/**
	 * Removes a "tag" from this entity (if it had that tag).
	 * Entities can see one another's tags when they collide, and can perform resolution behavior based on that information.
	 * @param tag Tag to be removed
	 * @return true iff a tag was removed.
	 */
	public boolean removeTag(String tag){
		return tags.remove(tag);
	}
	
	/**
	 * Indicates if this entity has the specified tag
	 * Entities can see one another's tags when they collide, and can perform resolution behavior based on that information.
	 * @param tag Tag to check for the presence of.
	 * @return true iff this entity has the given tag.
	 */
	public boolean hasTag(String tag){
		return tags.contains(tag);
	}
	
	/**
	 * Links this entity with another given entity.
	 * What 'linking' means varies by entity pair, most sorts of entities cannot be linked.
	 * An example of a link would be pairing a TogglableEntity with the ButtonEntity that toggles it.
	 * Attempting to link entity pairs that cannot be linked should throw an IllegalArgumentException.
	 * Classes that override this should also override removeLink()
	 * @param e The Entity to link with this.
	 */
	public void addLink(Entity e){
		throw new IllegalArgumentException("These entity types cannot be linked: " +
	                                       this.getClass() + " " + e.getClass());
	}
	
	/**
	 * Removes a link with another given entity.
	 * What 'linking' means varies by entity pair, most sorts of entities cannot be linked.
	 * An example of a link would be pairing a TogglableEntity with the ButtonEntity that toggles it.
	 * If an entity pair cannot be linked, it also cannot be delinked (and should throw an IllegalArgumentException)
	 * Classes that override this should also override addLink()
	 * @param e The Entity to remove a link from.
	 * @return true iff a link was removed, false if there was no link
	 */
	public boolean removeLink(Entity e){
		throw new IllegalArgumentException("These entity types cannot be delinked: " +
                this.getClass() + " " + e.getClass());
	}
	
	public abstract void preStep();
	
	public abstract void moveStep();
	
	public abstract void resolveCollision(Entity e, Vector2d cv);
	
	public abstract void postStep();
	
	/**
	 * Paints the entity with the given graphics context, taking into consideration the given screen constraints.
	 * Entities that need custom graphics should override this method. By default, just draws the entity's shapes.
	 * @param g2d The graphics context to paint with.
	 * @param xoffset Game coordinates x-value of the left edge of the screen.
	 * @param yoffset Game coordinates y-value of the top edge of the screen.
	 * @param scale The scale at which the screen is displaying.
	 * 				This number is the ratio of one game distance unit to one pixel on the display
	 */
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		shapes.paintShapes(g2d, xoffset, yoffset, scale);
	}
	
	/**
	 * Paints (usually invisible) information about the entity to the given graphics context.
	 * This method will not be called during normal playing of the game,
	 * but will be used when this entity is selected in the map editor.
	 * Use it to paint useful information for the editor's user, like the patrol route of a patrolling mob.
	 * By default, this method does nothing.
	 * @param g2d The graphics context to paint with.
	 * @param xoffset Game coordinates x-value of the left edge of the screen.
	 * @param yoffset Game coordinates y-value of the top edge of the screen.
	 * @param scale The scale at which the screen is displaying.
	 * 				This number is the ratio of one game distance unit to one pixel on the display
	 */
	public void paintAdditionalEntityInfo(Graphics2D g2d, double xoffset, double yoffset, double scale){}
}
