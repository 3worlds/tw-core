package au.edu.anu.twcore.ecosystem.runtime.space;

import au.edu.anu.twcore.ecosystem.runtime.containers.IndexedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.uit.space.Box;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public interface Space extends IndexedContainer<SystemComponent> {

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
	public Graph<SystemComponent,SystemRelation> asGraph();
	
}
