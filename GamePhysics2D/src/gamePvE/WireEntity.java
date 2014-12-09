package gamePvE;

import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

import java.awt.Color;

public class WireEntity extends TogglableEntity {
	
	private static final long serialVersionUID = 5168598323106401199L;
	
	private Color offColor;
	private Color onColor;
	 
	//defaults to "on means on color displayed, off means off color displayed". If invert is true, this is reversed.
	private boolean invert;
	
	public WireEntity(ShapeGroup shapes, int mode, Color offColor, Color onColor, boolean invert) {
		super(shapes, "terrain", mode);
		this.offColor = offColor;
		this.onColor = onColor;
		this.invert = invert;
		
		if(isOn ^ invert)
			shapes.setColor(onColor);
		else
			shapes.setColor(offColor);
	}
	
	public WireEntity(ShapeGroup shapes, int mode, boolean invert){
		this(shapes, mode, GameRunner.OFF_WIRE_COLOR, GameRunner.ON_WIRE_COLOR, invert);
	}
	
	public boolean isInverted(){
		return invert;
	}
	
	@Override
	protected void updateState(){
		super.updateState();
		if(changedState){
			if(isOn ^ invert)
				shapes.setColor(onColor);
			else
				shapes.setColor(offColor);
		}
			
	}

	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {}

	@Override
	public void postStep() {}
}
