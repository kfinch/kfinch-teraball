package gamePvE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import gamePhysics2D.BoundingLineSegment;
import gamePhysics2D.BoundingPolygon;
import gamePhysics2D.BoundingShape;
import gamePhysics2D.ShapeGroup;
import gamePhysics2D.Vector2d;

public class PatrollingMineEntity extends MineEntity {

	private static final long serialVersionUID = -3113288558289825940L;
	
	int nWaypoints;
	double xWaypoints[];
	double yWaypoints[];
	
	double patrolSpeed;
	
	int currentSegment;
	Vector2d currentVelocity;
	
	public PatrollingMineEntity(int nWaypoints, double xWaypoints[], double yWaypoints[], double patrolSpeed) {
		super(xWaypoints[0], yWaypoints[0]);
		this.nWaypoints = nWaypoints;
		this.xWaypoints = xWaypoints;
		this.yWaypoints = yWaypoints;
		this.patrolSpeed = patrolSpeed;
		currentSegment = 1;
		setVelocityVector();
	}
	
	public PatrollingMineEntity(int nWaypoints, double xWaypoints[], double yWaypoints[]) {
		this(nWaypoints, xWaypoints, yWaypoints, GameRunner.MINE_PATROL_SPEED);
	}
	
	private void setVelocityVector(){
		int srcIndex = currentSegment-1;
		if(srcIndex == -1)
			srcIndex = nWaypoints-1;
		Vector2d velocity = new Vector2d(xWaypoints[currentSegment] - xWaypoints[srcIndex],
				                         yWaypoints[currentSegment] - yWaypoints[srcIndex]);
		velocity.setMagnitude(patrolSpeed);
		currentVelocity = velocity;
	}
	
	@Override
	public void moveStep() {
		Vector2d distanceToWaypoint = new Vector2d(xWaypoints[currentSegment] - shapes.xLoc,
				                                   yWaypoints[currentSegment] - shapes.yLoc);
		if(distanceToWaypoint.magnitude() - patrolSpeed <= 0){
			shapes.translate(distanceToWaypoint);
			currentSegment++;
			if(currentSegment == nWaypoints)
				currentSegment = 0;
			setVelocityVector();
		}
		else{
			shapes.translate(currentVelocity);
		}
	}
	
	@Override
	public void paintAdditionalEntityInfo(Graphics2D g2d, double xoffset, double yoffset, double scale){
		Graphics2D gcopy = (Graphics2D) g2d.create();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
		gcopy.setStroke(dashed);
		gcopy.setColor(Color.red);
		
		List<BoundingShape> patrolLines = new ArrayList<BoundingShape>(nWaypoints);
		for(int i=0; i<nWaypoints; i++){
			int j = (i+1==nWaypoints) ? 0 : i+1;
			patrolLines.add(new BoundingLineSegment(xWaypoints[i], yWaypoints[i], xWaypoints[j], yWaypoints[j]));
		}
		ShapeGroup paint = new ShapeGroup(patrolLines, null);
		paint.paintShapes(gcopy, xoffset, yoffset, scale);
	}

}
