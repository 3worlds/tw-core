package au.edu.anu.twcore.ecosystem.runtime.space;

import fr.cnrs.iees.uit.space.Point;

/**
 * An interface for objects which are located in a space. Defines method to return the variables
 * that are used to compute spatial coordinates.
 *
 * @author J. Gignoux - 23 nov. 2020
 *
 */
public interface LocationData {

	/**
	 *
	 * @return the variables that are used to compute spatial coordinates
	 */
	public default double[] coordinates() {
		return null;
	}

	/**
	 *
	 * @param rank the rank of the coordinate (= the dimension) in the space
	 * @return the <em>rank<sup>th</sup></em> variable used to compute spatial coordinates
	 */
	public default double coordinate(int rank) {
		return Double.NaN;
	}

	/**
	 *
	 * @return the coordinates as a Point (immutable)
	 */
	public default Point asPoint() {
		return null;
	}

	/**
	 * Sets the incoming values as valid coordinates
	 *
	 * @param coord
	 */
	public default void setCoordinates(double[] coord) {
		// DO NOTHING
	}
}
