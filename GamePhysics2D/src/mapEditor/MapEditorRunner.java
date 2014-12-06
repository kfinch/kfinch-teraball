package mapEditor;

import gamePhysics2D.BoundingAABox;
import gamePhysics2D.BoundingPoint;
import gamePhysics2D.Entity;
import gamePhysics2D.Point2d;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;
import gamePvE.GameRunner;
import gamePvE.MapCoder;

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
	
	protected boolean isBoxSelecting; //is user 'box selecting' entities?
	protected boolean isEntityDragging; //is user dragging entities?
	
	public MapEditorRunner(){
		game = new GameRunner(null); //can leave parent null, because methods that use it won't be called
		
		selectedEntities = new HashSet<Entity>();
		
		display = new MapEditorPanel(this);
		
		mouseLoc = new Point2d(0,0);
		mouseDragStartLoc = new Point2d(0,0);
	}
	
	/**
	 * Adds at most one entity at the chosen location to the list of selected entities
	 * @param xLoc x location (game coordinates) of the selected spot
	 * @param yLoc y location (game coordinates) of the selected spot
	 */
	public void selectEntity(double xLoc, double yLoc){
		ShapeGroup point = new ShapeGroup(new BoundingPoint(xLoc, yLoc));
		List<Entity> selection = game.getSim().detectIndividualCollision(point);
		cullSelection(selection);
		if(!selection.isEmpty())
			selectedEntities.add(selection.get(0));
	}
	
	/**
	 * Adds all entities in the selected area to the list of selected entities
	 * @param xLoc x location (game coordinates) of the center of the selected area
	 * @param yLoc y location (game coordinates) of the center of the selected area
	 * @param xBound x bound (game coordinates) of the rectangular selection area. Width of area = 2 * xBound
	 * @param yBound y bound (game coordinates) of the rectangular selection area. Height of area = 2 * yBound
	 */
	public void selectEntities(double xLoc, double yLoc, double xBound, double yBound){
		ShapeGroup box = new ShapeGroup(new BoundingAABox(xLoc, yLoc, xBound, yBound));
		List<Entity> selection = game.getSim().detectIndividualCollision(box);
		cullSelection(selection);
		selectedEntities.addAll(selection);
	}
	
	/*
	 * Removes from the selection pool entities that should not be selectable in the map editor
	 */
	private void cullSelection(List<Entity> selection){
		Iterator<Entity> iter = selection.iterator();
		while(iter.hasNext()){
			Entity e = iter.next();
			if(e.hasTag("los") || e.hasTag("edge") || e.hasTag("playershield"))
				iter.remove();
		}
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
	//TODO: separate 'opening the file' and 'reading from the file' to two separate methods
	public void openStageFile(File stageFile){
		try {
			openedFile = stageFile;
			game = new GameRunner(null);
			stage = MapCoder.decodeMapFile(openedFile, game);
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + stageFile);
		}
		
		//centers display on player, then asks for a repaint of the display
		display.cornerX = game.playerEntity.shapes.xLoc - (display.panelWidth / 2);
		display.cornerY = game.playerEntity.shapes.yLoc - (display.panelHeight / 2);
		display.repaint();
	}
	
	/**
	 * Write the opened stage to a target file.
	 * @param stageFile A File object representing the stage file to be written to. 
	 * 					Will overwrite whatever is currently in the file.
	 */
	public void writeStageFile(File stageFile){
		try {
			MapCoder.encodeMapFile(stageFile, stage);
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + stageFile);
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
		
		if(SwingUtilities.isRightMouseButton(e)){
			//TODO: handle problems when right and left mouse buttons pressed at same time
			display.cornerX += (mouseLoc.x - e.getX()) / display.zoom;
			display.cornerY += (mouseLoc.y - e.getY()) / display.zoom;
			mouseLoc.x = e.getX();
			mouseLoc.y = e.getY();
		}
		else if(isBoxSelecting){
			updateMouseLoc(e);
		}
		else if(isEntityDragging){
			updateMouseLoc(e);
			//TODO: draw dragging entities (or actually update their location)
		}
		
		display.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		updateMouseLoc(e);
		//select one entity at mouse click
		if(SwingUtilities.isLeftMouseButton(e)){
			if(!e.isShiftDown())
				clearSelection();
			Point2d gameLoc = displayLocToGameLoc(mouseLoc);
			selectEntity(gameLoc.x, gameLoc.y);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		updateMouseLoc(e);
		mouseDragStartLoc.x = e.getX();
		mouseDragStartLoc.y = e.getY();
		gameMouseDragStartLoc = displayLocToGameLoc(mouseDragStartLoc);
		
		if(SwingUtilities.isLeftMouseButton(e)){
			boolean clickingOnSelectedEntity = false;
			ShapeGroup point = new ShapeGroup(new BoundingPoint(gameMouseLoc.x, gameMouseLoc.y));
			List<Entity> selection = game.getSim().detectIndividualCollision(point);
			cullSelection(selection);
			
			for(Entity en1 : selectedEntities){
				for(Entity en2 : selection){
					if(en1 == en2)
						clickingOnSelectedEntity = true;
				}
			}
			
			if(clickingOnSelectedEntity){
				isEntityDragging = true;
				//TODO: init shadowy entities being dragged graphic?
			}
			else{
				isBoxSelecting = true;
				if(!e.isShiftDown())
					clearSelection();
			}
		}
		
		display.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		updateMouseLoc(e);
		
		//select one or more entities that were just boxed with the mouse
		if(isBoxSelecting){
			isBoxSelecting = false;
			Point2d gameLoc1 = displayLocToGameLoc(mouseDragStartLoc);
			Point2d gameLoc2 = displayLocToGameLoc(mouseLoc);
			double xLoc = (gameLoc1.x + gameLoc2.x) / 2;
			double yLoc = (gameLoc1.y + gameLoc2.y) / 2;
			double xBound = Math.abs(gameLoc1.x - gameLoc2.x) / 2;
			double yBound = Math.abs(gameLoc1.y - gameLoc2.y) / 2;
			selectEntities(xLoc, yLoc, xBound, yBound);
		}
		else if(isEntityDragging){
			isEntityDragging = false;
			for(Entity en : selectedEntities){
				en.shapes.translate(new Vector2d(gameMouseDragStartLoc, gameMouseLoc));
				//TODO: handle change to entity string
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
	
	private void updateMouseLoc(MouseEvent e){
		mouseLoc.x = e.getX();
		mouseLoc.y = e.getY();
		gameMouseLoc = displayLocToGameLoc(mouseLoc);
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


