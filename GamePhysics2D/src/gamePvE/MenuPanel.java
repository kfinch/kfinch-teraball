package gamePvE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.io.File;

import javax.swing.JPanel;

public class MenuPanel extends JPanel implements KeyListener {

	protected int width;
	protected int height;
	
	private TeraBallFrontEnd parent;
	
	public MenuPanel(TeraBallFrontEnd parent){
		this.parent = parent;
		addKeyListener(this);
		setFocusable(true);
	}

	private void updateDimensions(){
		Dimension size = getSize();
		width = size.width;
		height = size.height;
	}
	
	private void doPainting(Graphics2D g2d){
		//draw menu options
		g2d.setColor(Color.red);
		g2d.setFont(new Font(Font.SANS_SERIF,Font.BOLD,(int) (Math.min(width, height) * 0.1)));
		g2d.drawString("TERABALL", (int)(width*0.1), (int)(height*0.2));
		g2d.setColor(Color.black);
		g2d.setFont(new Font(Font.SANS_SERIF,Font.BOLD,(int) (Math.min(width, height) * 0.06)));
		g2d.drawString("S - Start New Game", (int)(width*0.1), (int)(height*0.4));
		g2d.drawString("P - Preferences", (int)(width*0.1), (int)(height*0.5));
		g2d.drawString("H - High Scores", (int)(width*0.1), (int)(height*0.6));
		g2d.drawString("Q - Quit", (int)(width*0.1), (int)(height*0.7));
		
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		updateDimensions();
		doPainting((Graphics2D)g);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_S:
			parent.swapToGamePanel();
			parent.loadStage(new File("mapeditortest.tbstage"));
			parent.startSimulation();
			break;
		case KeyEvent.VK_P:
			parent.swapToPrefsPanel();
			break;
		case KeyEvent.VK_H:
			parent.swapToScoresPanel();
		case KeyEvent.VK_Q: 
			parent.quitGame();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
}
