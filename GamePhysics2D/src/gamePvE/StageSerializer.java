package gamePvE;

import gamePhysics2D.Entity;
import gamePhysics2D.EntitySimulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import mapEditor.EntityLink;
import mapEditor.StageInfo;
import mapEditor.StageInfo2;
import misc.BiHashMap;

/**
 * A second attempt at a class to read/write stage files.
 * This scheme uses a serializable data structure that holds all the info about a stage,
 * and the inbuilt Java serialization methods.
 * 
 * @author Kelton Finch
 */
public class StageSerializer {

	/*
	 * This method is needed to 'bootstrap' the new stage storage info from the old.
	 * A StageInfo2 (the new way) is generated from a StageInfo (the old way) and a GameRunner.
	 * Obviously, the StageInfo and the GameRunner need to be representing the same stage in order for this to work right.
	 */
	public static StageInfo2 generateNewStageInfoFromOld(StageInfo oldInfo, GameRunner game){
		EntitySimulator sim = game.getSim();
		BiHashMap<String, Entity> entityIDs = oldInfo.linkableEntityIDs;
		List<EntityLink> entityLinks = oldInfo.entityLinks;
		File nextStage = oldInfo.nextStage;
		return new StageInfo2(sim, entityIDs, entityLinks, nextStage);
	}
	
	/**
	 * Deserializes a StageInfo from the given file.
	 * @param stageFile File to read stage from.
	 * @return A StageInfo read from the given file.
	 */
	public static StageInfo2 deserializeStage(File stageFile){
		try {
			FileInputStream fileIn = new FileInputStream(stageFile);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			StageInfo2 result = (StageInfo2) in.readObject();
			in.close();
			fileIn.close();
			return result;
		} catch (FileNotFoundException e) {
			System.out.println("File " + stageFile + " was not found: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("There was an I/O problem while deserializing: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Class file not found: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Serializes the given StageInfo to the given File.
	 * @param stage StageInfo to be serialized.
	 * @param stageFile File to write the stage to.
	 */
	public static void serializeStage(StageInfo2 stage, File stageFile){
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(stageFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(stage);
	        out.close();
	        fileOut.close();
		} catch (FileNotFoundException e) {
			System.out.println("File " + stageFile + " was not found: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("There was an I/O problem while serializing: " + e.getMessage());
		}
	}
}
