package gamePvE;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import mapEditor.MapEditorRunner;
import mapEditor.StageInfo;
import gamePhysics2D.BoundingAABox;
import gamePhysics2D.BoundingCircle;
import gamePhysics2D.BoundingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.Entity;
import gamePhysics2D.EntitySimulator;
import gamePhysics2D.ShapeGroup;

/**
 * This class provides static functions to encode and decode map files.
 * Encoding turns a project in the map editor into a valid map file.
 * Decoding loads a valid map file such that it can be played.
 * 
 * Currently, 'entity categories' are hardcoded in.
 * This may be changed in the future, if I decide it makes sense to do so.
 * 
 * @author Kelton Finch
 */
public class MapCoder {

	public static StageInfo decodeMapFile(File fileName, GameRunner game) throws FileNotFoundException{
		Scanner sc = new Scanner(fileName);
		StageInfo stage = new StageInfo();
		while(sc.hasNext()){
			String category = sc.next();
			if(category.equals("CONSTANTS"))
				decodeConstants(sc, stage, game);
			else if(category.equals("MAP_SIZE"))
				decodeMapSize(sc, stage, game);
			else if(category.equals("PLAYER_START"))
				decodePlayerStart(sc, stage, game);
			else if(category.equals("ENTITIES"))
				decodeEntities(sc, stage, game);
			else if(category.equals("NEXT_STAGE"))
				decodeNextStage(sc, stage, game);
			else
				throw new IllegalArgumentException("Unrecognized category: " + category);
		}
		return stage;
	}
	
	private static void decodeConstants(Scanner sc, StageInfo stage, GameRunner game){
		//Constants currently hardcoded in GameSimulator, so this does nothing for now.
	}
	
	private static void decodeMapSize(Scanner sc, StageInfo stage, GameRunner game){
		double edgeThickness = GameRunner.STAGE_WALL_THICKNESS/2;
		double stageWidth = sc.nextDouble();
		double stageHeight = sc.nextDouble();
		
		stage.stageWidth = stageWidth;
		stage.stageHeight = stageHeight;
		
		BoundingAABox edgeBB = new BoundingAABox(-edgeThickness, stageHeight/2, edgeThickness, stageHeight+edgeThickness);
		Entity edge = new TerrainEntity(new ShapeGroup(edgeBB));
		edge.addTag("edge");
		game.getSim().addEntity(edge, "terrain");
		
		edgeBB = new BoundingAABox(stageWidth+edgeThickness, stageHeight/2, edgeThickness, stageHeight+edgeThickness);
		edge = new TerrainEntity(new ShapeGroup(edgeBB));
		edge.addTag("edge");
		game.getSim().addEntity(edge, "terrain");
		
		edgeBB = new BoundingAABox(stageWidth/2, -edgeThickness, stageWidth+edgeThickness, edgeThickness);
		edge = new TerrainEntity(new ShapeGroup(edgeBB));
		edge.addTag("edge");
		game.getSim().addEntity(edge, "terrain");
		
		edgeBB = new BoundingAABox(stageWidth/2, stageHeight+edgeThickness, stageWidth+edgeThickness, edgeThickness);
		edge = new TerrainEntity(new ShapeGroup(edgeBB));
		edge.addTag("edge");
		game.getSim().addEntity(edge, "terrain");
		
		sc.next(); //skips the "END"
	}
	
	private static void decodePlayerStart(Scanner sc, StageInfo stage, GameRunner game){
		game.playerEntity = new PlayerEntity(sc.nextDouble(), sc.nextDouble(), game.getSim());
		game.getSim().addEntity(game.playerEntity, "player");
		stage.player = game.playerEntity;
		
		sc.next(); //skips the "END"
	}
	
	private static void decodeNextStage(Scanner sc, StageInfo stage, GameRunner game){
		String nextStageFileName = sc.next();
		if(nextStageFileName.equals("NONE"))
			game.nextStage = null;
		else
			game.nextStage = new File(nextStageFileName);
		
		stage.nextStage = game.nextStage;
		
		sc.next(); //skips the "END"
	}
	
