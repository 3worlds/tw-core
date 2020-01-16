package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.ecosystem.runtime.Population;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * A container for 3Worlds runtime items
 * 
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface SimpleContainer<T extends Identity> extends Population {

	/**
	 * Returns the {@linkplain Population} data associated to this container.
	 * Population data are automatic variables added to any container (they include
	 * such things as number of items, number of newly created and deleted items).
	 * Population data are computed internally depending on the dynamics of the
	 * items stored in the container.
	 * 
	 * @return the population data as a read-only property list
	 */
	public ReadOnlyPropertyList populationData();

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
	public void removeItem(String id);

	/**
	 * Gets the item matching the id passed as argument. Only searches this
	 * container item list, not those of the sub-containers.
	 * 
	 * @param id the id to search for
	 * @return the matching item, {@code null} if not found
	 */
	public T item(String id);

	/**
	 * Gets all items contained in this container only, without those contained in
	 * sub-containers.
	 * 
	 * @return a read-only item list
	 */
	public Iterable<T> items();


	/**
	 * Effectively remove <em>and</em> add items from the container lists (before a
	 * call to this method, they are just stored into {@code itemsToRemove} and
	 * {@code itemsToAdd}). NB: to recursively effect changes for all
	 * sub-containers, use {@code effectAllChanges()}.
	 */
	public void effectChanges();

	public boolean contains(T item);

	public boolean contains(String item);
	
	public void clearItems();
	
	public void resetCounters();

}
