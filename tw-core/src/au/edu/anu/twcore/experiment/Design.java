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
import fr.cnrs.iees.twcore.constants.ExperimentDesignType;
import fr.cnrs.iees.twcore.constants.FileType;
import fr.cnrs.iees.omhtk.Resettable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "experiment/design" node label in the 3Worlds configuration tree.
 * Has the "type" or "file" property.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class Design extends InitialisableNode implements Resettable {

	private ExperimentDesignType type = null;
	private FileType fileType;
	
	public Design(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		reset();
	}

	public Design(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		// todo: load the design file
	}

	@Override
	public int initRank() {
		return N_DESIGN.initRank();
	}

	public ExperimentDesignType type() {
		return type;
	}
	
	public String file() {
		return fileType.getFile().getAbsolutePath();
	}

	@SuppressWarnings("unused")
	@Override
	public void reset() {
		if (properties().hasProperty(P_DESIGN_TYPE.key()))
			type = (ExperimentDesignType) properties().getPropertyValue(P_DESIGN_TYPE.key());
		else if (properties().hasProperty(P_DESIGN_FILE.key())) {
			FileType fileType = (FileType)properties().getPropertyValue(P_DESIGN_FILE.key());
//			fileName = (String)properties().getPropertyValue(P_DESIGN_FILE.key());
		}
	}
	
}
