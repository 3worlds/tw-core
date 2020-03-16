package au.edu.anu.twcore.ecosystem.runtime.containers;

import fr.cnrs.iees.identity.Identity;

/**
 * An interface for methods to add to a container to make it hierarchical (ie containing subcontainers)
 *
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface NestedContainer<T extends Identity> extends Container {

	/**
	 * Gets the sub-container matching the id passed as an argument. Only searches
	 * this container sub-container list, not those of its sub-containers.
	 *
	 * @param containerId the sub-container to search for
	 * @return the matching sub-container, {@code null} if not found
	 */
	public SimpleContainer<T> subContainer(String containerId);

	/**
	 * Gets the sub-container matching the id passed as an argument. Searches this
	 * container whole sub-container hierarchy, ie including all its sub-containers.
	 *
	 * @param containerId the sub-container to search for
	 * @return the matching sub-container, {@code null} if not found
	 */
	public SimpleContainer<T> findContainer(String containerId);

	/**
	 * Gets all sub-containers contained in this container only, without those
	 * contained in sub-containers.
	 *
	 * @return a read-only container list
	 */
	public Iterable<? extends SimpleContainer<T>> subContainers();


	public NestedContainer<T> parentContainer();

	/**
	 * Gets all items contained in this container, including those contained in
	 * sub-containers. CAUTION: these items may belong to different categories, i.e.
	 * they may not store the same sets of variables/parameters.
	 *
	 * @return a read-only list of items
	 */
	public Iterable<T> allItems();

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

	/**
	 * Returns the distance of this container to the root of the nested container hierarchy. Hence
	 * 0 for the root container, etc.
	 *
	 * @return
	 */
	public int depth();

	/**
	 * Returns the full hierarchical name of this container in the hierarchy, starting from the top
	 *
	 * @return
	 */
	public String[] fullId();

	/**
	 * Returns the full hierarchical name of an item contained in this container, starting from the top
	 *
	 * @return
	 */
	public String[] itemId(String itid);

}
