package mapEditor;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import misc.BiHashMap;
import gamePhysics2D.Entity;
import gamePhysics2D.EntitySimulator;
import gamePvE.GameRunner;

/**
 * Take Two..
 * This class encapsulates all needed information to build a Teraball stage.
 * It is also serializable, allowing it to be read from or written to a file.
 * 
 * @author Kelton Finch
 */
public class StageInfo2 implements Serializable {

	private static final long serialVersionUID = 4312324603350537471L;
	
	public EntitySimulator sim;
	public BiHashMap<String, Entity> entityIDs; //bimap of linkable entities and their IDs
	public List<EntityLink> entityLinks;
	public File nextStage;
	
	public StageInfo2(EntitySimulator sim, BiHashMap<String, Entity> entities, List<EntityLink> entityLinks, File nextStage){
		this.sim = sim;
		this.entityIDs = entities;
		this.entityLinks = entityLinks;
		this.nextStage = nextStage;
	}
	
	/**
	 * Loads the stage represented by this object into the given GameRunner.
	 * @param game the GameRunner instance on which to load this stage.
	 */
	public void loadStage(GameRunner game){
		game.setSim(sim);
		Entity e1, e2;
		for(EntityLink link : entityLinks){
			e1 = entityIDs.getB(link.e1);
			e2 = entityIDs.getB(link.e2);
			e1.link(e2);
		}
		game.setNextStage(nextStage);
	}
	
}
