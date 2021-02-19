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
package au.edu.anu.twcore.ecosystem.runtime.space;

import java.util.Collection;
import java.util.Collections;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.ecosystem.runtime.containers.DynamicContainer;
import au.edu.anu.twcore.ecosystem.runtime.containers.ResettableContainer;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SingleDataTrackerHolder;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.rngFactory.RngHolder;
import fr.ens.biologie.generic.Resettable;

/**
 * The type of Space used in 3Worlds
 * @author Jacques Gignoux - 13 mars 2020
 *
 * @param <T>
 */
public interface DynamicSpace<T extends Locatable>
	extends Space<T>,
			DynamicContainer<T>,
			ResettableContainer<T>,
			RngHolder,
			SingleDataTrackerHolder<Metadata>,
			Resettable {

	// default: no tracking assumed
	@Override
	default SpaceDataTracker dataTracker() {
		return null;
	}

	// default: no tracking assumed
	@Override
	default Metadata metadata() {
		return null;
	}

	/**
	 * relocate an item already present in space to another location. Item must be a
	 * mobile Locatable. This method can only be called from effectChanges()
	 *
	 * @param item the relocated item
	 */
	public void relocate(T item);

	/**
	 * Tag an item that has changed location for later relocation by relocate(...)
	 *
	 * @param item
	 */
	public void moveItem(T item);
	
	/**
	 * If items can change position in space, they may sometimes jump out of space.
	 * in this case they can be stored into this list (eg if it's not the responsability of the
	 * space to destroy them).
	 * 
	 * @return
	 */
	public default Collection<T> outOfSpaceItems() {
		return Collections.emptyList();
	}

}
