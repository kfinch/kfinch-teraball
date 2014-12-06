package gamePhysics2D;

public class Range2d {

	public double min;
	public double max;
	
	public Range2d(double min, double max){
		this.min = min;
		this.max = max;
	}
	
	public boolean inRangeInclusive(double x){
		return (x >= min) && (x <= max);
	}
	
}
