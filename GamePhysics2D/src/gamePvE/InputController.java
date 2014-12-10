package gamePvE;

import gamePhysics2D.Point2d;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

/**
 * Receives and passes on input from the player.
 * 
 * This class currently not used.
 * TODO: remove?
 * 
 * @author Kelton Finch
 */
public class InputController implements KeyListener, MouseListener, MouseMotionListener {

	protected Point2d mouseLoc;
	protected boolean leftMouseDragging;
	protected boolean rightMouseDragging;
	protected boolean playerAccelUp;
	protected boolean playerAccelDown;
	protected boolean playerAccelLeft;
	protected boolean playerAccelRight;
	
	public InputController(){
		mouseLoc = new Point2d(0,0);
		leftMouseDragging = false;
		rightMouseDragging = false;
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
		case KeyEvent.VK_BACK_SPACE: break; //TODO: handle level controls
		case KeyEvent.VK_1: break;
		case KeyEvent.VK_2: break;
		case KeyEvent.VK_3: break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

}
