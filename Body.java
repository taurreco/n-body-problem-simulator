/**
 * This class controls the information and behavior of a
 * celestial body.
 * 
 * Author: Tomas L. Dougan
 * Date of last modification: 23 June 2020
 */

package nbodysim;

import java.awt.Color;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Body {
	
	private int radius;
	private double mass;
	
	private Position position;
	
	private Vector vknot;
	private Vector velocity;
	private Vector acceleration;
	
	private Force netForce;
	
	private ArrayList<Force> forces;
	private ArrayList<Body> collidedBodies;
	private BodyPath path;
	
	/** Constructor: the default body.*/
	public Body(){
		radius = 0;
		position = new Position();
		vknot = new Vector();
		initialize();
	}
	
	/** 
	 * Constructor: a body with custom radius size and position.
	 *
	 * @param the radius of the body
	 * @param the position of the body
	 */
	public Body(int radius, Position position){
		this.radius = radius;
		this.position = position;
		this.vknot = new Vector();
		initialize();
	}
	
	/** 
	 * Constructor: a body with custom radius size, position,
	 * starting velocity.
	 * 
	 * @param the radius of the body
	 * @param the position of the body
	 * @param the initial velocity of the body
	 */
	public Body(int radius, Position position, Vector vknot){
		this.radius = radius;
		this.position = position;
		this.vknot = vknot;
		initialize();
	}
	
	/** 
	 * Initialize the default values for each class-scope field
	 * regardless of the constructor used.
	 */
	private void initialize(){
		velocity = vknot;
		mass = Math.pow((double)radius, 2);
		acceleration = new Vector();
		netForce = new Force(this); // New force with this 
			// body as the source
		forces = new ArrayList<Force>();
		collidedBodies = new ArrayList<Body>();
		// New body path with random color
		Random rand = new Random();
		path = new BodyPath(new Color(rand.nextInt(250), 
				rand.nextInt(250), rand.nextInt(250)));
	}
	
	/** Return the radius*/
	public int getRadius(){
		return radius;
	}
	
	/** Return the mass.*/
	public double getMass(){
		return mass;
	}
	
	/** Return the position.*/
	public Position getPosition(){
		return position;
	}
	
	/** Return the velocity.*/
	public Vector getVelocity(){
		return velocity;
	}
	
	/** Return the acceleration.*/
	public Vector getAcceleration(){
		return acceleration;
	}
	
	/** Return the net force.*/
	public Force getNetForce(){
		return netForce;
	}
	
	/** Return the path.*/
	public BodyPath getPath(){
		return path;
	}
	
	/** Add a position to the path.*/
	public void addToPath(Position position){
		path.add(position);
	}
	
	/** 
	 * Add the velocity vector's, adjusted with delta time, x
	 * and y components to the x and y coordinates of the 
	 * position respectively.  Do not update the coordinate 
	 * position of the body if movement in that direction is 
	 * impeded by another body.
	 */
	public void updatePosition(double deltaTime){
		// Calculate the updated positions based on the velocities
		double x = position.x + velocity.getXComponent() 
				* deltaTime;
		double y = position.y + velocity.getYComponent() 
				* deltaTime * -1;
		// Check to see that the body has collided with another
		if (collidedBodies.size() > 0){
			for (Body body : collidedBodies){
				// If the body is not impeded in the x direction
				if (position.x > body.position.x){
					/* 
					 * If the body would move away from the
					 * collided body
					 */
					if (position.x < x){
						position.x = x;
					}
					// If the body is impeded in the x direction
				} else {
					/* 
					 * If the body would move away from the
					 * collided body
					 */
					if (position.x > x){
						position.x = x;
					}
				}
				// If the body is not impeded in the y direction
				if (position.y > body.position.y){
					/* 
					 * If the body would move away from the
					 * collided body
					 */
					if (position.y < y){
						position.y = y;
					}
					// If the body is impeded in the y direction
				} else {
					/* 
					 * If the body would move away from the
					 * collided body
					 */
					if (position.y > y){
						position.y = y;
					}
				}	
			}
			/*
			 * Update position as normal provided the body has
			 * not collided with another.
			 */ 
		} else {
			position.x = x;
			position.y = y;
		}
	}
	
	/** 
	 * Add the delta-time adjusted acceleration vector to the 
	 * velocity vector.
	 */
	public void updateVelocity(double deltaTime){
		Vector adjustedAcceleration = new Vector(
				acceleration.getMagnitude() * deltaTime, 
				acceleration.getTheta());
		velocity = velocity.add(adjustedAcceleration);
	}
	
	/** 
	 * Set the acceleration equal to the net force but whose
	 * magnitude has been delta-time adjusted and divided by
	 * the body's mass.  This comes from Newton's 2nd law.
	 */
	public void updateAcceleration(double deltaTime){
		/* 
		 * Use Newton's 2nd law to derive the magnitude of 
		 * acceleration from the net force vector.
		 */
		double magnitude = netForce.getVector().getMagnitude() 
				* deltaTime / mass;
		double theta = netForce.getVector().getTheta();
		acceleration = new Vector(magnitude, theta);
	}
	
	/**
	 * For every body in the simulation, find the vector that
	 * describes the distance between this body and the other
	 * and use that to create the force vector describing the
	 * force that body exerts on this body.
	 * 
	 * @param the list of all bodies in the simulation
	 */
	public void updateForces(ArrayList<Body> bodies){
		/* 
		 * Have the list of bodies in the simulation that
		 * does not include this one.
		 */
		ArrayList<Body> otherBodies = new ArrayList<Body>();
		for (Body body : bodies){
			if (body != this){
				otherBodies.add(body);
			}
		}
		/*
		 * For every new body added to the simulation, add
		 * a corresponding force with that new body 
		 * as the source to the list of forces acting 
		 * on this body.
		 */
		while (otherBodies.size() > forces.size()){
			int i = forces.size();
			forces.add(new Force(otherBodies.get(i)));
		}
		for (int i = 0; i < otherBodies.size(); i++){
			Body otherBody = otherBodies.get(i);
			/*
			 * Get the distance vector between this body and the
			 * other body.
			 */
			Vector distanceVector = Vector.customVector(
					this.position, otherBody.position);
			double distance = distanceVector.getMagnitude();
			double theta = distanceVector.getTheta();
			/*
			 * Calculate the magnitude of the force vector using
			 * the magnitude and angle of the distance vector
			 * and Newton's law of universal gravitation.
			 */
			double magnitude = (Simulator.GRAVITATIONAL_CONSTANT
					*otherBody.mass*mass) / Math.pow(distance, 2);
			/*
			 * Update the corresponding force exerted by the other
			 * body to these values.
			 */
			forces.get(i).getVector().setMagnitude(magnitude);
			forces.get(i).getVector().setTheta(theta);
			/*
			 * Update the collision status of this body to other
			 * bodies.
			 */
			if (otherBody.radius/2 + radius/2 >= distance 
					&& !collidedBodies.contains(otherBody)){
				collidedBodies.add(otherBody);
			}else if (collidedBodies.contains(otherBody)){
				collidedBodies.remove(otherBody);
			}
		}
		updateNetForce(forces);
	}
	
	/**
	 * Sum all of the forces exerted on this body to find the
	 * net force exerted on the body.  If a force exerted on
	 * this body is from a body in collision with this one,
	 * have the net force not include that.
	 * 
	 * @param the list of all the forces exerted on this body
	 */
	private void updateNetForce(ArrayList<Force> forces){
		Force buffer = new Force();
		for (Force force : forces){
			// If the force is coming from a collided body
			if (collidedBodies.contains(force.getSource())){
				/*
				 * Add the exact opposite force to the sum
				 * so that the effects of the collided body
				 * are canceled out.
				 */
				force.getVector().setTheta(
						force.getVector().inverseTheta());
			}
			// Add the force to a buffer force
			buffer.setVector(buffer.getVector().add(
					force.getVector()));			
		}
		// Set the net force equal to the buffer
		netForce = buffer;
	}
}
