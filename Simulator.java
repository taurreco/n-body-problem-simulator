/**
 * This class represents the entire n-body simulation.  So,
 * every instance of the simulator will keep track of every
 * individual body in that simulation-- updating them at every 
 * frame-- with a user interface that allows for the manipulation
 * of the bodies and the way they are drawn to the screen.
 * 
 * The simulator is a child of the simulation loop, and so
 * is runnable and will use a thread to execute the loop
 * while other processes are handled separately.
 * 
 * Author: Tomas L. Dougan
 * Date of last modification: 23 June 2020
 */

package nbodysim;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.Math;

import javax.swing.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Simulator extends SimLoop{
	
	// Constants
	public final static double GRAVITATIONAL_CONSTANT = 1;
	
	private final Color COLOR_SIMPANEL = Color.WHITE;
	private final Color COLOR_GUI = Color.LIGHT_GRAY;
	
	private final Font FONT = new Font("Serif", Font.PLAIN, 14);
	private final Font FONT_BOLD = new Font("Serif", Font.BOLD, 14);
	
	private final int BODY_LIMIT = 25;
	private final int WIDTH = 1200;
	private final int HEIGHT = 750;
	
	// Containers
	private JFrame frame;
	private SimPanel simPanel;
	private JPanel gui;
	
	// Buttons
	private JButton radiusButton;
	private JButton taperedLengthButton;
	private JButton resetButton;
	
	// Check-boxes
	private JCheckBox togglePathTrace;
	private JCheckBox togglePathInterpolate;
	private JCheckBox togglePathTaper;
	private JCheckBox togglePathColors;
	private JCheckBox toggleNetForces;
	
	// Text-fields
	private JTextField radiusField;
	private JTextField taperedLengthField;
	
	// Labels
	private JLabel radiusValueLabel;
	private JLabel radiusTextLabel;
	private JLabel pausedLabel;
	private JLabel numberOfBodiesLabel;
	private JLabel taperedLengthValueLabel;
	private JLabel taperedLengthTextLabel;
	
	// Array-lists
	private ArrayList<Body> bodies;
	private ArrayList<Position> mousePositions;
	private ArrayList<Position> newBodyPositions;
	
	// New-body fields
	private Vector newBodyVknot;
	private Position newBodyPosition;
	private Position mousePosition;
	
	// Booleans
	private boolean isAddingBody;
	private boolean isTracingPaths;
	private boolean isInterpolatingPaths;
	private boolean isTaperingPaths;
	private boolean isColoringPaths;
	private boolean isShowingNetForces;
	
	// Misc. booleans
	private boolean isPaused;
	private boolean isUpdating;
	
	private int taperedLength;
	
	private SimListener simListener;
	
	/**
	 * Constructor: initialize class-scope declared fields, and 
	 * sets up basic conditions for a clean user interface.
	 */
	public Simulator(){
		// Check-boxes initialization and set up
		toggleNetForces = new JCheckBox("Show net forces");
		toggleNetForces.addActionListener(new ToggleListener());
		toggleNetForces.setFont(FONT);
		toggleNetForces.setBackground(COLOR_GUI);
				
		togglePathTrace = new JCheckBox("Trace Paths");
		togglePathTrace.addActionListener(new ToggleListener());
		togglePathTrace.setFont(FONT);
		togglePathTrace.setBackground(COLOR_GUI);
		
		togglePathInterpolate = new JCheckBox("Interpolate Paths");
		togglePathInterpolate.setVisible(false);
		togglePathInterpolate.addActionListener(new ToggleListener());
		togglePathInterpolate.setFont(FONT);
		togglePathInterpolate.setBackground(COLOR_GUI);
		
		togglePathTaper = new JCheckBox("Taper Paths");
		togglePathTaper.setVisible(false);
		togglePathTaper.addActionListener(new ToggleListener());
		togglePathTaper.setFont(FONT);
		togglePathTaper.setBackground(COLOR_GUI);
		
		togglePathColors = new JCheckBox("Color Paths");
		togglePathColors.setVisible(false);
		togglePathColors.addActionListener(new ToggleListener());
		togglePathColors.setFont(FONT);
		togglePathColors.setBackground(COLOR_GUI);
		
		// Misc. label initialization and set up
		numberOfBodiesLabel = new JLabel("Bodies: 0/25");
		numberOfBodiesLabel.setFont(FONT);
		
		pausedLabel = new JLabel("[Paused] \n");
		pausedLabel.setVisible(false);
		pausedLabel.setFont(FONT_BOLD);
		
		// Path tapering interface initialization and set up
		taperedLengthTextLabel = new JLabel("Length: ");
		taperedLengthValueLabel = new JLabel("50");
		
		taperedLengthTextLabel.setVisible(false);
		taperedLengthValueLabel.setVisible(false);
		taperedLengthTextLabel.setFont(FONT);
		taperedLengthValueLabel.setFont(FONT);
		
		taperedLengthButton = new JButton("Set Length");
		taperedLengthButton.setFocusable(false);
		taperedLengthButton.setFont(FONT);
		taperedLengthButton.setBackground(COLOR_SIMPANEL);
		taperedLengthButton.setVisible(false);
		taperedLengthButton.addActionListener(new ButtonListener());
		
		taperedLengthField = new JTextField("50");
		taperedLengthField.setFocusable(true);
		taperedLengthField.setFont(FONT);
		taperedLengthField.setVisible(false);
		
		// Radius adjusting interface initialization and set up
		radiusTextLabel = new JLabel("Radius: ");
		radiusValueLabel = new JLabel("50");
		
		radiusTextLabel.setFont(FONT);
		radiusValueLabel.setFont(FONT);
		
		radiusField = new JTextField("50");
		radiusField.setFocusable(true);
		radiusField.setFont(FONT);
		
		radiusButton = new JButton("Set Radius");
		radiusButton.setFocusable(false);
		radiusButton.setFont(FONT);
		radiusButton.setBackground(COLOR_SIMPANEL);
		
		resetButton = new JButton("Reset Simulation");
		resetButton.setFocusable(false);
		resetButton.setFont(FONT);
		resetButton.setBackground(COLOR_SIMPANEL);
		
		radiusButton.addActionListener(new ButtonListener());
		resetButton.addActionListener(new ButtonListener());
		
		// Simulation panel initialization and set up
		simListener = new SimListener();
		simPanel = new SimPanel(WIDTH, HEIGHT);
		simPanel.setBackground(COLOR_SIMPANEL);
		simPanel.addMouseListener(simListener);
		simPanel.addMouseMotionListener(simListener);
		simPanel.add(pausedLabel);
		simPanel.add(numberOfBodiesLabel);
		
		// GUI initialization and set up
		gui = new JPanel();
		gui.setBackground(COLOR_GUI);
		gui.setPreferredSize(new Dimension(WIDTH, HEIGHT/6));
		gui.add(radiusTextLabel);
		gui.add(radiusValueLabel);
		gui.add(radiusButton);
		gui.add(radiusField);
		gui.add(resetButton);
		gui.add(toggleNetForces);
		gui.add(togglePathTrace);
		gui.add(togglePathInterpolate);
		gui.add(togglePathTaper);
		gui.add(taperedLengthTextLabel);
		gui.add(taperedLengthValueLabel);
		gui.add(taperedLengthButton);
		gui.add(taperedLengthField);
		gui.add(togglePathColors);
		gui.addMouseListener(new GUIFocusSwitcher());
		
		// Window frame initialization and set up
		frame = new JFrame("N-Body Simulator");
		frame.addKeyListener(simListener);
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setResizable(false);
		frame.add(gui, BorderLayout.SOUTH);
		frame.add(simPanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
		frame.setFocusable(true);
		
		// Misc. initialization
		bodies = new ArrayList<Body>();
		mousePositions = new ArrayList<>();
		newBodyPositions = new ArrayList<>();
		
		isAddingBody = false;
		isTracingPaths = false;
		isInterpolatingPaths = false;
		isTaperingPaths = false;
		isPaused = false;
		isUpdating = false;
		
		taperedLength = BodyPath.getTaperedLength();
		/*
		 * When a new instance of simulator is created,
		 * have it run upon creation.
		 */
		runSim();
	}
	
	/**
	 * Declare, initialize, and start the thread which will run 
	 * the simulation loop
	 */
	private void runSim(){
		Thread loop = new Thread(this);
		loop.start();
	}
	
	/**
	 * At each update step, so long as the simulation is not 
	 * paused,update the forces acting on each body, the 
	 * acceleration of each body, the velocity of each body, 
	 * and the position of each body.  If the simulation is 
	 * tracing the paths of each body, update the paths of each 
	 * body with their previous position.  Also, for each body, 
	 * if their paths are being traced, update their paths' 
	 * boolean values isInterpolated and isTapered with the
	 * respective values controlled by the simulator.  If the 
	 * simulation is not tracing paths, clear the positions of 
	 * each bodies' path to reset every path.
	 * 
	 * Finally, update the internal fields of the simPanel which 
	 * correspond to fields in the simulator that are required by 
	 * the simPanel to appropriately draw the scene.
	 */
	@Override
	public void update(double deltaTime) {
		/*
		 *  Set the length of all tapered paths equal to what is
		 *  on the respective gui label.
		 */
		taperedLength = Integer.parseInt(
				taperedLengthValueLabel.getText());
		BodyPath.setTaperedLength(taperedLength);
		// Loop through all bodies
		for (Body body : bodies){
			isUpdating = true;
			if (isTracingPaths){
				// Update path with the previous position
				body.getPath().add(new Position(
						body.getPosition().x, 
						body.getPosition().y));
				/*
				 *  Ensure the conditions of each bodies' path match 
				 *  the respective fields in the simulation.
				 */
				if (isInterpolatingPaths 
						&& !body.getPath().isInterpolated()){
					body.getPath().setInterpolated(true);
				} else if (!isInterpolatingPaths 
						&& body.getPath().isInterpolated()){
					body.getPath().setInterpolated(false);
				}
				if (isTaperingPaths && !body.getPath().isTapered()){
					body.getPath().setTapered(true);
				} else if (!isTaperingPaths 
						&& body.getPath().isTapered()){
					body.getPath().setTapered(false);
				}
			} else if(body.getPath().list.size() > 0){
				body.getPath().clear();
			}
			// Update body physics
			if (!isPaused){
				body.updateForces(bodies);
				body.updateAcceleration(deltaTime);
				body.updateVelocity(deltaTime);
				body.updatePosition(deltaTime);
			}	
		}
		/*
		 * Update the simulation graphics logic's corresponding  
		 * fields.
		 */
		simPanel.update(bodies, newBodyPositions, mousePositions, 
				isAddingBody, isTracingPaths, isColoringPaths,
				isShowingNetForces);
		isUpdating = false;
	}
	
	/**
	 * Render the scene and update the numberOfBodiesLabel to 
	 * reflect the number of bodies.
	 */
	@Override
	public void render() {
		simPanel.repaint();
		numberOfBodiesLabel.setText("Bodies: " + bodies.size() 
				+ "/" + BODY_LIMIT);
	}
	
	/** Create a new instance of the simulator.*/
	public static void main(String[] args){
		new Simulator();
	}
	
	/**
	 * Handle all of the user input controlling the addition of 
	 * new bodies, increment and decrement of the radius with 
	 * arrow keys, and the pause mechanic with the space-bar.
	 */
	class SimListener implements MouseListener, 
		MouseMotionListener, KeyListener{
		
		/**
		 * Start the process of adding a new body.  Store the 
		 * mouse position as the position of the position of 
		 * the new body.
		 */
		@Override
		public void mousePressed(MouseEvent event) {
			if (SwingUtilities.isLeftMouseButton(event)){
				isAddingBody = true;
				newBodyPosition = new Position(event.getX(), 
						event.getY());
				mousePosition = new Position(event.getX(), 
						event.getY());
				newBodyPositions.add(newBodyPosition);
				mousePositions.add(mousePosition);
			}
			frame.requestFocus();
		}
		
		/**
		 * Update the mouse position, and the vector from 
		 * the new body to the mouse to get the velocity 
		 * vector of the new body.
		 */
		@Override
		public void mouseDragged(MouseEvent event) {
			if (SwingUtilities.isLeftMouseButton(event)){
				mousePosition = new Position(event.getX(), 
						event.getY());
				/*
				 * Store the vector describing the difference
				 * in position between the new body and the
				 * cursor.
				 */
				Vector distanceVector = 
						Vector.customVector(newBodyPosition,
								mousePosition);
				/*
				 * Use this vector to create the velocity vector
				 * of the new body.
				 */
				double magnitude = distanceVector.getMagnitude() 
						/ 100;
				double theta = distanceVector.getTheta();
				newBodyVknot = new Vector(magnitude, theta);
				// Update the mouse position
				mousePositions.remove(mousePositions.size() - 1);
				mousePositions.add(mousePosition);
				isAddingBody = true;
			}
		}
		
		/**
		 * Create a new body based on the calculated vector
		 * between the mouse and the position where it clicked.
		 * Add this new body to the simulation.
		 */
		@Override
		public void mouseReleased(MouseEvent event) {
			if (SwingUtilities.isLeftMouseButton(event)){
				// Get the radius of the new body from the label
				int newBodyRadius = Integer.parseInt(
						radiusValueLabel.getText());
				Body newBody = new Body(newBodyRadius, newBodyPosition, 
						newBodyVknot);
				/* 
				 * Do not add the new body while the other thread is
				 * looping through the bodies in the update method
				 * or when the limit of bodies is reached.
				 */
				if (!isUpdating && bodies.size() < BODY_LIMIT){
					if (newBodyPosition.equals(mousePosition)){
						newBody = new Body(newBodyRadius, newBodyPosition);
					}
					bodies.add(newBody);
					simPanel.update(bodies, newBodyPositions, mousePositions, 
							isAddingBody, isTracingPaths, isColoringPaths,
							isShowingNetForces);
					/*
					 * Maintain the mouse and new body positions if the
					 * simulation is paused.
					 */
					if (!isPaused){
						mousePositions.clear();
						newBodyPositions.clear();
					}
				}
				isAddingBody = false;
			}
		}
		
		/**
		 * Toggle pause when space-bar is pressed, and allow
		 * the arrow keys to manipulate the radius size. 
		 */
		@Override
		public void keyPressed(KeyEvent event) {
			if (event.getKeyCode() == 38){ // Up arrow
				int newRadius = Integer.parseInt(
						radiusValueLabel.getText()) + 1;
				if (newRadius > 400){
					radiusValueLabel.setText("400");
				} else {
					radiusValueLabel.setText(Integer.toString(newRadius));
				}
			}
			if (event.getKeyCode() == 40){ // Down arrow
				int newRadius = Integer.parseInt(
						radiusValueLabel.getText()) - 1;
				if (newRadius < 0){
					radiusValueLabel.setText("0");
				} else {
					radiusValueLabel.setText(Integer.toString(
							newRadius));
				}
			}
			if (event.getKeyCode() == 32 || event.getKeyCode() == 80){ 
					// Space-bar or 'p'
				isPaused = !isPaused;
				pausedLabel.setVisible(!pausedLabel.isVisible());
				/*
				 *  When unpaused, reset the position lists used when
				 *  drawing the vectors of each new body.
				 */
				if (!isPaused){
					simPanel.unpause();
					mousePositions.clear();
					newBodyPositions.clear();
				} else {
					simPanel.pause();
				}
			}
		}
		@Override public void mouseMoved(MouseEvent event) {}
		@Override public void mouseClicked(MouseEvent event) {}
		@Override public void mouseEntered(MouseEvent event) {}
		@Override public void mouseExited(MouseEvent event) {}
		@Override public void keyReleased(KeyEvent event) {}
		@Override public void keyTyped(KeyEvent event) {}
	}
	
	/**
	 * Redirect focus from any component in the user interface
	 * back to the window-frame, so the actions handled in the
	 * SimListener class take priority.
	 */
	class GUIFocusSwitcher extends MouseAdapter{
		
		/** When clicked, redirect focus back to the window-frame*/
		@Override
		public void mouseClicked(MouseEvent event) {
			frame.requestFocus();			
		}
	}
	
	/**
	 * Handle the events that occur after input from every button
	 * in the user interface.
	 */
	class ButtonListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent event) {
			// Radius button
			if (event.getSource().equals(radiusButton)){
				// Input validation
				if (radiusField.getText().matches("[0-9]+")){
					int newRadius = Integer.parseInt(radiusField.getText());
					if (newRadius > 400){
						radiusValueLabel.setText("400");
						radiusField.setText("400");
					} else if (newRadius < 0){
						radiusValueLabel.setText("0");
						radiusField.setText("0");
					} else{
						radiusValueLabel.setText(radiusField.getText());
					}
				}
			}
			// Path taper button
			if (event.getSource().equals(taperedLengthButton)){
				// Input validation
				if (taperedLengthField.getText().matches("[0-9]+")){
					int newLength = Integer.parseInt(
							taperedLengthField.getText());
					if (newLength > 100){
						taperedLengthValueLabel.setText("100");
						taperedLengthField.setText("100");
					}else if (newLength < 0){
						taperedLengthValueLabel.setText("0");
						taperedLengthField.setText("0");
					}else{
						taperedLengthValueLabel.setText(
								taperedLengthField.getText());
					}
				}
			}
			// Reset button
			if (event.getSource().equals(resetButton)){	
				if (!isUpdating){
					bodies.clear();
					mousePositions.clear();
					newBodyPositions.clear();
				}
			}
			frame.requestFocus();
		}
	}
	
	/**
	 * Handle the events that occur after input from every
	 * check-box in the user interface.
	 */
	class ToggleListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent event) {
			// Enable/disable path tracing
			if (event.getSource().equals(togglePathTrace)){
				isTracingPaths = !isTracingPaths;
				togglePathInterpolate.setVisible(
						!togglePathInterpolate.isVisible());
				togglePathTaper.setVisible(
						!togglePathTaper.isVisible());
				togglePathColors.setVisible(
						!togglePathColors.isVisible());
			}
			// Enable/disable path interpolation
			if (event.getSource().equals(togglePathInterpolate)){
				isInterpolatingPaths = !isInterpolatingPaths;
			}
			// Enable/disable path tapering
			if (event.getSource().equals(togglePathTaper)){
				isTaperingPaths = !isTaperingPaths;
				taperedLengthTextLabel.setVisible(
						!taperedLengthTextLabel.isVisible());
				taperedLengthValueLabel.setVisible(
						!taperedLengthValueLabel.isVisible());
				taperedLengthButton.setVisible(
						!taperedLengthButton.isVisible());
				taperedLengthField.setVisible(
						!taperedLengthField.isVisible());
			}
			// Enable/disable path colors
			if (event.getSource().equals(togglePathColors)){
				isColoringPaths = !isColoringPaths;
			}
			/*
			 *  Enable/disable graphical representation of
			 * the net force acting on each body.
			 */
			if (event.getSource().equals(toggleNetForces)){
				isShowingNetForces = !isShowingNetForces;
			}
			frame.requestFocus();	
		}	
	}
}
