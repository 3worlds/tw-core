package containers;

import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;

/**
 * Nestable containers for ... anything, actually. The nesting of containers
 * is optional and can be of any depth
 * 
 * @author Jacques Gignoux - 28 juin 2019
 *
 */
public interface CategorizedContainer<T> extends Iterable<T> {
	
	/** a unique id for this container */
	public String id();
	
	/** a list of category signatures */
	public Set<String> signature();

	// can return an iterator on all the T it contains
	// returns the sub-container with id containerId
	public CategorizedContainer<T> container(String containerId);
	
	// also implements population, statistics, etc.
	
	/** returns the parameterSet associated to this container */
	public TwData parameterSet();
	
	/** returns any variables associated to this container (eg population data) */
	public TwData variables();
	
	/** returns the (singleton) object used to structure this container
	 * eg SystemFactory, lifeCycle, or anything else
	 * Ideally, all the T objects should be processable by a single process which only
	 * knows about this structurer.
	 * 
	 * @return
	 */
	public Categorized structurer();
	
	// what about quadtrees for quick access to data ?
	// all Ts in a such container share common data structures, so should all be indexable 
	// by a Quadtree - hence a faster access.
	// maybe this could be done by another iterator ? what we want is a find() method.
	// RegionIndexingTree in uit has getPointsWithinBox etc. methods
}
