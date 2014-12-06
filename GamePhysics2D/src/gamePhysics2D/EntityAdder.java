package gamePhysics2D;

/**
 * A wrapper class for EntitySimulator, to be handed to entities to allow them
 * to spawn other entities without giving them the entire simulator.
 * 
 * @author Kelton Finch
 */
public class EntityAdder {

	private EntitySimulator sim;
	
	public EntityAdder(EntitySimulator sim){
		this.sim = sim;
	}
	
	public void addEntity(Entity e, String categoryName){
		sim.addEntity(e, categoryName);
	}
}
