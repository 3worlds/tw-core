/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.ecosystem.runtime.containers;

import java.util.Collection;
import java.util.Set;

import fr.cnrs.iees.identity.Identity;

/**
 * An interface for containers that can be reset to an initial state. Such containers maintain
 * an initial content that can replace their current content when a reset method is called.
 *
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface ResettableContainer<T extends Identity> extends Container {

	// four ways to add items to the initialItems list
	@SuppressWarnings("unchecked")
	/**
	 * Add initial items to a container
	 *
	 * @param items the items to add
	 */
	public void setInitialItems(T... items);

	/**
	 * Add a collection of initial items to a container
	 *
	 * @param items the collection of items to add
	 */
	public void setInitialItems(Collection<T> items);

//	/**
//	 * Add any list of items to a container
//	 *
//	 * @param items an Iterable of items to add
//	 */
//	public void setInitialItems(Iterable<T> items);

	/**
	 * Add a single initialItem to a container
	 *
	 * @param item the item to add
	 */
	public void addInitialItem(T item);

	/**
	 * Exposes initial items as a Set (every item, implementing the Idendity interface, is unique).
	 *
	 * @return the list of initial items as a Set
	 */
	public Set<T> getInitialItems();

	/**
	 * Tells if an item is found in the initial items list of this container
	 *
	 * @param item the initial item to look for
	 * @return true if items is contained in this container
	 */
	public boolean containsInitialItem(T item);

	/**
	 * Initial items are used by ResettableContainer to construct their runtime list of items
	 * This usually implies a cloning of the initial items to runtime items.
	 * This method finds the initial item of which its argument is the clone (if still present - at
	 * runtime, items may come and go).
	 *
	 * @param item the runtime item
	 * @return the initial item of which the previous is the clone, null if nothing found
	 */
	public T initialForItem(String id);

}
