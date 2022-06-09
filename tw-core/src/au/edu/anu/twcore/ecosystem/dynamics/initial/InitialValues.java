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

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
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
	
	// REMINDER: this is called from within ElementType.initialise() (ie graph is valid)
	public DataIdentifier fullId() {
		// the ElementType parent of this node
		TreeNode eType = getParent();
		String[] dif = new String[3]; // component index = 0, group = 1, life cycle = 2
		if (eType instanceof ComponentType) {
			dif[0] = this.id();
			// the ElementType parent - always != null
			TreeNode peType = eType.getParent();
			if (peType instanceof GroupType) {
				dif[1] = (String)this.properties().getPropertyValue(P_DATASOURCE_IDGROUP.key());
				TreeNode gpeType = peType.getParent(); // caution: may be the structure node
				// get the life cycle id from what has been loaded from the file
				// normally this has been done before since SimulatorNode initialises from
				// LifeCycle down to Component
				if (gpeType instanceof LifeCycleType)
					for (DataIdentifier gif:((GroupType)peType).initialItems().keySet())
						if (gif.groupId().equals(dif[1])) {
							dif[2] = gif.lifeCycleId();
							break;
				}
				// if gpeType == arena, no need for a lcId
			}
			// if peType == arena, no need for a groupId
		}
		else if (eType instanceof GroupType) {
			dif[1] = this.id();
			// the ElementType parent - always != null
			TreeNode peType = eType.getParent();
			if (peType instanceof LifeCycleType)
				dif[2] = (String)this.properties().getPropertyValue(P_DATASOURCE_IDLC.key());
			// if not a life cycle type, then peType == arena, no need for a lcid
		}
		else if (eType instanceof LifeCycleType) {
			dif[2] = this.id();
		}
		// if eType==arena, no need for anything
		return new DataIdentifier(dif);
	}

}
