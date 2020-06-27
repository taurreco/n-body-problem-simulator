/**
 * This class handles the vector data structure, its fields,
 * and operations.
 * 
 * Author: Tomas L. Dougan
 * Date of last modification: 22 June 2020
 */

package nbodysim;

import java.lang.Math;

public class Vector {
	
	private final double PI = Math.PI;
	
	private double magnitude;
	private double theta; // Measured in radians
	private double xComponent;
	private double yComponent;
	
	/** Constructor: the default vector.*/
	public Vector(){
		magnitude = 0;
		theta = 0;
		setComponents();
	}
	
	/** 
	 * Constructor: a vector with custom magnitude and angle.
	 * 
	 * @param the magnitude of the vector
	 * @param the angle of the vector in radians
	 */
	public Vector(double magnitude, double theta){
		this.magnitude = magnitude;
		this.theta = theta;
		setComponents();
	}
	
	/**
	 * Call the getComponents method to initialize the x and
	 * y components.
	 */
	private void setComponents(){
		xComponent = getComponents(magnitude, theta)[0];
		yComponent = getComponents(magnitude, theta)[1];
	}
	
	/**
	 * Use trigonometric functions sin and cosine to find the
	 * size of the x and y components given the magnitude and
	 * angle.  If the angle is one of four cardinal angles where
	 * one component is equal to the entirety of the magnitude,
	 * set the component values without calculation.
	 * 
	 * @param the magnitude of the vector
	 * @param the angle of the vector
	 * @return an array with the x component in the first
	 *   element, and the y component in the second element
	 */
	private double[] getComponents(double magnitude, 
			double theta){
		double[] components = new double[2];
		// Positive horizontal angle
		if (theta > 0 && theta % 2*PI == 0 || theta < 0 
				&& theta % PI == 0 || theta == 0){
			components[0] = magnitude * 1;
			components[1] = magnitude * 0;
			return components;
		}
		// Negative horizontal angle
		if (theta > 0 && theta % PI == 0 || theta < 0 
				&& theta % 2*PI == 0){
			components[0] = magnitude * -1;
			components[1] = magnitude * 0;
			return components;
		}
		// Positive vertical angle
		if (theta > 0 && theta % PI/2 == 0 && theta % PI != 0 
				|| theta < 0 && theta % 3*PI/2 == 0){
			components[0] = magnitude * 0;
			components[1] = magnitude * 1;
			return components;
		}
		// Negative vertical angle
		if (theta > 0 && theta % 3*PI/2 == 0 || theta < 0 
				&& theta % PI/2 == 0  && theta % PI != 0){
			components[0] = magnitude * 0;
			components[1] = magnitude * -1;
			return components;
		}
		// Trig calculations
		components[0] = magnitude * (Math.floor(Math.cos(theta) 
				* 1000) / 1000);
		components[1] = magnitude * (Math.floor(Math.sin(theta) 
				* 1000) / 1000);
		return components;
	}
	
	/** Return the magnitude.*/
	public double getMagnitude(){
		return magnitude;
	}
	
	/** Return the theta.*/
	public double getTheta(){
		return theta;
	}
	
	/** Return the x component.*/
	public double getXComponent(){
		return xComponent;
	}
	
	/** Return the y component.*/
	public double getYComponent(){
		return yComponent;
	}
	
	/** 
	 * Set the magnitude equal to the value passed to the method
	 * and then reset the components to reflect the new magnitude.
	 * 
	 * @param the new magnitude
	 */
	public void setMagnitude(double magnitude){
		this.magnitude = magnitude;	
		this.setComponents();
	}
	
	/** 
	 * Set the theta equal to the value passed to the method
	 * and then reset the components to reflect the new angle.
	 * 
	 * @param the new theta
	 */
	public void setTheta(double theta){
		this.theta = theta;
		this.setComponents();
	}
	
	/** 
	 * Use the components of this vector and another vector to
	 * find the sum of the two vectors by adding the components
	 * and recalculating the magnitude and angle.
	 * 
	 * @param the vector to be summed with this one
	 * @return the vector whose magnitude and angle represent the
	 *   sum of the magnitudes and angles of this vector and
	 *   another
	 */
	public Vector add(Vector vector){
		// Sum the components
		double xComponent = this.xComponent + vector.xComponent;
		double yComponent = this.yComponent + vector.yComponent;
		// Recalculate magnitude and angle
		double magnitude = calculateMagnitude(xComponent, 
				yComponent);
		double theta = calculateTheta(xComponent, yComponent,
				magnitude);
		// Account for errors that can happen when values are 0
		if (vector.theta == this.theta && vector.theta == 0){
			theta = 0;
		}
		if(vector.magnitude == 0){
			return this;
		}
		return new Vector(magnitude, theta);
	}
	
	/** Return the angle that is opposite to that of this vector.*/
	public double inverseTheta(){		
		return calculateTheta(xComponent * -1, yComponent * -1,
				magnitude);
	}
	
	/**
	 * Find the vector that represents the relationship between
	 * two points by making use of the calculation methods.
	 *
	 * @param the position of the origin of the vector
	 * @param the position of the end of the vector
	 * @return a vector describing the magnitude (or distance)
	 *   between the position, and the angle they form above
	 *   the horizontal
	 */
	public static Vector customVector(Position source, 
			Position target){
		/*
		 *  Calculate the components of the vector using the
		 *  coordinates of the positions.
		 */
		double xComponent = target.x - source.x;
		double yComponent = (target.y - source.y) * -1;
		// Calculate the magnitude and angle of the vector
		double magnitude = calculateMagnitude(xComponent, 
				yComponent);
		double theta = calculateTheta(xComponent, yComponent, 
				magnitude);
		return new Vector(magnitude, theta);
	}
	
	/** 
	 * Return the magnitude given two components using 
	 * pythagoras's theorem.
	 *
	 * @param the x component of the vector
	 * @param the y component of the vector
	 * @return the magnitude of the vector
	 */
	private static double calculateMagnitude(double xComponent, 
			double yComponent){
		return  Math.sqrt(Math.pow(xComponent, 2) 
				+ Math.pow(yComponent, 2));
	}
	
	/** 
	 * Return the angle given the components and magnitude using
	 * the trigonometric function arc cosine.
	 *
	 * @param the x component of the vector
	 * @param the y component of the vector
	 * @param the magnitude of the vector
	 * @return the angle, theta, of the vector
	 */
	private static double calculateTheta(double xComponent, 
			double yComponent, double magnitude){
		// If the angle is above the horizontal
		if (yComponent < 0){
			return Math.acos(xComponent/magnitude) * -1;
		}
		// If the angle is below the horizontal
		return Math.acos(xComponent/magnitude);
	}
}
