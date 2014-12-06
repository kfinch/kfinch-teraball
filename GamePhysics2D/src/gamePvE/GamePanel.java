package gamePvE;

import gamePhysics2D.BoundingAABox;
import gamePhysics2D.BoundingCircle;
import gamePhysics2D.BoundingLineSegment;
import gamePhysics2D.BoundingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.Entity;
import gamePhysics2D.Point2d;
import gamePhysics2D.Ray2d;
import gamePhysics2D.EntitySimulator;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel {
	
	protected int panelWidth, panelHeight; //size of game panel
	protected int cornerX, cornerY; //location (in stage coordinates) of the corner of the screen
	
	private String message;
	private Font messageFont;
	private Color messageColor;
	private boolean displayMessage;
	
	private GameRunner game;
	
	public GamePanel(GameRunner game){
		this.game = game;
		
		displayMessage = false;
		
		//adds listeners to receive input
		addKeyListener(game);
		addMouseListener(game);
		addMouseMotionListener(game);
		setFocusable(true);
	}
	
	public void displayCenterMessage(String message, Font messageFont, Color messageColor){
		displayMessage = true;
		this.message = message;
		this.messageFont = messageFont;
		this.messageColor = messageColor;
		repaint();
	}
	
	public void clearCenterMessage(){
		displayMessage = false;
		repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		updateDimensions();
		doPainting((Graphics2D)g);
	}
	
	private void updateDimensions(){
		panelWidth = getSize().width;
		panelHeight = getSize().height;
		cornerX = (int) (game.playerEntity.shapes.xLoc - panelWidth/2);
		cornerY = (int) (game.playerEntity.shapes.yLoc - panelHeight/2);
	}
	
	private void doPainting(Graphics2D g2d){
		//paint entities
		game.getSim().paintEntities(g2d, cornerX, cornerY, 1);
		
		//paint health bar
		g2d.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));
		g2d.setColor(GameRunner.PLAYER_COLOR);
		g2d.drawString("Health: ", 10, 20);
		g2d.setColor(GameRunner.ENEMY_COLOR);
		g2d.fillRect(67, 8, (int) game.playerEntity.maxHealth, 14);
		g2d.setColor(GameRunner.PLAYER_COLOR);
		g2d.fillRect(67, 8, (int) game.playerEntity.currHealth, 14);
		
		//paint fuel bar
		g2d.setColor(GameRunner.FUELPICKUP_COLOR);
		g2d.drawString("Fuel: ", 10, 38);
		g2d.setColor(GameRunner.PLAYER_SHIELD_COLOR);
		g2d.fillRect(67, 26, game.playerEntity.maxFuel, 14);
		g2d.setColor(GameRunner.FUELPICKUP_COLOR);
		g2d.fillRect(67, 26, game.playerEntity.currFuel, 14);
		
		//paint item bar
		g2d.setColor(GameRunner.VIOLET_KEY_COLOR);
		g2d.drawString("Items: ", 10, 56);
		int i = 67;
		int itemDrawSize = 14;
		for(Item item : game.playerEntity.getItems()){
			item.paintItem(g2d, i, 43, itemDrawSize);
			i += itemDrawSize;
		}
		
		//paint score
		g2d.setColor(GameRunner.COIN_COLOR);
		g2d.drawString(String.valueOf("Score: " + game.playerEntity.getPoints()), 10, 74);
		
		//paint center message (if needed)
		if(displayMessage){
			g2d.setFont(messageFont);
			Rectangle2D messageDims = g2d.getFontMetrics().getStringBounds(message, g2d);
			int messageHeight = (int) messageDims.getHeight();
			int messageWidth = (int) messageDims.getWidth();
			int bottomy = (panelHeight + messageHeight) / 2;
			int topy = (panelHeight - messageHeight) / 2;
			int leftx = (panelWidth - messageWidth) / 2;
			
			g2d.setColor(Color.lightGray);
			g2d.fillRect(leftx-GameRunner.MESSAGE_MARGIN, topy,
					     messageWidth+GameRunner.MESSAGE_MARGIN*2, messageHeight+GameRunner.MESSAGE_MARGIN);
			
			g2d.setColor(Color.black);
			g2d.setStroke(new BasicStroke(5));
			g2d.drawRect(leftx-GameRunner.MESSAGE_MARGIN, topy,
				     messageWidth+GameRunner.MESSAGE_MARGIN*2, messageHeight+GameRunner.MESSAGE_MARGIN);
			
			g2d.setColor(messageColor);
			g2d.drawString(message, leftx, bottomy);
		}
	}
	
}
