package au.edu.anu.twcore.ecosystem.runtime.containers;

import fr.cnrs.iees.identity.Identity;

/**
 * An interface for containers that provide an efficient way to find an item given another one.
 * It uses a space based on variables to make the searching efficient.
 * 
 * @author Jacques Gignoux - 24 janv. 2020
 *
 */
public interface IndexedContainer<T extends Identity> {
		
	public T getNearestItem(T item);
	
	public Iterable<T> getItemsWithin(T item, double distance);

	public void index(T item);
	
	public default void index(Iterable<T> items) {
		for (T comp:items)
			index(comp);
	}

	public void unIndex(T item);
	
	public default void unindex(Iterable<T> items) {
		for (T i:items)
			unIndex(i);
	}

}
