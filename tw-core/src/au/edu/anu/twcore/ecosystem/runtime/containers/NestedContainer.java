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

/**
 * An interface for methods to add to a container to make it hierarchical (ie containing subcontainers)
 *
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface NestedContainer<T> extends Container {

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
	public Collection<? extends SimpleContainer<T>> subContainers();


	public NestedContainer<T> parentContainer();

	/**
	 * Gets all items contained in this container, including those contained in
	 * sub-containers. CAUTION: these items may belong to different categories, i.e.
	 * they may not store the same sets of variables/parameters.
	 *
	 * @return a read-only list of items
	 */
	public Collection<T> allItems();

	/** counts the total number of items, including those of subContainers */
//	public int totalCount();

	/** counts the total number of added items, including those of subContainers */
//	public int totalAdded();

	/** counts the total number of added items, including those of subContainers */
//	public int totalRemoved();

	public void clearAllItems();

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
