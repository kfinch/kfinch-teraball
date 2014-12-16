package gamePvE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import gamePhysics2D.BoundingLineSegment;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;

public abstract class TogglableEntity extends Entity {

	private static final long serialVersionUID = 1969462688072757164L;
	
	public static final int AND_MODE = 0; //on iff all attached buttons are on.
	public static final int OR_MODE = 1; //on iff at least one attached button is on.
	public static final int XOR_MODE = 2; //on iff exactly one attached button is on.
	
	private final int mode;
	
	protected List<ButtonEntity> buttons; //list of buttons that can toggle this entity
	protected boolean isOn; //true iff this entity is toggled 'on'
	private boolean wasOn; //true iff this enttiy was toggled 'on' last step. Used for finding if entity was toggled.
	protected boolean changedState; //set during prestep, true iff isOn changed during this prestep.
	
	public TogglableEntity(ShapeGroup shapes, String tag, int mode) {
		super(shapes, tag);
		this.mode = mode;
		buttons = new ArrayList<ButtonEntity>();
		isOn = false; //defaults to off until state is updated with first button added
		wasOn = false;
		changedState = false;
	}
	
	public int getMode(){
		return mode;
	}

	protected void updateState(){
		wasOn = isOn;
		
		switch(mode){
		case AND_MODE:
			isOn = true;
			for(ButtonEntity button : buttons){
				if(!button.isOn){
					isOn = false;
					break;
				}
			}
			break;
		case OR_MODE:
			isOn = false;
			for(ButtonEntity button : buttons){
				if(button.isOn){
					isOn = true;
					break;
				}
			}
			break;
		case XOR_MODE:
			isOn = false;
			for(ButtonEntity button : buttons){
				if(isOn && button.isOn){
					isOn = false;
					break;
				}
				else if(button.isOn){
					isOn = true;
				}
			}
			break;
		}
		
		if(wasOn != isOn)
			changedState = true;
		else
			changedState = false;
	}
	
	public void addButton(ButtonEntity button){
		buttons.add(button);
		updateState();
	}
	
	/*
	 * Behavior is undefined if you remove all the buttons (but it shouldn't crash)
	 */
	public boolean removeButton(ButtonEntity button){
		boolean result = buttons.remove(button);
		updateState();
		return result;
	}
	
	@Override
	public void addLink(Entity e){
		if(e instanceof ButtonEntity)
			addButton((ButtonEntity) e);
		else
			super.addLink(e);
	}
	
	@Override
	public boolean removeLink(Entity e){
		if(e instanceof ButtonEntity)
			return removeButton((ButtonEntity) e);
		else
			return super.removeLink(e);
	}
	
	@Override
	public void preStep(){
		updateState();
	}
	
	@Override
	public void paintAdditionalEntityInfo(Graphics2D g2d, double xoffset, double yoffset, double scale){
		Graphics2D gcopy = (Graphics2D) g2d.create();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
		gcopy.setStroke(dashed);
		gcopy.setColor(Color.red);
		
		List<BoundingShape> linkLines = new ArrayList<BoundingShape>(buttons.size());
		for(Entity e : buttons)
			linkLines.add(new BoundingLineSegment(shapes.xLoc, shapes.yLoc, e.shapes.xLoc, e.shapes.yLoc));
		ShapeGroup paint = new ShapeGroup(linkLines, null);
		paint.paintShapes(gcopy, xoffset, yoffset, scale);
	}
}
