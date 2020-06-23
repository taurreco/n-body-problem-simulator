/**
 * This class contains all of the qualities of a path as well
 * as the logic when adding to, interpolating, and tapering that
 * path.
 * 
 * Author: Tomas L. Dougan
 * Date of last modification: 23 June 2020
 */

package nbodysim;

import java.awt.Color;
import java.util.ArrayList;

public class BodyPath {
	
	private final double PI = Math.PI;
	private final int SPACING = 1;
	
	private static int taperedLength;
	
	public ArrayList<Position> list;
	
	private Color color;
	private boolean isInterpolated;
	private boolean isTapered;
	
	/** 
	 * Constructor: a path with a custom color.
	 * 
	 * @param the color of the path
	 */
	public BodyPath(Color color){
		this.color = color;
		taperedLength = 50;
		list = new ArrayList<>();
		isInterpolated = false;
		isTapered = false;
	}
	
	/** Return the color.*/
	public Color getColor(){
		return color;
	}
	/** Return the tapered length of all paths.*/
	public static int getTaperedLength(){
		return taperedLength;
	}
	
	/** Return whether the path is interpolated or not.*/
	public boolean isInterpolated(){
		return isInterpolated;
	}
	
	/** Return whether the path is tapered or not.*/
	public boolean isTapered(){
		return isTapered;
	}
	
	/**
	 * Change whether the path is interpolated or not.
	 * 
	 * @param the new state of interpolation
	 */
	public void setInterpolated(boolean isInterpolated){
		this.isInterpolated = isInterpolated;
	}
	/** 
	 * Set the tapered length for all paths.
	 *
	 * @param the new tapered length
	 */
	public static void setTaperedLength(int taperedLengthInput){
		taperedLength = taperedLengthInput;
	}
	
	/**
	 * Change whether the path is tapered or not.
	 * 
	 * @param the new state of tapering
	 */
	public void setTapered(boolean isTapered){
		this.isTapered = isTapered;
	}
	
	/** Clear the list.*/
	public void clear(){
		list.clear();
	}
	
	/**
	 * Depending on whether the path is interpolated or tapered,
	 * either add the target position to the path, interpolate to
	 * that target position, and/or remove the first position of
	 * the path until the size of the path is correctly tapered.
	 * 
	 * @param the target position to add or interpolate to in
	 *   the path
	 */
	public void add(Position target){
		// Taper the path when needed
		while (isTapered && list.size() > taperedLength){
			list.remove(0);
		}
		// For all positions other than the first
		if (list.size() > 1){
			Position source = list.get(list.size() -1);
			Vector distanceVector = Vector.customVector(source, 
					target);
			if (isInterpolated){
				/* 
				 * If the distance between positions is large
				 * enough to warrant interpolation
				 */
				if ((int)distanceVector.getMagnitude() 
						> SPACING){
					for (Position position : interpolate(
							distanceVector, source, target)){
						/* 
						 * Add all of the approximated points
						 * between the source and target.
						 */
						list.add(position);
					}
				} else if((int)distanceVector.getMagnitude() 
						== SPACING){
					list.add(target);
				}
				list.add(target);
			} else {
				list.add(target);
			}
		} else {
			list.add(target);
		}
	}

	/**
	 * Algebraically derive a linear equation from two points
	 * and use that to approximate new points in between in order
	 * to keep the spacing in between each point in the path
	 * constant despite changing speed.
	 * 
	 * Whenever the distanceVector is closer to the horizontal, 
	 * solve for y to interpolate, and when its closer to 
	 * vertical, solve for x to interpolate.  This ensures no
	 * undefined slopes are used when approximating new points.
	 * 
	 * @param distance vector between the source and target
	 *   positions
	 * @param the source position to interpolate from
	 * @param the target position to interpolate to
	 * @return a list of positions in between the source and
	 *   target positions to add to the path
	 */
	private ArrayList<Position> interpolate(Vector distanceVector, 
			Position source, Position target){
		ArrayList<Position> interpolated = new ArrayList<>();
		double theta = distanceVector.getTheta();
		// Make sure theta is less than 2*pi
		while (theta > 2*PI){
			theta = theta - 2*PI;
		}
		// Slope
		double m = (target.y - source.y) / (target.x - source.x);
		// Y - intercept
		double b = target.y - (m * target.x);
		/*
		 *  If the angle of the distance vector is closer to
		 *  the horizontal
		 */
		if (theta > (3*PI)/4 &&  theta < (5*PI)/4
				|| theta < PI/4 && theta > 0 
				|| theta > (7*PI)/4
				|| theta < -(7*PI)/4 
				|| theta > -PI/4 && theta < 0 
				|| theta < -(3*PI)/4 && theta > -(5*PI)/4){
			/* 
			 * For every interval of spacing between the source and
			 * target positions
			 */
			for (int i = 0; 
					i < (int)distanceVector.getMagnitude()/SPACING; 
					i++){
				/* 
				 * Use the slope and intercept to find the approximate
				 * position for that interval.
				 */
				double x = target.x + (SPACING * i);
				double y = (x * m) + b;
				// Add the position
				interpolated.add(new Position(x, y));
			}
			return interpolated;
		}
		// If the angle of the distance vector is closer to the vertical
		for (int i = 0; 
				i < (int)distanceVector.getMagnitude()/SPACING; 
				i++){
			/*
			 * Calculate the position using the inverse of the
			 * equation used before.
			 */
			double y = target.y + (SPACING * i);
			double x = (y - b)/m;
			// Add the position
			interpolated.add(new Position(x, y));
		}
		return interpolated;
	}
}
