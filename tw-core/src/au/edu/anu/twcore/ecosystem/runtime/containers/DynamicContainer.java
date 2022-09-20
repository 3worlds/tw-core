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
 * An interface for containers which must delay addition and deletion of items up to
 * a given step
 *
 * @author Jacques Gignoux - 24 janv. 2020
 *
 * @param <T> Item
 */
public interface DynamicContainer<T> extends Container {

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
	 * @param item the item to remove
	 */
	public void removeItem(T item);

	/**
	 * Effectively remove <em>and</em> add items from the container lists (before a
	 * call to this method, they are just stored into {@code itemsToRemove} and
	 * {@code itemsToAdd}). NB: to recursively effect changes for all
	 * sub-containers, use {@code effectAllChanges()}.
	 * The arguments are here in case a return list of items is needed - depends on context of course
	 */
	@SuppressWarnings("unchecked")
	public void effectChanges(Collection<T>...changedLists);

	/**
	 * Flag used to signal if the container has been changed during a computation step
	 *
	 * @return true if changed since last effectChanges()
	 */
	public boolean structureChanged();

	/**
	 * Sets the changed flag to true
	 */
	public DynamicContainer<T> changeStructure();
	
	public default boolean stateChanged() {
		return false;
	}
	
	public default DynamicContainer<T> changeState() {
		return this;
	}
	
}
