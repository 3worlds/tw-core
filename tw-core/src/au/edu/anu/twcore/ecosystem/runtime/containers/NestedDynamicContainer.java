package au.edu.anu.twcore.ecosystem.runtime.containers;

import fr.cnrs.iees.identity.Identity;

/**
 * A complement to dynamicContainer for nested containers
 *
 * @author Jacques Gignoux - 24 janv. 2020
 *
 */
public interface NestedDynamicContainer<T extends Identity> extends DynamicContainer<T> {

	/**
	 * Effectively remove <em>and</em> add items from the container lists and from
	 * <em>all</em> its sub-containers (before a call to this method, items are just
	 * stored into {@code itemsToRemove} and {@code itemsToAdd})
	 */
	public void effectAllChanges();

}
