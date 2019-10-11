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

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_INSTANCEOF;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.dynamics.SystemComponentNode;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

/**
 * 
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class Individual 
		extends InitialisableNode 
		implements Sealable, LimitedEdition<SystemComponent> {

	private boolean sealed = false;
//	private TwData variables = null;
	private SystemComponentNode factory = null;
	private Map<Integer,SystemComponent> individuals = new HashMap<>();

	// default constructor
	public Individual(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Individual(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		factory = (SystemComponentNode) get(edges(Direction.OUT),
			selectOne(hasTheLabel(E_INSTANCEOF.label())),
			endNode());
		sealed = true;
	}

	@Override
	public int initRank() {
		return N_INDIVIDUAL.initRank();
	}
	
//	public TwData getVariables() {
//		if (sealed)
//			return variables;
//		else
//			throw new TwcoreException("attempt to access uninitialised data");
//	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SystemComponent getInstance(int id) {
		if (!sealed)
			initialise();
		if (!individuals.containsKey(id)) {
			SystemComponent sc = factory.getInstance(id).newInstance();
			for (TreeNode tn:getChildren())
				if (tn instanceof VariableValues)
					((VariableValues)tn).fill(sc.currentState());
			// TODO: workout the particular case when an individual has parameters
			LimitedEdition<SystemContainer> p = (LimitedEdition<SystemContainer>) getParent();
			if (sc.membership().categories().equals(p.getInstance(id).categoryInfo().categories()))
				p.getInstance(id).addInitialItem(sc);	
		}
		return individuals.get(id);
	}
	
}
