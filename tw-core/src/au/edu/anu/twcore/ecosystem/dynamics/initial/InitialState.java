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
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;

/**
 * A class matching the "ecosystem/dynamics/initialState" node of the 3w configuration
 * @author Jacques Gignoux - 17 juin 2019
 *
 */
// this is initialised after SystemFactory, Ecosystem and LifeCycle, so that all these
// classes are up and ready with their containers and data templates
@Deprecated
public class InitialState
		extends InitialisableNode
		implements Sealable, LimitedEdition<ComponentContainer> {

	private boolean sealed = false;
//	private TwData parameters = null;
//	private SystemContainer container = null;
	private Map<Integer,ComponentContainer> containers = new HashMap<>();

	// default constructor
	public InitialState(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public InitialState(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		sealed = true;
	}

//	@Override
//	public TwData getParameters() {
//		if (sealed)
//			return parameters;
//		else
//			throw new TwcoreException("attempt to access uninitialised data");
//	}

	@Override
	public int initRank() {
		return N_INITIALSTATE.initRank();
	}
	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

//	@Override
//	public SystemContainer container() {
//		if (sealed)
//			return container;
//		else
//			throw new TwcoreException("attempt to access uninitialised data");
//	}

	@Override
	public ComponentContainer getInstance(int id) {
		if (!sealed)
			initialise();
		if (!containers.containsKey(id)) {
			Ecosystem ecosystem = (Ecosystem) getParent().getParent();
			containers.put(id,ecosystem.getInstance(id));
			for (TreeNode tn:getChildren())
				if (tn instanceof ConstantValues)
					((ConstantValues) tn).fill(containers.get(id).parameters());
		}
		return containers.get(id);
	}

}
