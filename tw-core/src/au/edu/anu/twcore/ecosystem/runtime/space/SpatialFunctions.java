package au.edu.anu.twcore.ecosystem.runtime.space;

import fr.cnrs.iees.uit.space.Point;

/**
 * Spatial functions made available to end-users.
 * 
 * @author Jacques Gignoux - 4 sept. 2020
 *
 */
public interface SpatialFunctions {

	/**
	 * Distance computations, including edge-effect corrections 
	 * 
	 * @param points
	 * @return
	 */
	public double squaredEuclidianDistance(double[] focal, double[] other);
	
	// all these lead to the previous, no need to everride
	public default double squaredEuclidianDistance(Point focal, Point other) {
		return squaredEuclidianDistance(focal.asArray(),other.asArray());
	}
	public default double squaredEuclidianDistance(Location focal, Location other) {
		return squaredEuclidianDistance(focal.asPoint().asArray(),other.asPoint().asArray());
	}
	public default double euclidianDistance(Point focal, Point other) {
		return Math.sqrt(squaredEuclidianDistance(focal.asArray(),other.asArray()));
	}
	public default double euclidianDistance(Location focal, Location other) {
		return Math.sqrt(squaredEuclidianDistance(focal.asPoint().asArray(),other.asPoint().asArray()));
	}
	public default double euclidianDistance(double[] focal, double[] other) {
		return Math.sqrt(squaredEuclidianDistance(focal,other));
	}
	

}
