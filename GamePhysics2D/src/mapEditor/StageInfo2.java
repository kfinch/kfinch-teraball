package mapEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import misc.BiHashMap;
import gamePhysics2D.Entity;
import gamePvE.GameRunner;
import gamePvE.PlayerEntity;

/**
 * Take Two..
 * This class encapsulates all needed information to build a Teraball stage.
 * It is also serializable, allowing it to be read from or written to a file.
 * 
 * @author Kelton Finch
 */
public class StageInfo2 {

	public PlayerEntity player;
	public BiHashMap<String, Entity> entities; //bimap of entities and their IDs
	public List<EntityLink> entityLinks;
	public File nextStage;
	
	public StageInfo2(PlayerEntity player, BiHashMap<String, Entity> entities, List<EntityLink> entityLinks, File nextStage){
		this.player = player;
		this.entities = entities;
		this.entityLinks = entityLinks;
		this.nextStage = nextStage;
	}
	
	/**
	 * Generates a StageInfo class equivalent to what is loaded in the given GameRunner.
	 * @param game The game instance to read from
	 */
	public StageInfo2(GameRunner game){
		
	}
	
}