	/*
	 * Decodes a list of entities
	 * 
	 * <any number of entities, format as follows:>
	 * 
	 * terrain <shape>
	 * mine [xloc] [yloc]
	 * patrollingmine [nwaypoints] [x1] [y1] [x2] [y2] ...
	 * gunturret [xloc] [yloc] [facing]
	 * 		(facing: expressed as fraction of Pi)
	 * missileturret [xloc] [yloc]
	 * coin [xloc] [yloc]
	 * bigcoin [xloc] [yloc]
	 * exit [xloc] [yloc]
	 * keyedexit [ID] [xloc] [yloc] [numkeys]
	 * exitkey [ID] [xloc] [yloc] <color>
	 * 		(color: violet / indigo / blue / green)
	 * gate [ID] <mode> <invert?> <shape>
	 * 		(mode: and / or / xor)
	 * 		(invert?: true / false)
	 * wire [ID] <mode> <invert?> <shape>
	 * 	    (mode: and / or / xor)
	 * 		(invert?: true / false)
	 * button [ID] [xloc] [yloc] <mode>
	 * 		(mode: toggle / hold / onepress)
	 * link [entity1's ID] [entity2's ID]
	 * fuelpickup [xloc] [yloc]
	 */
	private static void decodeEntities(Scanner sc, StageInfo stage, GameRunner game){
		Map<String, Entity> linkableEntities = new HashMap<String, Entity>();
		Map<Entity, String> entityCodeMap = new HashMap<Entity, String>();
		
		String entityString;
		do{
			do{
				entityString = sc.nextLine();
			}while(entityString.equals(""));
			entityCodeMap.put(decodeEntity(entityString, game, linkableEntities), entityString);
		}while(!entityString.startsWith("END"));
		
		stage.entityCodeMap = entityCodeMap;
	}
	
