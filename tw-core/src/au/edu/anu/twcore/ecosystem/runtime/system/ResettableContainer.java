package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Collection;
import java.util.Set;

import fr.cnrs.iees.identity.Identity;

/**
 * An interface for containers that can be reset to an initial state - means they maintains
 * an initial content that can replace their current content at any time.
 * 
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface ResettableContainer<T extends Identity> {

	// four ways to add items to the initialItems list
	@SuppressWarnings("unchecked")
	public void setInitialItems(T... items);
	public void setInitialItems(Collection<T> items);
	public void setInitialItems(Iterable<T> items);
	public void addInitialItem(T item);
	public Set<T> getInitialItems();

	public boolean containsInitialItem(T item);

	/**
	 * Returns the initial item from which an item is a copy, null if it's not a
	 * copy.
	 * 
	 * @param item
	 * @return
	 */
	public T initialForItem(String id);
	
}
