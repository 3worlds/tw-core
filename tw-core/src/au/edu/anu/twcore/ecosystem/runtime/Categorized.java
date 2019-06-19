package au.edu.anu.twcore.ecosystem.runtime;

import java.util.Set;

import au.edu.anu.twcore.ecosystem.structure.Category;

/**
 * To be associated to objects sorted by category
 * @author Jacques Gignoux - 23 avr. 2013
 *
 */
public interface Categorized {

	/** checks if this instance belongs to all categories specified in the argument */
	public default boolean belongsTo(Set<Category> cs) {
		return categories().containsAll(cs);
	}
	
	/** returns the category stamp of this instance for easy comparison */
	public Set<Category> categories();

}