	private static Entity decodeEntity(String line, GameRunner game, Map<String,Entity> linkableEntities){
		Scanner sc = new Scanner(line);
		String type = sc.next();
		if(type.equals("END")){
			return null;
		}
		else if(type.equals("terrain")){
			Entity e = new TerrainEntity(decodeShapeGroup(sc));
			game.getSim().addEntity(e, "terrain");
			return e;
		}
		else if(type.equals("mine")){
			Entity e = new MineEntity(sc.nextDouble(), sc.nextDouble());
			game.getSim().addEntity(e, "terrain");
			return e;
		}
		else if(type.equals("patrollingmine")){
			int nWaypoints = sc.nextInt();
			double xWaypoints[] = new double[nWaypoints];
			double yWaypoints[] = new double[nWaypoints];
			for(int i=0; i<nWaypoints; i++){
				xWaypoints[i] = sc.nextDouble();
				yWaypoints[i] = sc.nextDouble();
			}
			Entity e = new PatrollingMineEntity(nWaypoints, xWaypoints, yWaypoints,
	             	                            GameRunner.MINE_PATROL_SPEED*sc.nextDouble());
			game.getSim().addEntity(e, "terrain");
			return e;
		}
		else if(type.equals("gunturret")){
			GunTurretEntity gun = new GunTurretEntity(sc.nextDouble(), sc.nextDouble(),
					                                  sc.nextDouble()*Math.PI, game.getAdder());
			LineOfSightEntity gunlos = new LineOfSightEntity(gun, game.playerEntity);
			gun.setLOS(gunlos);
			game.getSim().addEntity(gun, "terrain");
			game.getSim().addEntity(gunlos, "los");
			return gun;
		}
		else if(type.equals("missileturret")){
			MissileTurretEntity missile = new MissileTurretEntity(sc.nextDouble(), sc.nextDouble(), game.getAdder());
			LineOfSightEntity missilelos = new LineOfSightEntity(missile, game.playerEntity);
			missile.setLOS(missilelos);
			game.getSim().addEntity(missile, "terrain");
			game.getSim().addEntity(missilelos, "los");
			return missile;
		}
		else if(type.equals("coin")){
			Entity e = new CoinEntity(sc.nextDouble(), sc.nextDouble(),
					                  GameRunner.REG_COIN_SIZE, GameRunner.REG_COIN_VALUE);
			game.getSim().addEntity(e, "playeritems");
			return e;
		}
		else if(type.equals("bigcoin")){
			Entity e = new CoinEntity(sc.nextDouble(), sc.nextDouble(),
                                      GameRunner.BIG_COIN_SIZE, GameRunner.BIG_COIN_VALUE);
			game.getSim().addEntity(e, "playeritems");
			return e;
		}
		else if(type.equals("customcoin")){
			Entity e = new CoinEntity(sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.nextInt());
			game.getSim().addEntity(e, "playeritems");
			return e;
		}
		else if(type.equals("exit")){
			Entity e = new LevelExitEntity(sc.nextDouble(), sc.nextDouble());
			game.getSim().addEntity(e, "playeritems");
			return e;
		}
		else if(type.equals("keyedexit")){
			String id = sc.next();
			double xLoc = sc.nextDouble();
			double yLoc = sc.nextDouble();
			int numKeys = sc.nextInt();
			KeyedLevelExitEntity keyedExit = new KeyedLevelExitEntity(xLoc, yLoc, numKeys);
			linkableEntities.put(id, keyedExit);
			game.getSim().addEntity(keyedExit, "playeritems");
			return keyedExit;
		}
		else if(type.equals("exitkey")){
			String id = sc.next();
			double xLoc = sc.nextDouble();
			double yLoc = sc.nextDouble();
			String colorString = sc.next();
			Color color;
			if(colorString.equals("violet"))
				color = GameRunner.VIOLET_KEY_COLOR;
			else if(colorString.equals("indigo"))
				color = GameRunner.INDIGO_KEY_COLOR;
			else if(colorString.equals("blue"))
				color = GameRunner.BLUE_KEY_COLOR;
			else if(colorString.equals("green"))
				color = GameRunner.GREEN_KEY_COLOR;
			else
				throw new IllegalArgumentException("Unrecognized color: " + colorString);
			Entity e = new KeyEntity(xLoc, yLoc, color);
			linkableEntities.put(id, e);
			game.getSim().addEntity(e, "playeritems");
			return e;
		}
		else if(type.equals("button")){
			String id = sc.next();
			
			double xLoc = sc.nextDouble();
			double yLoc = sc.nextDouble();
			String mode = sc.next();
			int modeCode;
			if(mode.equals("toggle")){
				modeCode = ButtonEntity.TOGGLE_MODE;
			}
			else if(mode.equals("hold")){
				modeCode = ButtonEntity.HOLD_MODE;
			}
			else if(mode.equals("onepress")){
				modeCode = ButtonEntity.ONE_PRESS_MODE;
			}
			else{
				throw new IllegalArgumentException("Unrecognized button mode: " + mode);
			}
			
			ButtonEntity button = new ButtonEntity(xLoc, yLoc, modeCode);
			linkableEntities.put(id, button);
			game.getSim().addEntity(button, "playeritems");
			return button;
		}
		else if(type.equals("gate")){
			String id = sc.next();
			
			String mode = sc.next();
			int modeCode;
			if(mode.equals("and")){
				modeCode = TogglableEntity.AND_MODE;
			}
			else if(mode.equals("or")){
				modeCode = TogglableEntity.OR_MODE;
			}
			else if(mode.equals("xor")){
				modeCode = TogglableEntity.XOR_MODE;
			}
			else{
				throw new IllegalArgumentException("Unrecognized gate mode: " + mode);
			}
			
			boolean invert = decodeBoolean(sc);
			
			GateEntity gate = new GateEntity(decodeShapeGroup(sc), modeCode, invert);
			linkableEntities.put(id, gate);
			game.getSim().addEntity(gate, "terrain");
			return gate;
		}
		else if(type.equals("wire")){
			String id = sc.next();
			
			String mode = sc.next();
			int modeCode;
			if(mode.equals("and")){
				modeCode = TogglableEntity.AND_MODE;
			}
			else if(mode.equals("or")){
				modeCode = TogglableEntity.OR_MODE;
			}
			else if(mode.equals("xor")){
				modeCode = TogglableEntity.XOR_MODE;
			}
			else{
				throw new IllegalArgumentException("Unrecognized wire mode: " + mode);
			}
			
			boolean invert = decodeBoolean(sc);
			WireEntity wire = new WireEntity(decodeShapeGroup(sc), modeCode, invert);
			linkableEntities.put(id, wire);
			game.getSim().addEntity(wire, "nocollide");
			return wire;
		}
		else if(type.equals("link")){
			String id1 = sc.next();
			String id2 = sc.next();
			Entity e1 = linkableEntities.get(id1);
			Entity e2 = linkableEntities.get(id2);
			if(e1 instanceof ButtonEntity && e2 instanceof TogglableEntity){
				((TogglableEntity)e2).addButton((ButtonEntity)e1);
			}
			else if(e1 instanceof KeyedLevelExitEntity && e2 instanceof KeyEntity){
				((KeyedLevelExitEntity)e1).addKey((KeyEntity)e2);
			}
			else{
				throw new IllegalArgumentException("these entity types cannot be linked: " +
			                                       e1.getClass() + " " + e2.getClass());
			}
			return null;
		}
		else if(type.equals("fuelpickup")){
			Entity e = new FuelPickupEntity(sc.nextDouble(), sc.nextDouble());
			game.getSim().addEntity(e, "playeritems");
			return e;
		}
		else{
			throw new IllegalArgumentException("Unregonized entity type: " + type);
		}
		//TODO: Add additional entity types / modes
	}
	
