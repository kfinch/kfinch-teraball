package gamePvE;

import java.awt.Color;
import java.awt.Graphics2D;

import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class GateEntity extends TogglableEntity {

	private Color closedColor;
	private Color openColor;
	 
	//defaults to "on means closed, off means open". If invert is true, this is reversed.
	private boolean invert;
	
	public GateEntity(ShapeGroup shapes, int mode, Color closedColor, Color openColor, boolean invert) {
		super(shapes, "terrain", mode);
		this.closedColor = closedColor;
		this.openColor = openColor;
		this.invert = invert;
		
		if(isOn ^ invert){
			addTag("solidimmovable");
			shapes.setColor(closedColor);
		}
		else{
			removeTag("solidimmovable");
			shapes.setColor(openColor);
		}
	}
	
	public GateEntity(ShapeGroup shapes, int mode, boolean invert){
		this(shapes, mode, GameRunner.CLOSED_GATE_COLOR, GameRunner.OPEN_GATE_COLOR, invert);
	}
	
	public boolean isInverted(){
		return invert;
	}

	@Override
	protected void updateState(){
		super.updateState();
		if(changedState){
			if(isOn ^ invert){
				addTag("solidimmovable");
				shapes.setColor(closedColor);
			}
			else{
				removeTag("solidimmovable");
				shapes.setColor(openColor);
			}
		}
	}
	
	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {}

	@Override
	public void postStep() {}
}
