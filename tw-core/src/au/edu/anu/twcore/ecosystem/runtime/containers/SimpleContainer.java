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

import fr.cnrs.iees.identity.Identity;

/**
 * A container for 3Worlds runtime items
 *
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface SimpleContainer<T extends Identity> extends Container {

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
	public Collection<T> items();

	public boolean contains(T item);

	public boolean contains(String item);

	public void clearItems();

}
