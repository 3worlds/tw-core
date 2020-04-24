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
package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static au.edu.anu.twcore.ecosystem.structure.Category.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "categorySet" node label in the 3Worlds configuration tree.
 * Has no properties.
 *
 * @author Jacques Gignoux - 29 mai 2019
 *
 */
public class CategorySet extends InitialisableNode {

	// default category sets present in ALL models
	public final static String[] systemElements	= {arena,lifeCycle,group,component,relation,space};
	public final static String[] lifespan 		= {permanent,ephemeral};
	public final static String[] composition 	= {population,individual};


	// default constructor
	public CategorySet(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public CategorySet(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_CATEGORYSET.initRank();
	}

	@SuppressWarnings("unchecked")
	public Iterable<Category> categories() {
		return (Iterable<Category>) getChildren();
	}

	public String name() {
		return classId();
	}

}
