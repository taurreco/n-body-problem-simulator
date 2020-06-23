/**
 * This class is used to hold the graphics logic behind the
 * simulation-- how, where, and when lines are drawn to the 
 * screen
 * 
 * Author: Tomas L. Dougan
 * Date of last modification: 22 June 2020
 */

package nbodysim;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

public class SimPanel extends JPanel{
	
	// Constants
	private final int WIDTH;
	private final int HEIGHT;
	
	// Lists
	private ArrayList<Body> bodies;
	private ArrayList<Position> newBodyPositions;
	private ArrayList<Position> mousePositions;
	
	// Booleans
	private boolean isAddingBody;
	private boolean isTracingPaths;
	private boolean isColoringPaths;
	private boolean isPaused;
	private boolean isShowingNetForces;
	
	/** 
	 * Constructor: initialize class-scope declared fields
	 * and takes in a custom height and width.
	 */
	SimPanel(int WIDTH, int HEIGHT){
		// Dimensions
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		// Initializations
		bodies = new ArrayList<>();
		newBodyPositions = new ArrayList<>();
		mousePositions = new ArrayList<>();
		
		isAddingBody = false;
		isTracingPaths = false;
		isPaused = false;
		isColoringPaths = false;
		isShowingNetForces = false;
	}
	
	/**
	 * Use the information set by the update method to draw
	 * the correct lines to the screen.
	 */
	@Override
	public void paintComponent(Graphics g){
		// Delete lines from the previous frame
		g.clearRect(0, 0, WIDTH, HEIGHT);
		for (Body body : bodies){
			// Draw each body
			int x = (int)body.getPosition().x 
					- (body.getRadius()/2);
			int y = (int)body.getPosition().y 
					- (body.getRadius()/2);
			int x1 = (int)body.getPosition().x;
			int y1 = (int)body.getPosition().y;
			g.drawOval(x, y, body.getRadius(), 
					body.getRadius());
			Force force = body.getNetForce();
			int	x2 = (int)(x1 
					+ (force.getVector().getMagnitude() * 1) 
					* Math.cos(force.getVector().getTheta()));
			int	y2 = (int)(y1
					+ (force.getVector().getMagnitude() * 1) 
					* Math.sin(force.getVector().getTheta()) * -1);
			/*
			 * Draw the line indicating net force exerted on each
			 * body.
			 */
			if (isShowingNetForces){
				g.drawLine(x1, y1, x2, y2);
			}
			if (isTracingPaths){
				BodyPath path = body.getPath();
				// Draw the paths
				for (int i = 0; i < path.list.size(); i++){
					// Set the color
					if(isColoringPaths){
						g.setColor(path.getColor());
					}
					g.drawLine((int)path.list.get(i).x, 
							(int)path.list.get(i).y, 
							(int)path.list.get(i).x, 
							(int)path.list.get(i).y);
				}
			}
			// Reset the color in case the paths are colored
			g.setColor(Color.BLACK);
		}
		if (isAddingBody){
			if (newBodyPositions.size() > 0){
				/*
				 * Draw the lines which follow the mouse when
				 * creating new bodies.
				 */
				g.drawLine((int)newBodyPositions.get(0).x, 
						(int)newBodyPositions.get(0).y, 
						(int)mousePositions.get(0).x, 
						(int)mousePositions.get(0).y);
			}
		}
		if (isPaused){
			/*
			 * Maintain new body lines when the simulation is
			 * paused.
			 */
			for (int i = 0; i < newBodyPositions.size(); i++){
				g.drawLine((int)newBodyPositions.get(i).x, 
						(int)newBodyPositions.get(i).y, 
						(int)mousePositions.get(i).x, 
						(int)mousePositions.get(i).y);
			}
		}
	}
	
	/**
	 * Receive and match the values passed through the method
	 * so that they are in sync with the simulation.
	 * 
	 * @param the list of bodies in the simulation
	 * @param the list of new body positions
	 * @param the list of mouse position
	 * @param whether a new body is being added
	 * @param whether the simulation is tracing paths
	 * @param whether the paths are colored
	 * @param whether the net forces are displayed
	 */
	public void update(ArrayList<Body> bodies, 
			ArrayList<Position> newBodyPositions, 
			ArrayList<Position> mousePositions, boolean isAddingBody, 
			boolean isTracingPaths, boolean isColoringPaths, 
			boolean isShowingNetForces){
		this.bodies = bodies;
		this.newBodyPositions = newBodyPositions;
		this.mousePositions = mousePositions;
		this.isAddingBody = isAddingBody;
		this.isTracingPaths = isTracingPaths;
		this.isColoringPaths = isColoringPaths;
		this.isShowingNetForces = isShowingNetForces;
	}
	
	/** Set isPaused to true.*/
	public void pause(){
		isPaused = true;
	}
	
	/** Set isPaused to false.*/
	public void unpause(){
		isPaused = false;
	}
}
