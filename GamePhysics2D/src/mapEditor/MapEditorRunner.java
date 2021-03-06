package mapEditor;

import gamePhysics2D.BoundingAABox;
import gamePhysics2D.BoundingPoint;
import gamePhysics2D.Entity;
import gamePhysics2D.Point2d;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;
import gamePvE.GameRunner;
import gamePvE.MapCoder;
import gamePvE.MineEntity;
import gamePvE.StageSerializer;
import gamePvE.TerrainEntity;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

public class MapEditorRunner implements MouseListener, MouseMotionListener, MouseWheelListener {

	public static final Color SELECTION_BOX_COLOR = Color.decode("#ff0000");
	public static final Color MOUSE_DRAG_COLOR = Color.decode("#999999");
	
	public static final int MANIPULATOR_TOOL = 0;
	public static final int DRAGGING_ENTITIES = 1;
	public static final int BOX_SELECTING = 2;
	public static final int DRAWING_SHAPE = 3;
	public static final int PLACING_ENTITY = 4;
	
	public static final double ZOOM_FACTOR = 1.15;
	
	//the currently opened stage's info
	protected StageInfo stage;
	
	//a game runner to simulate a stage's entities. It will never be actually started.
	protected GameRunner game;
	
	//the file descriptor of the currently opened stage
	protected File openedFile;
	
	//the selected entity or entities. Set is empty if nothing is selected.
	protected Set<Entity> selectedEntities;
	
	//JPanel to display the stage
	protected MapEditorPanel display;
	
	//information about the mouse
	protected Point2d mouseLoc; //mouse location in display coordinates
	protected Point2d gameMouseLoc; //mouse location in game coordinates
	protected Point2d mouseDragStartLoc; //start of mouse drag location in display coordinates
	protected Point2d gameMouseDragStartLoc; //start of mouse drag location in game coordinates
	
	protected int userState; //tracks what the user is doing, see the integer constants above.
	
	//the entity type selected for placement. Will be placed on mouse click if userState == PLACING_ENTITY
	protected Entity entityToPlace;
	
	//TODO: implement snap to grid in a sensible way
	protected boolean isSnapToGridOn; //tracks if snap to grid is on.
	protected double snapToGrid; //spacing of "snap to grid" feature.
	
	public MapEditorRunner(){
		game = new GameRunner(null); //can leave parent null, because methods that use it won't be called
		
		selectedEntities = new HashSet<Entity>();
		
		display = new MapEditorPanel(this);
		
		mouseLoc = new Point2d(0,0);
		mouseDragStartLoc = new Point2d(0,0);
		
		userState = MANIPULATOR_TOOL;
		
		entityToPlace = null;
		
		isSnapToGridOn = false;
		snapToGrid = 10;
	}
	
	/*
	 * Returns a list of selectable entities that intersect with the given shape(s).
	 */
	private List<Entity> getSelectableEntitiesIntersectingShapes(ShapeGroup shapes){
		List<Entity> selection = game.getSim().detectIndividualCollision(shapes);
		cullSelection(selection);
		return selection;
	}
	
	/*
	 * Removes from the selection pool entities that should not be selectable in the map editor
	 */
	private void cullSelection(List<Entity> selection){
		Iterator<Entity> iter = selection.iterator();
		while(iter.hasNext()){
			Entity e = iter.next();
			if(e.hasTag("los") || e.hasTag("edge") || e.hasTag("playershield") || e.hasTag("player"))
				iter.remove();
		}
	}
	
	/*
	 * Returns the 'top most' selectable entity at the chosen location (or null if nothing is there)
	 */
	private Entity getSelectableEntityAtPoint(double xLoc, double yLoc){
		List<Entity> selection = getSelectableEntitiesIntersectingShapes(new ShapeGroup(new BoundingPoint(xLoc, yLoc)));
		if(selection.isEmpty())
			return null;
		return selection.get(0);
	}
	
	/*
	 * Returns all selectable entities within the chosen box
	 */
	private List<Entity> getSelectableEntitiesInBox(double xLoc, double yLoc, double xBound, double yBound){
		ShapeGroup box = new ShapeGroup(new BoundingAABox(xLoc, yLoc, xBound, yBound));
		return getSelectableEntitiesIntersectingShapes(box);
	}
	