	/*
	 * Decodes a shape
	 * 
	 * <shape>:	rectangle [xloc] [yloc] [xbound] [ybound]
	 * 			circle [xloc] [yloc] [radius]
	 * 			polygon [xloc] [yloc] [npoints] [x1] [y1] [x2] [y2] ... <- polygon points are relative to xloc, yloc
	 * 			abspolygon [npoints] [x1] [y1] [x2] [y2] ...            <- polygon points are absolute
	 * 			pipe [thickness] [wrapsaround?] [npoints] [x1] [y1] [x2] [y2] ...
	 */
	private static ShapeGroup decodeShapeGroup(Scanner sc){
		String shape = sc.next();
		if(shape.equals("rectangle")){
			return new ShapeGroup(new BoundingAABox(sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.nextDouble()));
		}
		else if(shape.equals("circle")){
			return new ShapeGroup(new BoundingCircle(sc.nextDouble(), sc.nextDouble(), sc.nextDouble()));
		}
		else if(shape.equals("polygon")){
			double xLoc = sc.nextDouble();
			double yLoc = sc.nextDouble();
			int nPoints = sc.nextInt();
			double xPoints[] = new double[nPoints];
			double yPoints[] = new double[nPoints];
			for(int i=0; i<nPoints; i++){
				xPoints[i] = sc.nextDouble();
				yPoints[i] = sc.nextDouble();
			}
			return new ShapeGroup(new BoundingPolygon(xLoc, yLoc, nPoints, xPoints, yPoints));
		}
		else if(shape.equals("abspolygon")){
			int nPoints = sc.nextInt();
			double xPoints[] = new double[nPoints];
			double yPoints[] = new double[nPoints];
			for(int i=0; i<nPoints; i++){
				xPoints[i] = sc.nextDouble();
				yPoints[i] = sc.nextDouble();
			}
			return new ShapeGroup(new BoundingPolygon(nPoints, xPoints, yPoints));
		}
		else if(shape.equals("pipe")){
			double thickness = sc.nextDouble();
			boolean wrapsAround = decodeBoolean(sc);
			int nPoints = sc.nextInt();
			double xPoints[] = new double[nPoints];
			double yPoints[] = new double[nPoints];
			for(int i=0; i<nPoints; i++){
				xPoints[i] = sc.nextDouble();
				yPoints[i] = sc.nextDouble();
			}
			return ShapeGroup.generatePipe(thickness, wrapsAround, nPoints, xPoints, yPoints, null);
		}
		//you could technically chain 'multi' with itself, and it would work, but like... don't.
		else if(shape.equals("multi")){
			int count = sc.nextInt();
			ShapeGroup result = decodeShapeGroup(sc);
			for(int i=1; i<count; i++)
				result.addAll(decodeShapeGroup(sc));
			return result;
		}
		else{
			throw new IllegalArgumentException("Unrecognized shape: " + shape);
		}
	}
	
