package gamePvE;

import gamePhysics2D.Vector2d;

public class PatrollingMineEntity extends MineEntity {

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

}
