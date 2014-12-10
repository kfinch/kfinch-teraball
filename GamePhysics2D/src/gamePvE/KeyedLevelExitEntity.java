package gamePvE;

import gamePhysics2D.BoundingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.Entity;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class KeyedLevelExitEntity extends Entity {
	
	private static final long serialVersionUID = -2546909117783528676L;
	
	private int maxKeys;
	private int currKeys;
	private KeyEntity[] keys;
	private ShapeGroup[] keyHoles;
	
	public KeyedLevelExitEntity(ShapeGroup shapes, int maxKeys, KeyEntity[] keys, ShapeGroup[] keyHoles) {
		super(shapes, "keyedexit");
		this.maxKeys = maxKeys;
		this.keys = keys;
		this.keyHoles = keyHoles;
		currKeys = 0;
	}
	
	/*
	 * numKeys must be between 1 and 4 (inclusive)
	 */
	public KeyedLevelExitEntity(double xLoc, double yLoc, int numKeys){
		this(generateDefaultShapes(xLoc, yLoc), numKeys, new KeyEntity[numKeys], new ShapeGroup[numKeys]);
		
		switch(numKeys){
		case 1 : 
			keyHoles[0] = generateDefaultKeyShapes(xLoc, yLoc, Color.white);
			break;
		case 2 :
			keyHoles[0] = generateDefaultKeyShapes(xLoc-10, yLoc, Color.white);
			keyHoles[1] = generateDefaultKeyShapes(xLoc+10, yLoc, Color.white);
			break;
		case 3 :
			keyHoles[0] = generateDefaultKeyShapes(xLoc, yLoc-10, Color.white);
			keyHoles[1] = generateDefaultKeyShapes(xLoc-10, yLoc+10, Color.white);
			keyHoles[2] = generateDefaultKeyShapes(xLoc+10, yLoc+10, Color.white);
			break;
		case 4 :
			keyHoles[0] = generateDefaultKeyShapes(xLoc, yLoc-15, Color.white);
			keyHoles[1] = generateDefaultKeyShapes(xLoc-10, yLoc, Color.white);
			keyHoles[2] = generateDefaultKeyShapes(xLoc, yLoc+15, Color.white);
			keyHoles[3] = generateDefaultKeyShapes(xLoc+10, yLoc, Color.white);
			break;
		default :
			throw new IllegalArgumentException("Illegal number of key holes: " + numKeys);
		}
		
		for(int i=0; i<numKeys; i++)
			shapes.merge(keyHoles[i]);
	}

	private static ShapeGroup generateDefaultShapes(double xLoc, double yLoc){
		double xp[] = {-20,-20,20,20,0};
		double yp[] = {-30,30,30,-30,-35};
		int np = 5;
		return new ShapeGroup(new BoundingPolygon(xLoc, yLoc, np, xp, yp), GameRunner.EXIT_COLOR);
	}
	
	protected static ShapeGroup generateDefaultKeyShapes(double xLoc, double yLoc, Color color){
		List<BoundingShape> resultList = new ArrayList<BoundingShape>(2);
		
		int np1 = 5;
		double xp1[] = {0,6,2,-2,-6};
		double yp1[] = {-10,-6,0,0,-6};
		resultList.add(new BoundingPolygon(xLoc, yLoc, np1, xp1, yp1));
		
		int np2 = 4;
		double xp2[] = {2,2,-2,-2};
		double yp2[] = {0,10,10,0};
		resultList.add(new BoundingPolygon(xLoc, yLoc, np2, xp2, yp2));
		
		return new ShapeGroup(resultList, color);
	}
	
	public int getMaxKeys(){
		return maxKeys;
	}
	
	public void addKey(KeyEntity key){
		keys[currKeys] = key;
		currKeys++;
	}
	
	@Override
	public void addLink(Entity e){
		if(e instanceof KeyEntity)
			addKey((KeyEntity)e);
		else
			super.addLink(e);
	}
	
	@Override
	public boolean removeLink(Entity e){
		if(e instanceof KeyEntity)
			//TODO: make key links removable, for the sake of the map editor
			return false; //key links cannot be removed
		else
			return super.removeLink(e);
	}
	
	@Override
	public void preStep() {}

	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {}

	@Override
	public void postStep() {
		if(!hasTag("activeexit")){
			for(int i=0; i<maxKeys; i++){
				if(keys[i].isActive)
					return;
			}
			addTag("activeexit");
		}
	}

	@Override
	public void paintEntity(Graphics2D g2d, double xoffset, double yoffset, double scale){
		for(int i=0; i<maxKeys; i++){
			if(!keys[i].isActive) //this is sloppy way to handle color change, also inefficient. TODO: do it better!
				keyHoles[i].setColor(keys[i].color);
		}
		super.paintEntity(g2d, xoffset, yoffset, scale);
	}
	
}

class KeyEntity extends Entity {
	
	private static final long serialVersionUID = -7014584980766678291L;
	
	protected Color color;
	
	public KeyEntity(ShapeGroup shapes, Color color) {
		super(shapes, "key");
		this.color = color;
	}
	
	public KeyEntity(double xLoc, double yLoc, Color color){
		this(KeyedLevelExitEntity.generateDefaultKeyShapes(xLoc, yLoc, color), color);
	}

	@Override
	public void addLink(Entity e){
		if(e instanceof KeyedLevelExitEntity)
			e.addLink(this);
		else
			super.addLink(e);
	}
	
	@Override
	public boolean removeLink(Entity e){
		if(e instanceof KeyedLevelExitEntity)
			return e.removeLink(this);
		else
			return super.removeLink(e);
	}
	
	@Override
	public void preStep() {}

	@Override
	public void moveStep() {}

	@Override
	public void resolveCollision(Entity e, Vector2d cv) {
		if(e instanceof PlayerEntity){
			((PlayerEntity)e).addItem(new KeyItem(color));
			isActive = false;
		}
	}

	@Override
	public void postStep() {}
}

class KeyItem implements Item {

	public Color color;
	
	private String id;
	private String name;
	
	private static int drawN = 7;
	private static double drawX[] = {0.5, 0.8, 0.6, 0.6, 0.4, 0.4, 0.2};
	private static double drawY[] = {0.0, 0.3, 0.5, 1.0, 1.0, 0.5, 0.3};
	
	public KeyItem(String id, String name, Color color){
		this.id = id;
		this.name = name;
		this.color = color;
	}
	
	public KeyItem(Color color){
		//TODO: Make different ids / names for different color keys.. but what is good way?
		this("key", "Key", color);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void paintItem(Graphics2D g2d, int xLoc, int yLoc, int size) {
		g2d.setColor(color);
		int drawXInts[] = new int[drawN];
		int drawYInts[] = new int[drawN];
		for(int i=0; i<drawN; i++){
			drawXInts[i] = (int) (drawX[i]*size) + xLoc;
			drawYInts[i] = (int) (drawY[i]*size) + yLoc;
		}
		g2d.fillPolygon(drawXInts, drawYInts, drawN);
	}
	
	@Override
	public boolean equals(Object o){
		return ((KeyItem)o).getID().equals(this.getID());
	}
	
}
