package mapEditor;

import gamePhysics2D.Entity;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class MapEditorPanel extends JPanel {

	protected int panelWidth, panelHeight; //size of editor panel
	protected double cornerX, cornerY; //location (in stage coordinates) of the corner of the screen
	
	//Amount the display is zoomed in.  (in-game distance units) = (pixels on display) * (zoom)
	protected double zoom;
	
	private MapEditorRunner editor;
	
	public MapEditorPanel(MapEditorRunner editor){
		this.editor = editor;
		
		cornerX = 0;
		cornerY = 0;
		zoom = 1;
		
		//adds listeners to receive input
		addMouseListener(editor);
		addMouseMotionListener(editor);
		addMouseWheelListener(editor);
		setFocusable(true);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		updateDimensions();
		doPainting((Graphics2D)g);
	}
	
	private void updateDimensions(){
		panelWidth = getSize().width;
		panelHeight = getSize().height;
	}
	
	private void doPainting(Graphics2D g2d){
		//paint entities
		editor.game.getSim().paintEntities(g2d, cornerX, cornerY, zoom);
		
		//paint entity additional info for selected entities
		for(Entity e : editor.selectedEntities)
			e.paintAdditionalEntityInfo(g2d, cornerX, cornerY, zoom);
		
		//paint selection boxes
		for(Entity e : editor.selectedEntities){
			g2d.setColor(MapEditorRunner.SELECTION_BOX_COLOR);
			int rectXLoc = (int) ((e.shapes.xLoc - e.shapes.xBound - cornerX) * zoom);
			int rectYLoc = (int) ((e.shapes.yLoc - e.shapes.yBound - cornerY) * zoom);
			int rectXSize = (int) (e.shapes.xBound * 2 * zoom);
			int rectYSize = (int) (e.shapes.yBound * 2 * zoom);
			g2d.drawRect(rectXLoc, rectYLoc, rectXSize, rectYSize);
		}
		
		//paint mouse click-drag box
		if(editor.userState == MapEditorRunner.BOX_SELECTING){
			int xMin = (int) Math.min(editor.mouseLoc.x, editor.mouseDragStartLoc.x);
			int yMin = (int) Math.min(editor.mouseLoc.y, editor.mouseDragStartLoc.y);
			int xMax = (int) Math.max(editor.mouseLoc.x, editor.mouseDragStartLoc.x);
			int yMax = (int) Math.max(editor.mouseLoc.y, editor.mouseDragStartLoc.y);
			g2d.setColor(MapEditorRunner.MOUSE_DRAG_COLOR);
			g2d.drawRect(xMin, yMin, xMax-xMin, yMax-yMin);
		}
		//paint transparent versions of selected entities, showing where they will be dropped
		else if(editor.userState == MapEditorRunner.DRAGGING_ENTITIES){
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
			double offsetX = editor.gameMouseDragStartLoc.x - editor.gameMouseLoc.x;
			double offsetY = editor.gameMouseDragStartLoc.y - editor.gameMouseLoc.y;
			for(Entity e : editor.selectedEntities)
				e.paintEntity(g2d, cornerX+offsetX, cornerY+offsetY, zoom);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		}
		//paint transparent version of entity to placed, showing where it will be placed
		else if(editor.userState == MapEditorRunner.PLACING_ENTITY){
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
			double offsetX = -editor.gameMouseLoc.x;
			double offsetY = -editor.gameMouseLoc.y;
			editor.entityToPlace.paintEntity(g2d, cornerX+offsetX, cornerY+offsetY, zoom);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		}
	}
}
