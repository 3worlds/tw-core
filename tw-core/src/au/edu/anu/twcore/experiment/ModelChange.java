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
package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import fr.cnrs.iees.omugi.collections.tables.DoubleTable;
import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "experiment/treatment/modelChange" node label in the 3Worlds configuration tree.
 * Has the "parameter" and "replaceWith" properties.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class ModelChange extends InitialisableNode {

	public ModelChange(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public ModelChange(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_MODELCHANGE.initRank();
	}

	public String parameter() {
		return (String) properties().getPropertyValue(P_MODELCHANGE_PARAMETER.key());
	}
	
	public DoubleTable replaceWith() {
		return (DoubleTable) properties().getPropertyValue(P_MODELCHANGE_REPLACEWITH.key());
	}
}
