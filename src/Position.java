/**
 * This class establishes the position data structure
 * used to keep track of points in space in the simulation.
 * 
 * Author: Tomas L. Dougan
 * Date of last modification: 23 June 2020
 */

package nbodysim;

class Position{
	
	// Coordinates
	public double x, y;
	
	/** Constructor: the default position.*/
	public Position(){
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * Constructor: the position with custom coordinates.
	 * 
	 * @param the x coordinate
	 * @param the y coordinate
	 */
	public Position(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Distinguish between positions and whether they are the
	 * same.
	 * 
	 * @param another position
	 */
	public boolean equals(Position position){
		if (this.x == position.x && this.y == position.y){
			return true;
		}
		return false;
	}
}