	/*
	 * Decodes a boolean.
	 * "t" or "true" are accepted as true
	 * "f" or "false" are accepted as false
	 */
	private static boolean decodeBoolean(Scanner sc){
		String bool = sc.next();
		if(bool.equals("t") || bool.equals("true"))
			return true;
		else if(bool.equals("f") || bool.equals("false"))
			return false;
		else
			throw new IllegalArgumentException("Unrecognized boolean: " + bool);
	}
	
	
	public static void encodeMapFile(File fileName, StageInfo stage) throws FileNotFoundException{
		PrintWriter writer = new PrintWriter(fileName);

		encodeConstants(writer, stage);
		encodeMapSize(writer, stage);
		encodePlayerStart(writer, stage);
		encodeEntities(writer, stage);
		encodeNextStage(writer, stage);
	}
	
	private static void encodeConstants(PrintWriter writer, StageInfo stage){
		//TODO: Implement. Constants currently hard coded.
	}
	
	private static void encodeMapSize(PrintWriter writer, StageInfo stage){
		writer.println("MAP_SIZE");
		writer.println(stage.stageWidth + " " + stage.stageHeight);
		writer.println("END");
	}
	
	private static void encodePlayerStart(PrintWriter writer, StageInfo stage){
		writer.println("PLAYER_START");
		writer.println(stage.player.shapes.xLoc + " " + stage.player.shapes.yLoc);
		writer.println("END");
	}
	
	private static void encodeEntities(PrintWriter writer, StageInfo stage){
		writer.println("ENTITIES");
		for(String e : stage.entityCodeMap.values()){
			writer.println(e);
		}
		writer.println("END");
	}

	private static void encodeNextStage(PrintWriter writer, StageInfo stage){
		if(stage.nextStage == null)
			return;
		
		writer.println("NEXT_STAGE");
		writer.println(stage.nextStage);
		writer.println("END");
	}
	
