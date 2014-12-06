package gamePvE;

import java.awt.Graphics2D;

public interface Item {

	public String getName();
	public String getID();
	public void paintItem(Graphics2D g2d, int xLoc, int yLoc, int size);
	
}
