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
package au.edu.anu.twcore.ecosystem.dynamics.initial;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.ElementType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.experiment.runtime.DataIdentifier;

/**
 * A class matching the "ecosystem/dynamics/.../parameterValues" node of the 3W configuration tree.
 * 
 * @author Jacques Gignoux - 17 juin 2019
 *
 */
public class InitialValues extends InitialisableNode {

	// default constructor
	public InitialValues(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public InitialValues(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_INITIALVALUES.initRank();
	}
	
	// FLAW? how can we recover the fullId when part of the data is loaded from data sources ?
	// should we use a matching character, ie '*' to tell as per found in data file ?
	// NB ElementType have the initialItems()  ready if initialise has been called
	public DataIdentifier fullId() {
		ElementType<?,?> eType = (ElementType<?, ?>) getParent();
		String[] dif = new String[3]; // component index = 0
		if (eType!=null) {
			if (eType instanceof ComponentType) {
				dif[0] = this.id();
				if (eType.properties().hasProperty(P_DATASOURCE_IDGROUP.key()))
					dif[1] = (String)eType.properties().getPropertyValue(P_DATASOURCE_IDGROUP.key());
				else
					// todo: ask the grouptype ?
					;
				// TODO: search for lifecycle
			}
			else if (eType instanceof GroupType) {
				dif[1] = this.id();
				if (eType.properties().hasProperty(P_DATASOURCE_IDLC.key()))
					dif[2] = (String)eType.properties().getPropertyValue(P_DATASOURCE_IDLC.key());
				else
					// todo: ask the life cycle type if any data was loaded ?
					;
			}
		}
		return new DataIdentifier(dif);
	}

}
