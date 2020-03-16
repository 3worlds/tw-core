package au.edu.anu.twcore.ecosystem.runtime.containers;

import fr.cnrs.iees.identity.Identity;

/**
 * An interface for containers which must delay addition and deletion of items up to
 * a given step
 *
 * @author Jacques Gignoux - 24 janv. 2020
 *
 * @param <T>
 */
public interface DynamicContainer<T extends Identity> extends Container {

	/**
	 * Tag an item for addition into this container's item list. The item will be
	 * effectively added only when {@code effectChanges()} or
	 * {@code effectAllChanges()} is called thereafter. This enables one to keep the
	 * container state consistent over time in discrete time simulations.
	 *
	 * @param item the item to add
	 */
	public void addItem(T item);

	/**
	 * Tag an item for removal from this container's item list. The item will be
	 * effectively removed only when {@code effectChanges()} or
	 * {@code effectAllChanges()} is called thereafter. This enables one to keep the
	 * container state consistent over time in discrete time simulations.
	 *
	 * @param id the id of the item to remove
	 */
	public void removeItem(T item);


	/**
	 * Effectively remove <em>and</em> add items from the container lists (before a
	 * call to this method, they are just stored into {@code itemsToRemove} and
	 * {@code itemsToAdd}). NB: to recursively effect changes for all
	 * sub-containers, use {@code effectAllChanges()}.
	 */
	public void effectChanges();

}
