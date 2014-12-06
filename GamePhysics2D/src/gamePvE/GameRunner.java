package gamePvE;

import gamePhysics2D.EntityAdder;
import gamePhysics2D.EntitySimulator;
import gamePhysics2D.Point2d;
import gamePhysics2D.Vector2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GameRunner implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
	
	public static final Color PLAYER_COLOR = Color.decode("#119999");
	
	public static final Color TERRAIN_COLOR = Color.decode("#333333");
	public static final Color ENEMY_COLOR = Color.decode("#bb7777");
	public static final Color COIN_COLOR = Color.decode("#bbbb00");
	public static final Color EXIT_COLOR = Color.decode("#55bbff");
	public static final Color FUELPICKUP_COLOR = Color.decode("#050599");
	
	public static final Color PLAYER_SHIELD_COLOR = Color.decode("#77ccff");
	
	public static final Color ENEMY_SHOT_COLOR = Color.decode("#ff5555");
	public static final Color ENEMY_MISSILE_COLOR = Color.decode("#aa0000");
	public static final Color ENEMY_MISSILE_TRAIL_COLOR = Color.decode("#ff5555");
	
	public static final Color UNPRESSED_BUTTON_COLOR = Color.decode("#bb6644");
	public static final Color PRESSED_BUTTON_COLOR = Color.decode("#ff8866");
	
	public static final Color OFF_WIRE_COLOR = Color.decode("#ffbbff");
	public static final Color ON_WIRE_COLOR = Color.decode("#dd99bb");
	
	public static final Color CLOSED_GATE_COLOR = Color.decode("#336633");
	public static final Color OPEN_GATE_COLOR = Color.decode("#ccffcc");
	
	public static final Color GREEN_KEY_COLOR = Color.decode("#229922");
	public static final Color BLUE_KEY_COLOR = Color.decode("#222299");
	public static final Color INDIGO_KEY_COLOR = Color.decode("#440099");
	public static final Color VIOLET_KEY_COLOR = Color.decode("#6600aa");
	
	
	public static final double STAGE_WALL_THICKNESS = 1000;
	
	public static final double PLAYER_MAX_HEALTH = 100;
	public static final double PLAYER_SIZE = 15;
	public static final double PLAYER_MASS = 100;
	public static final double PLAYER_BOUNCE = 0.25;
	public static final double PLAYER_ACCEL = 0.15;
	public static final double PLAYER_MAX_SPEED = 7;
	public static final double PLAYER_FRICTION = 0.03;
	
	public static final double PLAYER_THRUSTER_POWER = 0.5;
	public static final int PLAYER_THRUSTER_MAX_FUEL = 100;
	
	public static final int STARTING_POINTS = 800;
	public static final int POINT_DRAIN_RATE = 1;
	
	public static final double MINE_DAMAGE = 10;
	public static final double MINE_KNOCKBACK = 4;
	public static final double MINE_PATROL_SPEED = 3;
	
	public static final double TURRET_TURN_SPEED = 0.04;
	public static final int TURRET_GUN_CD = 30;
	
	public static final double TURRET_SHOT_DAMAGE = 10;
	public static final double TURRET_SHOT_KNOCKBACK = 2;
	public static final double TURRET_SHOT_SPEED = 7;
	public static final double TURRET_SHOT_SIZE = 4;
	
	public static final int TURRET_MISSILE_CD = 60;
	public static final double TURRET_MISSILE_DAMAGE = 30;
	public static final double TURRET_MISSILE_KNOCKBACK = 3;
	public static final double TURRET_MISSILE_SPEED = 3;
	public static final double TURRET_MISSILE_SIZE = 6;
	public static final double TURRET_MISSILE_TURN_SPEED = 0.07;
	public static final double TURRET_MISSILE_TURN_SPEED_DEGRADE = 0.0005;
	public static final double TURRET_MISSILE_ACCEL = 0.05;
	public static final int TURRET_MISSILE_CHANGE_DURATION = 100;
	
	public static final double BUTTON_SIZE = 10;
	
	public static final double REG_COIN_SIZE = 4;
	public static final int REG_COIN_VALUE = 100;
	
	public static final double BIG_COIN_SIZE = 8;
	public static final int BIG_COIN_VALUE = 400;
	
	public static final double FUELPICKUP_XSIZE = 5;
	public static final double FUELPICKUP_YSIZE = 7;
	public static final int FUELPICKUP_VALUE = 40;
	
	
	public static final int MESSAGE_MARGIN = 10;
	
	
	private static final int STEP_DURATION = 30; //Change to ~30 for exec jar packing. Eclipse run is slower )=
	                                             //to ~25 for running eclipse
	
	private Timer gameClock; //game clock handles the game's "ticks"
	
	private EntitySimulator entitySim; //runs the entity simulation, stepping entities and resolving collisions
	private EntityAdder adder;
	
	public PlayerEntity playerEntity; //Entity object representing the player
	
	protected GamePanel display; //JPanel displays the game
	
	protected File currentStage; //stage file of current level
	protected File nextStage; //stage file of next level (specified by current level, null if there is no next level)
	
	private TeraBallFrontEnd parent; //pointer to parent JFrame, allows for view swapping when game over
	
	private Queue<PauseMessageInfo> pauseMessageQueue;
	
	private boolean isPaused;
	
	//tracks player orders
	private boolean playerAccelUp;
	private boolean playerAccelDown;
	private boolean playerAccelLeft;
	private boolean playerAccelRight;
	private boolean leftMouseDragging;
	private boolean rightMouseDragging;
	
	//the mouse's location
	private Point2d mouseLoc;
	
	public GameRunner(TeraBallFrontEnd parent){
		this.parent = parent;
		
		playerAccelUp = false;
		playerAccelDown = false;
		playerAccelLeft = false;
		playerAccelRight = false;
		leftMouseDragging = false;
		rightMouseDragging = false;
		
		mouseLoc = new Point2d(0,0);
		
		pauseMessageQueue = new LinkedList<PauseMessageInfo>();
		
		isPaused = false;
		
		//initialize game clock
		gameClock = new Timer(STEP_DURATION, this);
		
		//initialize game panel (JPanel that game is actually displayed to)
		display = new GamePanel(this);
		
		//initialize simulator with entity categories and interaction info
		//TODO: I'm not sure if it actually makes sense to hardcode this in. Investigate other options.
		entitySim = new EntitySimulator();
		adder = new EntityAdder(entitySim);
				
		entitySim.addCategory("nocollide");  //non colliding entities, like cosmetic items
		entitySim.addCategory("playeritems"); //items only the player interacts with, like powerups
		entitySim.addCategory("terrain"); //terrain, stationary hazards, etc
		entitySim.addCategory("projectiles"); //projectiles like enemy attacks, missiles, etc.
		entitySim.addCategory("mobs"); //moving or otherwise changing entities
		entitySim.addCategory("player"); //the player
		entitySim.addCategory("los"); //(invisible) line of sight checking entities
				
		entitySim.newInteraction("player", "playeritems");
				
		entitySim.newInteraction("player", "terrain");
		entitySim.newInteraction("projectiles", "terrain");
		entitySim.newInteraction("mobs", "terrain");
		entitySim.newInteraction("los", "terrain");
				
		entitySim.newInteraction("player", "projectiles");
		entitySim.newInteraction("mobs", "projectiles");
				
		entitySim.newInteraction("mobs", "mobs");
		entitySim.newInteraction("player", "mobs");
		entitySim.newInteraction("los", "mobs");
				
		entitySim.newInteraction("los", "player");
	}
	
	public void loadStage(File stageFile){
		//TODO: catch here, or propagate one more level up?
		try {
			entitySim.clear(); //clear any existing entities
			MapCoder.decodeMapFile(stageFile, this); //read map file to load new entities
			currentStage = stageFile;
		} catch (FileNotFoundException e) {
			System.out.println("file not found!");
		}
	}
	
	public EntitySimulator getSim(){
		return entitySim;
	}
	
	public EntityAdder getAdder(){
		return adder;
	}

	/**
	 * Starts the game. A level must be loaded for this to work properly.
	 */
	public void restartGameClock(){
		//starts (or restarts) the game clock
		gameClock.restart();
	}
	
	public void restartStage(){
		loadStage(currentStage);
		restartGameClock();
		pauseAndMessage("3", new Font(Font.SANS_SERIF,Font.BOLD,40), Color.red, 600);
		pauseAndMessage("2", new Font(Font.SANS_SERIF,Font.BOLD,40), Color.red, 600);
		pauseAndMessage("1", new Font(Font.SANS_SERIF,Font.BOLD,40), Color.red, 600);
	}
	
	public void nextStage(){
		loadStage(nextStage);
		restartGameClock();
		pauseAndMessage("3", new Font(Font.SANS_SERIF,Font.BOLD,40), Color.red, 600);
		pauseAndMessage("2", new Font(Font.SANS_SERIF,Font.BOLD,40), Color.red, 600);
		pauseAndMessage("1", new Font(Font.SANS_SERIF,Font.BOLD,40), Color.red, 600);
	}
	
	public void quitToMenu(){
		gameClock.stop();
		parent.swapToMenuPanel();
	}
	
	public void pause(){
		display.displayCenterMessage("Paused", new Font(Font.SANS_SERIF,Font.BOLD,24), Color.red);
		isPaused = true;
		gameClock.stop();
	}
	
	public void unpause(){
		display.clearCenterMessage();
		isPaused = false;
		gameClock.restart();
	}
	
	public void pauseAndMessage(String message, Font messageFont, Color messageColor, int pauseDuration){
		pauseMessageQueue.add(new PauseMessageInfo(message, messageFont, messageColor, pauseDuration));
	}
	
	/**
	 * Moves the simulation one step forward. This is automatically called by the game clock.
	 */
	private void step(){
		while(!pauseMessageQueue.isEmpty()){
			PauseMessageInfo current = pauseMessageQueue.peek();
			
			if(current.messageDuration <= 0){
				pauseMessageQueue.poll();
				display.clearCenterMessage();
				continue;
			}
			
			display.displayCenterMessage(current.message, current.messageFont, current.messageColor);
			current.messageDuration -= STEP_DURATION;
			
			return;
		}
		
		//apply player input accelerations
		playerEntity.playerAccelerate(playerAccelUp, playerAccelDown, playerAccelLeft, playerAccelRight);
		
		//apply player thrust
		if(leftMouseDragging){
			Vector2d thrustVector = new Vector2d(mouseLoc.x + display.cornerX - playerEntity.shapes.xLoc,
					                             mouseLoc.y + display.cornerY - playerEntity.shapes.yLoc);
			double direction = thrustVector.angle();
			playerEntity.playerThrust(direction);
		}
		else
			playerEntity.isThrusting = false; //TODO: better way to do this?
		
		//apply player shield
		if(rightMouseDragging){
			Vector2d shieldVector = new Vector2d(mouseLoc.x + display.cornerX - playerEntity.shapes.xLoc,
                    							 mouseLoc.y + display.cornerY - playerEntity.shapes.yLoc);
			double direction = shieldVector.angle();
			playerEntity.playerShield(direction);
		}
		else{
			playerEntity.isShielding = false;
			playerEntity.playerDropShield();
		}
		
		//step the entity simulation
		entitySim.step();
		
		//order the display to update and refresh
		display.repaint();
		
		if(playerEntity.currHealth <= 0) //player lose
			restartStage();
		
		if(playerEntity.exited){ //player win
			if(nextStage == null)
				quitToMenu();
			else
				nextStage();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		step();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseLoc.x = e.getX();
		mouseLoc.y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e))
			leftMouseDragging = true;
		if(SwingUtilities.isRightMouseButton(e))
			rightMouseDragging = true;
		mouseLoc.x = e.getX();
		mouseLoc.y = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e))
			leftMouseDragging = false;
		if(SwingUtilities.isRightMouseButton(e))
			rightMouseDragging = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_W: case KeyEvent.VK_UP: playerAccelUp = true; break;
		case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: playerAccelRight = true; break;
		case KeyEvent.VK_S: case KeyEvent.VK_DOWN: playerAccelDown = true; break;
		case KeyEvent.VK_A: case KeyEvent.VK_LEFT: playerAccelLeft = true; break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_W: case KeyEvent.VK_UP: playerAccelUp = false; break;
		case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: playerAccelRight = false; break;
		case KeyEvent.VK_S: case KeyEvent.VK_DOWN: playerAccelDown = false; break;
		case KeyEvent.VK_A: case KeyEvent.VK_LEFT: playerAccelLeft = false; break;
		case KeyEvent.VK_BACK_SPACE: restartStage(); break;
		case KeyEvent.VK_ESCAPE: quitToMenu(); break;
		case KeyEvent.VK_P:
			if(isPaused)
				unpause();
			else
				pause();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}

class PauseMessageInfo {
	public String message;
	public Font messageFont;
	public Color messageColor;
	public int messageDuration;
	
	public PauseMessageInfo(String message, Font messageFont, Color messageColor, int messageDuration){
		this.message = message;
		this.messageFont = messageFont;
		this.messageColor = messageColor;
		this.messageDuration = messageDuration;
	}
}