	/**
	 * Adds the 'top most' entity at the chosen location to the selection list.
	 * @param xLoc x location (game coordinates) of the selected spot
	 * @param yLoc y location (game coordinates) of the selected spot
	 */
	public void selectEntity(double xLoc, double yLoc){
		Entity e = getSelectableEntityAtPoint(xLoc, yLoc);
		
		if(e == null)
			return;
		
		selectedEntities.add(e);
	}
	
	/**
	 * Removes the 'top most' entity at the chosen location to the selection list.
	 * @param xLoc x location (game coordinates) of the selected spot
	 * @param yLoc y location (game coordinates) of the selected spot
	 */
	public void deselectEntity(double xLoc, double yLoc){
		Entity e = getSelectableEntityAtPoint(xLoc, yLoc);
		
		if(e == null)
			return;
		
		selectedEntities.remove(e);
	}
	
	/**
	 * Adds the 'top most' entity at the chosen location to the selection list
	 * if it wasn't already selected, removes it if it was already selected.
	 * @param xLoc x location (game coordinates) of the selected spot
	 * @param yLoc y location (game coordinates) of the selected spot
	 */
	public void toggleEntitySelection(double xLoc, double yLoc){
		Entity e = getSelectableEntityAtPoint(xLoc, yLoc);
		
		if(e == null)
			return;
		
		if(selectedEntities.contains(e))
			selectedEntities.remove(e);
		else
			selectedEntities.add(e);
	}
	
	/**
	 * Adds all entities in the selected area to the list of selected entities
	 * @param xLoc x location (game coordinates) of the center of the selected area
	 * @param yLoc y location (game coordinates) of the center of the selected area
	 * @param xBound x bound (game coordinates) of the rectangular selection area. Width of area = 2 * xBound
	 * @param yBound y bound (game coordinates) of the rectangular selection area. Height of area = 2 * yBound
	 */
	public void selectEntities(double xLoc, double yLoc, double xBound, double yBound){
		selectedEntities.addAll(getSelectableEntitiesInBox(xLoc, yLoc, xBound, yBound));
	}
	
	/**
	 * Removes all entities in the selected area from the list of selected entities
	 * @param xLoc x location (game coordinates) of the center of the selected area
	 * @param yLoc y location (game coordinates) of the center of the selected area
	 * @param xBound x bound (game coordinates) of the rectangular selection area. Width of area = 2 * xBound
	 * @param yBound y bound (game coordinates) of the rectangular selection area. Height of area = 2 * yBound
	 */
	public void deselectEntities(double xLoc, double yLoc, double xBound, double yBound){
		selectedEntities.removeAll(getSelectableEntitiesInBox(xLoc, yLoc, xBound, yBound));
	}
	
	/**
	 * Deselects all selected entities.
	 */
	public void clearSelection(){
		selectedEntities.clear();
	}
	
	/**
	 * Opens a stage file.
	 * @param stageFile A File object representing the stage file to open.
	 */
	public void openStageFile(File stageFile){
		openedFile = stageFile;
		game = new GameRunner(null);
		stage = StageSerializer.deserializeStage(stageFile);
		stage.loadStage(game);
		
		//centers display on player, goes to default zoom, then asks for a repaint of the display
		display.cornerX = game.playerEntity.shapes.xLoc - (display.panelWidth / 2);
		display.cornerY = game.playerEntity.shapes.yLoc - (display.panelHeight / 2);
		display.zoom = 1;
		display.repaint();
	}
	
	public void saveStageFile(){
		writeStageFile(openedFile);
	}
	
	public void saveAsStageFile(File newFile){
		writeStageFile(newFile);
	}
	
	/**
	 * Write the opened stage to a target file.
	 * @param stageFile A File object representing the stage file to be written to. 
	 * 					Will overwrite whatever is currently in the file.
	 */
	//TODO: will it overwrite properly? test this.
	public void writeStageFile(File stageFile){
		StageSerializer.serializeStage(stage, stageFile);
	}
	
