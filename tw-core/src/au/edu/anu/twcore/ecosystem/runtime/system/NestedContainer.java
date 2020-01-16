package au.edu.anu.twcore.ecosystem.runtime.system;

import fr.cnrs.iees.identity.Identity;

/**
 * An interface for methods to add to a container to make it hierarchical (ie containing subcontainers)
 * 
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface NestedContainer<T extends Identity> {

	/**
	 * Gets the sub-container matching the id passed as an argument. Only searches
	 * this container sub-container list, not those of its sub-containers.
	 * 
	 * @param containerId the sub-container to search for
	 * @return the matching sub-container, {@code null} if not found
	 */
	public NestedContainer<T> subContainer(String containerId);

	/**
	 * Gets the sub-container matching the id passed as an argument. Searches this
	 * container whole sub-container hierarchy, ie including all its sub-containers.
	 * 
	 * @param containerId the sub-container to search for
	 * @return the matching sub-container, {@code null} if not found
	 */
	public NestedContainer<T> findContainer(String containerId);
	
	/**
	 * Gets all sub-containers contained in this container only, without those
	 * contained in sub-containers.
	 * 
	 * @return a read-only container list
	 */
	public Iterable<? extends NestedContainer<T>> subContainers();

	/**
	 * Gets all items contained in this container, including those contained in
	 * sub-containers. CAUTION: these items may belong to different categories, i.e.
	 * they may not store the same sets of variables/parameters.
	 * 
	 * @return a read-only list of items
	 */
	public Iterable<T> allItems();

	/**
	 * Effectively remove <em>and</em> add items from the container lists and from
	 * <em>all</em> its sub-containers (before a call to this method, items are just
	 * stored into {@code itemsToRemove} and {@code itemsToAdd})
	 */
	public void effectAllChanges();

	/** counts the total number of items, including those of subContainers */
	public int totalCount();

	/** counts the total number of added items, including those of subContainers */
	public int totalAdded();

	/** counts the total number of added items, including those of subContainers */
	public int totalRemoved();

	public void clearAllItems();
	
	// This is here in case descendant classes also implement StateContainer
	public default void clearAllVariables() {
		// do nothing
	}
}
