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
package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.ecosystem.runtime.tracking.GraphDataTracker;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.Resettable;

/**
 * The component matching the whole system
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ArenaComponent extends HierarchicalComponent implements Resettable {

	private GraphDataTracker dataTracker;

	public ArenaComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// Particular to arena as it is a singleton instance
	@Override
	public void preProcess() {
		// re-copy initial constants and drivers
		ArenaFactory fact = (ArenaFactory) membership();
		if (currentState() != null)
			currentState().setProperties(fact.driverTemplate);
		if (constants() != null)
			constants().setProperties(fact.lifetimeConstantTemplate);
		// re-run setInitialState method
		if (initialiser() != null) {
			if (constants() != null)
				constants().writeEnable();
			if (currentState() != null)
				currentState().writeEnable();
			initialiser().setInitialState(null, null, null, this, null);
			if (constants() != null)
				constants().writeDisable();
			if (currentState() != null)
				currentState().writeDisable();
		}

	}

	@Override
	public ArenaFactory elementFactory() {
		return (ArenaFactory) membership();
	}

	public void setDataTracker(GraphDataTracker dt) {
		dataTracker = dt;
	}

	public GraphDataTracker getDataTracker() {
		return dataTracker;
	}

	public String name() {
		return elementFactory().name;
	}

}
