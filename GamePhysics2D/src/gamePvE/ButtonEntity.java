package gamePvE;

import java.awt.Color;
import java.awt.Graphics2D;

import gamePhysics2D.BoundingCircle;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class ButtonEntity extends Entity {

	private static final long serialVersionUID = 7035873488234804895L;
	
	public static final int TOGGLE_MODE = 0; //pressing button toggles between 'on' and 'off'
	public static final int HOLD_MODE = 1; //button is 'on' only while pressed
	public static final int ONE_PRESS_MODE = 2; //pressing button once leaves it permanently 'on'
	
	private final int mode;
	
	public boolean isPressed;
	public boolean wasPressed;
	public boolean isOn;
	
	private Color onColor;
	private Color offColor;
	
	public ButtonEntity(ShapeGroup shapes, Color onColor, Color offColor, int mode) {
		super(shapes, "button");
		this.onColor = onColor;
		this.offColor = offColor;
		this.mode = mode;
		isPressed = false;
		wasPressed = false;
		isOn = false;
	}
	
	public ButtonEntity(double xLoc, double yLoc, int mode){
		this(generateDefaultShapes(xLoc, yLoc), GameRunner.PRESSED_BUTTON_COLOR,
		     GameRunner.UNPRESSED_BUTTON_COLOR, mode);
	}
	
	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		return new ShapeGroup(new BoundingCircle(xLoc, yLoc, GameRunner.BUTTON_SIZE));
	}
	
	public int getMode(){
		return mode;
	}

	@Override
	public void link(Entity e){
		if(e instanceof TogglableEntity)
			e.link(this);
		else
			super.link(e);
	}
	
	@Override
	public void preStep() {}

	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {
		if(e.hasTag("player")){
			isPressed = true;
			if(mode == HOLD_MODE)
				isOn = false;
			
			switch(mode){
			case TOGGLE_MODE:
				if(!wasPressed){
					if(isOn)
						isOn = false;
					else
						isOn = true;
				}
				break;
			case HOLD_MODE:
				isOn = true;
				break;
			case ONE_PRESS_MODE:
				isOn = true;
				break;
			}
			
		}
	}

	@Override
	public void postStep() {
		wasPressed = false;
		if(isPressed && mode != ONE_PRESS_MODE){
			isPressed = false;
			wasPressed = true;
		}
	}

	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		if(isOn)
			shapes.setColor(onColor);
		else
			shapes.setColor(offColor);
		super.paintEntity(g2d, xoffset, yoffset, scale);
	}
	
}
