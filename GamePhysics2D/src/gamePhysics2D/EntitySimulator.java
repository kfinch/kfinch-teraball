package gamePhysics2D;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A class to manage and handle the collisions of all the entities in a game environment.
 * Entities are divided into categories to allow level designer to only check for collision
 * between entities that can actually collide.
 * 
 * I probably designed this class to be a bit too 'generalized', and perhaps it should have been
 * merged with the GameSimulator class. For now, it serves its function acceptably.
 * 
 * @author Kelton Finch
 */
public class EntitySimulator implements Serializable {

	private static final long serialVersionUID = -7784533886082574195L;
	
	private List<List<Entity>> entityLists; //list of lists of entities
	private List<Set<Integer>> entityInteractions; //list of sets of category interactions
	private List<String> entityListNames; //list of categories
	
	public EntitySimulator(){
		entityLists = new ArrayList<List<Entity>>();
		entityInteractions = new ArrayList<Set<Integer>>();
		entityListNames = new ArrayList<String>();
	}
	
	/**
	 * Add an entity category to the simulator.
	 * @param categoryName Name of the category to be added
	 */
	public void addCategory(String categoryName){
		if(entityListNames.contains(categoryName)){
			System.err.println("That category already exists!");
			System.exit(1);
		}
		
		entityLists.add(new LinkedList<Entity>());
		entityInteractions.add(new HashSet<Integer>());
		entityListNames.add(categoryName);
	}
	
	/**
	 * Add a (two way) interaction between two categories.
	 * @param c1 Name of the one category
	 * @param c2 Name of the other category
	 */
	public void newInteraction(String c1, String c2){
		int i1 = entityListNames.indexOf(c1);
		int i2 = entityListNames.indexOf(c2);
		
		if(i1 == -1){
			System.err.println("No such category: " + c1);
			System.exit(1);
		}
		if(i2 == -1){
			System.err.println("No such category: " + c2);
			System.exit(1);
		}
		
		//'interactions' are bidirectional, and are stored only in higher indices pointing to lower indices
		if(i1 > i2){
			entityInteractions.get(i1).add(i2);
		}
		else{
			entityInteractions.get(i2).add(i1);
		}
	}
	
	/**
	 * Register an entity with the simulator.
	 * @param e Entity to be added
	 * @param categoryName Category the entity is to be added to.
	 */
	public void addEntity(Entity e, String categoryName){
		int i = entityListNames.indexOf(categoryName);
		
		if(i == -1)
			throw new IllegalArgumentException("unrecognized category name: " + categoryName);
		
		entityLists.get(i).add(e);
	}
	
	/**
	 * Advance the simulation by one time step.
	 * Simulator removes each entity with set deletion flag,
	 * then runs each entity's preStep, then each moveStep,
	 * then resolves all relevant collisions, and finally runs each postStep.
	 */
	public void step(){
		Vector2d cv;
		Iterator<Entity> iter1, iter2;
		Entity e1, e2;
		
		//remove entities marked for deletion
		for(List<Entity> le : entityLists){
			iter1 = le.iterator();
			while(iter1.hasNext()){
				e1 = iter1.next();
				if(!e1.isActive) //if no longer active, remove from list
					iter1.remove();
			}
		}
		
		//pre step
		for(List<Entity> le : entityLists){
			for(Entity e : le){
				e.preStep();
			}
		}
		
		//move step
		for(List<Entity> le : entityLists){
			for(Entity e : le){
				e.moveStep();
			}
		}
		
		//collide
		for(int i=0; i<entityLists.size(); i++){ //for each category...
			for(int j : entityInteractions.get(i)){ //for each category that category interacts with...
				iter1 = entityLists.get(i).iterator();
				while(iter1.hasNext()){ //for each entity in the first category...
					e1 = iter1.next();
					iter2 = entityLists.get(j).iterator();
					while(iter2.hasNext()){ //for each entity in the second category...
						e2 = iter2.next();
						
						//check for collision if they aren't the same entity
						if(e1 == e2)
							continue;
						
						cv = e1.shapes.resolveCollision(e2.shapes);
						if(cv != null){
							e1.resolveCollision(e2, cv);
							cv.multiply(-1);
							e2.resolveCollision(e1, cv);
						}
					}
				}
			}
		}
		
		//post step
		for(List<Entity> le : entityLists){
			for(Entity e : le){
				e.postStep();
			}
		}
	}
	
	
	public List<Entity> getCategory(String category){
		int i = entityListNames.indexOf(category);
		return entityLists.get(i);
	}
	
	/*
	 * Checks the given entity for collision with all entities in the simulator.
	 * Useful for 'one shot' collision detection, like line of sight, lasers, explosions, etc.
	 */
	public void resolveIndividualCollision(Entity e){
		for(List<Entity> el : entityLists)
			resolveIndividualCollisionInCategory(e, el);
	}
	
	/*
	 * Checks the given entity for collision with all entities in category.
	 * Useful for 'one shot' collision detection, like line of sight, lasers, explosions, etc.
	 */
	public void resolveIndividualCollision(Entity e, String category){
		int i = entityListNames.indexOf(category);
		resolveIndividualCollisionInCategory(e, entityLists.get(i));
	}
	
	private void resolveIndividualCollisionInCategory(Entity e, List<Entity> el){
		Vector2d cv;
		for(Entity f : el){
			if(e == f)
				continue;
			
			cv = e.shapes.resolveCollision(f.shapes);
			if(cv != null){
				e.resolveCollision(f, cv);
				cv.multiply(-1);
				f.resolveCollision(e, cv);
			}
		}
	}
	
	/*
	 * Returns a list of entities that collide with the given shape group.
	 * This is used in the editor for selecting entities, and can 
	 */
	public List<Entity> detectIndividualCollision(ShapeGroup shapes){
		LinkedList<Entity> result = new LinkedList<Entity>();
		Vector2d cv;
		
		for(List<Entity> el : entityLists){
			for(Entity e : el){
				cv = shapes.resolveCollision(e.shapes);
				if(cv != null)
					result.add(e);
			}
		}
		
		return result;
	}
	
	/**
	 * Clears all entities from the simulator. Categories and interactions remain.
	 */	
	public void clear(){
		for(List<Entity> list : entityLists){
			list.clear();
		}
	}
	
	/**
	 * Paints all contained entities.
	 * @param g2d Graphics context to be painted to.
	 * @param xoffset x coordinate of top left of screen
	 * @param yoffset y coordinate of top left of screen
	 * @param scale scaling factor of paint
	 */
	public void paintEntities(Graphics2D g2d, double xoffset, double yoffset, double scale){
		for(List<Entity> le : entityLists){
			for(Entity e : le){
				e.paintEntity(g2d, xoffset, yoffset, scale);
			}
		}
	}

}
