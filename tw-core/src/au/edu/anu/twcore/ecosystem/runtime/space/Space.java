package au.edu.anu.twcore.ecosystem.runtime.space;

import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.uit.space.Box;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public interface Space<T extends Located> {

	/**
	 * Every space is contained within a n-dim bounding box. This function returns
	 * the bounding box (useful for drawing the space).
	 * 
	 * @return the space bounding box
	 */
	public Box boundingBox();
	
	/**
	 * Spaces can be 1,2,3,n-dimensional. This function returns their dimension.
	 * 
	 * @return the space dimension
	 */
	public int ndim();
	
	/**
	 * A space can be represented as a graph of SystemComponents and SystemRelations. This
	 * function returns such a graph. Useful for space complexity comparisons.
	 * 
	 * @return
	 */
	public Graph<? extends Node, ? extends Edge> asGraph();
	
	/**
	 * Locates a system component within this space. This will trigger a call to 
	 * SystemComponent.initialLocation()
	 * 
	 * @param focal the system to add
	 *
	 */
	public void locate(T focal);
	
	/**
	 * Removes the system component focal from this space.
	 * 
	 * @param focal the system to remove
	 * 
	 */
	public void unlocate(T focal);
	
	/**
	 * ABSOLUTE precision (in space distance units), ie distance below which locations are considered
	 * identical.
	 * NB precision is used to assess if two points are at the same location
	 * 
	 * @return the precision of location coordinates
	 */
	public double precision();
	
	/**
	 * 
	 * @return the measurement unit of locations
	 */
	public String units();
	
	/**
	 * 
	 * @return the SpaceType matching this particular descendant
	 */
	public default SpaceType type() {
		for (SpaceType st:SpaceType.values())
			if (st.className().equals(this.getClass().getName()))
				return st;
		return null;
	}
	
	/**
	 * gets all the items located at the shortest distance from the focal item, excluding itself.
	 * It allows for items having the same location.
	 * 
	 * @param item
	 * @return
	 */
	public Iterable<T> getNearestItems(T item);
	
	/**
	 * gets all items within a distance of the focal item.
	 * 
	 * @param item
	 * @param distance
	 * @return
	 */
	public Iterable<T> getItemsWithin(T item, double distance);

}
