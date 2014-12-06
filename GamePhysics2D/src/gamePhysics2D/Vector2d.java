package gamePhysics2D;

/**
 * A representation of a vector in 2D space.
 * Generates the x and y magnitude from angle and absolute magnitude.
 * angle is in radians.
 * 
 * @author Kelton Finch
 */
public class Vector2d {
	
	public double x, y;
	
	public Vector2d(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Vector2d(){
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2d(Vector2d v){
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector2d(Point2d s, Point2d f){
		this.x = f.x - s.x;
		this.y = f.y - s.y;
	}
	
	/**
	 * Compute vector's angle with respect to the origin
	 * @return vector's angle, in radians
	 */
	public double angle(){
		return Math.atan2(y, x);
	}
	
	/**
	 * Compute vector's magnitude
	 * @return vector's magnitude
	 */
	public double magnitude(){
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	/**
	 * Generates a new x and y from a given angle and magnitude.
	 * x and y are modified as a side effect of this method.
	 * @param angle The new vector's angle
	 * @param magnitude The new vector's magnitude
	 */
	public void setAngleAndMagnitude(double angle, double magnitude){
		x = magnitude * Math.cos(angle);
		y = magnitude * Math.sin(angle);
	}
	
	public void setAngle(double angle){
		setAngleAndMagnitude(angle, magnitude());
	}
	
	public void setMagnitude(double magnitude){
		setAngleAndMagnitude(angle(), magnitude);
	}
	
	/**
	 * Adds another vector to this one.
	 * @param v The vector to be added.
	 */
	public void add(Vector2d v){
		x += v.x;
		y += v.y;
	}
	
	/**
	 * Subtracts another vector from this one.
	 * @param v The vector to be subtracted.
	 */
	public void subtract(Vector2d v){
		x -= v.x;
		y -= v.y;
	}
	
	/**
	 * Scalar multiplies this vector
	 * @param mult The scalar to multiply this vector by
	 */
	public void multiply(double mult){
		x *= mult;
		y *= mult;
	}
	
	/**
	 * Computes the dot product of this vector with a supplied vector
	 * @param v The other vector to use in the dot product
	 * @return The dot product of this vector with v
	 */
	public double dot(Vector2d v){
		return (x * v.x) + (y * v.y);
	}
	
	/**
	 * Normalizes this vector. (Turns it into a unit vector with the same angle)
	 */
	public void normalize(){
		double mag = magnitude();
		x = x/mag;
		y = y/mag;
	}
	
	/**
	 * Provides a normalized version of this vector. (A unit vector with same angle as this vector)
	 * @return A normalized version of this vector.
	 */
	public Vector2d normalVector(){
		Vector2d result = new Vector2d(this);
		result.normalize();
		return result;
	}
	
	public Vector2d vectorProjection(double angle){
		Vector2d result = new Vector2d();
		double resultMag = magnitude() * Math.cos(angle() - angle);
		result.setAngleAndMagnitude(angle, resultMag);
		return result;
	}
	
	public Vector2d vectorRejection(double angle){
		Vector2d result = new Vector2d(this);
		result.subtract(vectorProjection(angle));
		return result;
	}
	
	/**
	 * Gives the smallest angle required to get from angle start to angle finish
	 * @param start The starting angle (between -pi and pi)
	 * @param finish The finishing angle (between -pi and pi)
	 * @return The smallest angle that can be added to start to get to finish (between -pi and pi)
	 */
	public static double deltaAngle(double start, double finish){
		double result = finish - start;
		double crossUp = finish - start - 2*Math.PI;
		double crossDown = finish - start + 2*Math.PI;
		if(Math.abs(result) > Math.abs(crossUp))
			result = crossUp;
		if(Math.abs(result) > Math.abs(crossDown))
			result = crossDown;
		return result;
	}
	
	@Override
	public String toString(){
		return "{" + x + "," + y + "}";
	}
}
