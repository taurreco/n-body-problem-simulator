/**
 * This class establishes the force data structure used to keep 
 * track of forces between bodies in the simulation.
 * 
 * Author: Tomas L. Dougan
 * Date of last modification: 23 June 2020
 */

package nbodysim;

public class Force{
	
	private Object source;
	private Vector vector;
	
	/** Constructor: the default force.*/
	public Force(){
		this.vector = new Vector();
	}
	
	/**
	 * Constructor: a force with a custom source.
	 * 
	 * @param the source exerting the force
	 */
	public Force(Object source){
		this.source = source;
		this.vector = new Vector();
	}
	
	/**
	 * Constructor: a force with a custom vector and source
	 * 
	 * @param the source exerting the force
	 * @param the vector containing the magnitude and angle
	 *   of the force
	 */
	public Force(Object source, Vector vector){
		this.source = source;
		this.vector = vector;
	}
	
	/** Return the source of the force.*/
	public Object getSource(){
		return source;
	}
	
	/** Return the vector of the force.*/
	public Vector getVector(){
		return vector;
	}
	
	/**
	 * Change the source of this force.
	 * 
	 * @param the new source of this force
	 */
	public void setSource(Object source){
		this.source = source;
	}
	
	/**
	 * Change the vector of this force.
	 * 
	 * @param the new vector of this force
	 */
	public void setVector(Vector vector){
		this.vector = vector;
	}	
}