	/*
	 * Temp method to convert current form of stage file into the new form
	 */
	public void bootstrapStage(File oldStageFile, File newStageFile){
		try {
			OldStageInfo oldStage = MapCoder.decodeMapFile(oldStageFile, game);
			StageInfo newStage = StageSerializer.generateNewStageInfoFromOld(oldStage, game);
			game = new GameRunner(null);
			newStage.loadStage(game);
			StageSerializer.serializeStage(newStage, newStageFile);
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		}
	}
	
	public void switchToManipulatorTool(){
		userState = MANIPULATOR_TOOL;
	}
	
	/*
	 * A collection of methods that enable entities to be added to a stage.
	 * They all set userState to either DEFINING_ENTITY or PLACING_ENTITY,
	 * and they initialize the entity that will be added.
	 * The entity will not actually be added to the simulator until finalizeEntityPlacement() is called.
	 * When new entity types are introduced to the map editor,
	 * a new placeEntity method will need to be added below, and finalizeEntityPlacement() will need to be modified.
	 */
	
	public void placeTerrainEntity(){
		
	}
	
	public void placeMineEntity(){
		userState = PLACING_ENTITY;
		entityToPlace = new MineEntity(0,0);
	}
	
	public void finalizeEntityPlacement(){
		if(entityToPlace instanceof TerrainEntity){
			
		}
		else if(entityToPlace instanceof MineEntity){
			Entity e = entityToPlace.deepCopy();
			e.shapes.moveTo(gameMouseLoc);
			stage.sim.addEntity(e, "terrain");
		}
	}
	
	

	/*
	 * Given a pixel coordinates on the screen, returns the location that corresponds to in the game coordinates.
	 */
	public Point2d displayLocToGameLoc(Point2d displayLoc){
		double gameX = display.cornerX + (displayLoc.x / display.zoom);
		double gameY = display.cornerY + (displayLoc.y / display.zoom);
		return new Point2d(gameX, gameY);
	}
	
