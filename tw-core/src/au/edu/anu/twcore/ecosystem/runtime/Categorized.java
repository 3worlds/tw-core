package au.edu.anu.twcore.ecosystem.runtime;

import java.util.Set;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.ens.biologie.generic.SaveableAsText;

/**
 * To be associated to objects sorted by category
 * @author Jacques Gignoux - 23 avr. 2013
 *
 */
public interface Categorized {

	public static final char CATEGORY_SEPARATOR = SaveableAsText.COLON;
	
	/** checks if this instance belongs to all categories specified in the argument */
	public default boolean belongsTo(Set<Category> cs) {
		return categories().containsAll(cs);
	}
	
	/** returns the category stamp of this instance for easy comparison */
	public Set<Category> categories();
	
	/** returns a string representation of the category set */
	public String categoryId();
	
	/** utility to work out a signature from a category list */
	public default String buildCategorySignature() {
		StringBuilder sb = new StringBuilder();
		Set<Category> set = categories();
		int i=0;
		for (Category c:set) {
			sb.append(c.name());
			if (i<set.size()-1)
				sb.append(CATEGORY_SEPARATOR);
			i++;
		}
		return sb.toString();
	}

}
