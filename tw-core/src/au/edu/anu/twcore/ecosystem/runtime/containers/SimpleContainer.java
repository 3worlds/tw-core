package au.edu.anu.twcore.ecosystem.runtime.containers;

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

	public boolean contains(T item);

	public boolean contains(String item);
	
	public void clearItems();

}
