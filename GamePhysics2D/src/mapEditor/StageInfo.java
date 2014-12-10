package mapEditor;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import misc.BiHashMap;
import gamePhysics2D.Entity;
import gamePhysics2D.EntitySimulator;
import gamePvE.GameRunner;
import gamePvE.PlayerEntity;

/**
 * This class encapsulates all needed information to build a Teraball stage.
 * It is also serializable, allowing it to be read from or written to a file.
 * 
 * Entity links are actually preserved in the EntitySimulator, but this structure also keeps it
 * in a more accessible format for use by the map editor.
 * 
 * @author Kelton Finch
 */
public class StageInfo implements Serializable {

	private static final long serialVersionUID = 4312324603350537471L;
	
	public EntitySimulator sim;
	public PlayerEntity player;
	public BiHashMap<String, Entity> entityIDs; //bimap of linkable entities and their IDs
	public List<EntityLink> entityLinks;
	public File nextStage;
	
	public StageInfo(EntitySimulator sim, PlayerEntity player, BiHashMap<String, Entity> entities,
			          List<EntityLink> entityLinks, File nextStage){
		this.sim = sim;
		this.player = player;
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
		game.playerEntity = player;
		Entity e1, e2;
		game.setNextStage(nextStage);
	}
	
}
