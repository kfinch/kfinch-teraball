package gamePhysics2D;

import java.io.Serializable;

/**
 * A wrapper class for EntitySimulator, to be handed to entities to allow them
 * to spawn other entities without giving them the entire simulator.
 * 
 * @author Kelton Finch
 */
public class EntityAdder implements Serializable {

	private static final long serialVersionUID = -6880746555645725653L;
	
	private EntitySimulator sim;
	
	public EntityAdder(EntitySimulator sim){
		this.sim = sim;
	}
	
	public void addEntity(Entity e, String categoryName){
		sim.addEntity(e, categoryName);
	}
}