	/**
	 * Given an entity, generates the line in a stage file that would specify that entity.
	 * Depending on the entity, some state based properties may be lost.
	 * @param e Entity to be encoded
	 * @return A string that could specify the entity in a stage file
	 */
	public static String encodeEntity(Entity e, StageInfo stage){
		if(e instanceof TerrainEntity){
			return "terrain " + encodeShapes(e.shapes) + "\n";
		}
		else if(e instanceof MineEntity){
			return "mine " + e.shapes.xLoc + " " + e.shapes.yLoc + "\n";
		}
		else if(e instanceof PatrollingMineEntity){
			PatrollingMineEntity pme = (PatrollingMineEntity)e;
			String result = "patrollingmine " + pme.nWaypoints + " ";
			for(int i=0; i<pme.nWaypoints; i++)
				result += pme.xWaypoints[i] + " " + pme.yWaypoints[i] + "\n";
			return result;
		}
		else if(e instanceof GunTurretEntity){
			GunTurretEntity gte = (GunTurretEntity)e;
			return "gunturret " + gte.shapes.xLoc + " " + gte.shapes.yLoc + " " + (gte.getFacing() / Math.PI) + "\n";
		}
		else if(e instanceof MissileTurretEntity){
			return "missileturret " + e.shapes.xLoc + " " + e.shapes.yLoc + "\n";
		}
		else if(e instanceof CoinEntity){
			CoinEntity ce = (CoinEntity)e;
			return "customcoin " + ce.shapes.xLoc + " " + ce.shapes.yLoc + " " +
					ce.shapes.xBound + " " + ce.getValue() + "\n";
		}
		else if(e instanceof LevelExitEntity){
			return "exit " + e.shapes.xLoc + " " + e.shapes.yLoc + "\n";
		}
		else if(e instanceof KeyedLevelExitEntity){
			KeyedLevelExitEntity klee = (KeyedLevelExitEntity)e;
			return "keyedexit " + stage.linkableEntityIDs.get(e) + " " + e.shapes.xLoc + " " + e.shapes.yLoc +
					" " + klee.getMaxKeys() + "\n";
		}
		else if(e instanceof KeyEntity){
			KeyEntity ke = (KeyEntity)e;
			String result = "exitkey " + stage.linkableEntityIDs.get(e) +
					        " " + e.shapes.xLoc + " " + e.shapes.yLoc + " ";
			
			if(ke.color.equals(GameRunner.VIOLET_KEY_COLOR))
				result += "violet\n";
			else if(ke.color.equals(GameRunner.INDIGO_KEY_COLOR))
				result += "indigo\n";
			else if(ke.color.equals(GameRunner.BLUE_KEY_COLOR))
				result += "blue\n";
			else if(ke.color.equals(GameRunner.GREEN_KEY_COLOR))
				result += "green\n";
			else
				throw new IllegalArgumentException("Unrecognized key color: " + ke.color);
			
			return result;
		}
		else if(e instanceof GateEntity){
			GateEntity ge = (GateEntity)e;
			String result = "gate " + stage.linkableEntityIDs.get(e) + " ";
			
			int mode = ge.getMode();
			if(mode == TogglableEntity.AND_MODE)
				result += "and ";
			else if(mode == TogglableEntity.OR_MODE)
				result += "or ";
			else if(mode == TogglableEntity.XOR_MODE)
				result += "xor ";
			else
				throw new IllegalArgumentException("Unrecognized gate mode: " + mode);
			
			if(ge.isInverted())
				result += "t ";
			else
				result += "f ";
			
			result += encodeShapes(e.shapes) + "\n";
			return result;
		}
		else if(e instanceof WireEntity){
			WireEntity we = (WireEntity)e;
			String result = "gate " + stage.linkableEntityIDs.get(e) + " ";
			
			int mode = ge.getMode();
			if(mode == TogglableEntity.AND_MODE)
				result += "and ";
			else if(mode == TogglableEntity.OR_MODE)
				result += "or ";
			else if(mode == TogglableEntity.XOR_MODE)
				result += "xor ";
			else
				throw new IllegalArgumentException("Unrecognized gate mode: " + mode);
			
			if(ge.isInverted())
				result += "t ";
			else
				result += "f ";
			
			result += encodeShapes(e.shapes) + "\n";
			return result;
		}
		else{
			throw new IllegalArgumentException("Unrecognized Entity type: " + e.getClass());
		}
	}
	
	private static String encodeShapes(ShapeGroup shapes){
		int numShapes = shapes.getNumShapes();
		if(numShapes == 1)
			return encodeShape(shapes.getShapeList().get(0));
		else{
			String result = "multi " + numShapes + " ";
			List<BoundingShape> shapeList = shapes.getShapeList();
			for(BoundingShape shape : shapeList)
				result += encodeShape(shape);
			return result;
		}
	}
	
	private static String encodeShape(BoundingShape shape){
		if(shape instanceof BoundingAABox){
			return "rectangle " + shape.xLoc + " " + shape.yLoc + " " + shape.xBound + " " + shape.yBound + " ";
		}
		else if(shape instanceof BoundingCircle){
			return "circle " + shape.xLoc + " " + shape.yLoc + " " + shape.xBound + " ";
		}
		else if(shape instanceof BoundingPolygon){
			BoundingPolygon bp = (BoundingPolygon)shape;
			String result = "polygon " + shape.xLoc + " " + shape.yLoc + " " + bp.nPoints + " ";
			for(int i=0; i<bp.nPoints; i++)
				result += bp.xPoints[i] + " " + bp.yPoints[i] + " ";
			return result;
		}
		else{
			throw new IllegalArgumentException("Unrecognized sort of BoundingShape: " + shape.getClass());
		}
	}
	
}