	/*
	 * Given game coordinates, returns pixel coordinates on the screen (coordinates could be out of the display pane)
	 */
	public Point2d gameLocToDisplayLoc(Point2d gameLoc){
		double displayX = (gameLoc.x - display.cornerX) * display.zoom;
		double displayY = (gameLoc.y - display.cornerY) * display.zoom;
		return new Point2d(displayX, displayY);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		Point2d prevMouseLoc = new Point2d(mouseLoc);
		updateMouseLoc(e, true);
		if(SwingUtilities.isRightMouseButton(e)){
			//TODO: handle problems when right and left mouse buttons pressed at same time
			updateMouseLoc(e, false);
			display.cornerX += (prevMouseLoc.x - mouseLoc.x) / display.zoom;
			display.cornerY += (prevMouseLoc.y - mouseLoc.y) / display.zoom;
		}
		if(userState == DRAGGING_ENTITIES){
			//constrain to axis
			if(e.isShiftDown()){
				double xDiff = Math.abs(gameMouseLoc.x - gameMouseDragStartLoc.x);
				double yDiff = Math.abs(gameMouseLoc.y - gameMouseDragStartLoc.y);
				if(xDiff > yDiff){
					mouseLoc.y = mouseDragStartLoc.y;
					gameMouseLoc.y = gameMouseDragStartLoc.y;
				}
				else{
					mouseLoc.x = mouseDragStartLoc.x;
					gameMouseLoc.x = gameMouseDragStartLoc.x;
				}
			}
		}
		
		display.repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if(userState == PLACING_ENTITY){
			updateMouseLoc(e, true);
			display.repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		updateMouseLoc(e, true);
		if(SwingUtilities.isLeftMouseButton(e)){
			if(userState == PLACING_ENTITY){
				finalizeEntityPlacement();
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		updateMouseLoc(e, true);
		mouseDragStartLoc.x = e.getX();
		mouseDragStartLoc.y = e.getY();
		gameMouseDragStartLoc = displayLocToGameLoc(mouseDragStartLoc);
		
		if(SwingUtilities.isLeftMouseButton(e)){
			if(userState == MANIPULATOR_TOOL){
				boolean clickingOnEntity = false;
				boolean clickingOnSelectedEntity = false;
				Entity en = getSelectableEntityAtPoint(gameMouseLoc.x, gameMouseLoc.y);
				if(en != null){
					clickingOnEntity = true;
					if(selectedEntities.contains(en))
						clickingOnSelectedEntity = true;
				}
			
				if(clickingOnSelectedEntity){
					userState = DRAGGING_ENTITIES;
				}
				else if(clickingOnEntity){
					clearSelection();
					selectedEntities.add(en);
					userState = DRAGGING_ENTITIES;
				}
				else{
					userState = BOX_SELECTING;
				}
			}
		}
		
		display.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		updateMouseLoc(e, true);
		
		//select one or more entities that were just boxed with the mouse
		if(SwingUtilities.isLeftMouseButton(e)){
			if(userState == BOX_SELECTING){
				userState = MANIPULATOR_TOOL;
				Point2d gameLoc1 = displayLocToGameLoc(mouseDragStartLoc);
				Point2d gameLoc2 = displayLocToGameLoc(mouseLoc);
				double xLoc = (gameLoc1.x + gameLoc2.x) / 2;
				double yLoc = (gameLoc1.y + gameLoc2.y) / 2;
				double xBound = Math.abs(gameLoc1.x - gameLoc2.x) / 2;
				double yBound = Math.abs(gameLoc1.y - gameLoc2.y) / 2;
				if(e.isControlDown())
					deselectEntities(xLoc, yLoc, xBound, yBound);
				else{
					if(!e.isShiftDown())
						clearSelection();
					selectEntities(xLoc, yLoc, xBound, yBound);
				}
			}
			else if(userState == DRAGGING_ENTITIES){
				userState = MANIPULATOR_TOOL;
				//constrain to axis
				if(e.isShiftDown()){
					double xDiff = Math.abs(gameMouseLoc.x - gameMouseDragStartLoc.x);
					double yDiff = Math.abs(gameMouseLoc.y - gameMouseDragStartLoc.y);
					if(xDiff > yDiff){
						mouseLoc.y = mouseDragStartLoc.y;
						gameMouseLoc.y = gameMouseDragStartLoc.y;
					}
					else{
						mouseLoc.x = mouseDragStartLoc.x;
						gameMouseLoc.x = gameMouseDragStartLoc.x;
					}
				}
				for(Entity en : selectedEntities){
					//theoretically this should reflect in both GameRunner and StageInfo. TODO: Keep a careful eye on it.
					en.shapes.translate(new Vector2d(gameMouseDragStartLoc, gameMouseLoc));
				}
			}
		}
		
		display.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int wheelClicks = e.getWheelRotation();
		if(wheelClicks > 0){
			for(int i=0; i<wheelClicks; i++)
				zoom(e.getX(), e.getY(), 1/ZOOM_FACTOR);
		}
		else if(wheelClicks < 0){
			for(int i=0; i<-wheelClicks; i++)
				zoom(e.getX(), e.getY(), ZOOM_FACTOR);
		}
		display.repaint();
	}
	
	//TODO: snap to grid when dragging entities still not working quite right, offset issues?
	private void updateMouseLoc(MouseEvent e, boolean applySnapToGrid){
		mouseLoc.x = e.getX();
		mouseLoc.y = e.getY();
		gameMouseLoc = displayLocToGameLoc(mouseLoc);
		if(isSnapToGridOn && applySnapToGrid){
			gameMouseLoc.x = dSnap(gameMouseLoc.x, snapToGrid);
			gameMouseLoc.y = dSnap(gameMouseLoc.y, snapToGrid);
		}
	}
	
	private double dSnap(double d, double gridSize){
		double mod = d % gridSize;
		if(mod > gridSize/2)
			return d - mod + gridSize;
		else
			return d - mod;
	}
	
	/*
	 * Zooms the display by zoomFactor
	 * while ensuring that whatever the mouse is pointing at remains in the same place on the screen.
	 */
	private void zoom(double mouseX, double mouseY, double zoomFactor){
		display.cornerX = display.cornerX + (mouseX/display.zoom) - (mouseX/display.zoom/zoomFactor);
		display.cornerY = display.cornerY + (mouseY/display.zoom) - (mouseY/display.zoom/zoomFactor);
		display.zoom *= zoomFactor;
	}
	

}


