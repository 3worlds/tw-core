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

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.dynamics.Initialiser;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.HashMap;
import java.util.Map;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * A class matching the "ecosystem/dynamics/initialState/group" node of the 3w configuration
 *
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class Group
		extends InitialisableNode
		implements Sealable, LimitedEdition<ComponentContainer> {

	private boolean sealed = false;
	private ComponentContainer container = null;

	private Map<Integer,ComponentContainer> groups = new HashMap<>();

	private static final int baseInitRank = N_GROUP.initRank();

	// default constructor
	public Group(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Group(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		// ...
		sealed = true;
	}

	// this to call groups in proper dependency order, i.e. higher groups must be initialised first
	private int initRank(Group g, int rank) {
		if (g.getParent() instanceof Group)
			rank = initRank((Group)g.getParent(),rank) + 1;
		return rank;
	}

	@Override
	public int initRank() {
		return initRank(this,baseInitRank);
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

	private ComponentContainer makeContainer(int index) {
		// 1 leaf group
		TreeGraphNode n = (TreeGraphNode) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_GROUPOF.label())),
			endNode());
		if (n!=null) {
			// make sure parent container exists before
			ComponentContainer parentC = null;
			if (getParent() instanceof Group)
				parentC = ((Group)getParent()).getInstance(index);
			else if (getParent() instanceof InitialState)
				parentC = ((InitialState)getParent()).getInstance(index);
			// instantiate container
			ComponentType sf = (ComponentType) n;
			sf.initialise();
			container = sf.makeContainer(index,id(),parentC);
		}
		// 2 life cycle group
		n = (TreeGraphNode) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
			endNode());
		if (n!=null) {
			// make sure parent container exists before
			ComponentContainer parentC = null;
			if (getParent() instanceof InitialState)
				parentC = ((InitialState)getParent()).getInstance(index);
			// instantiate container
			LifeCycle lc = (LifeCycle) n;
			lc.initialise();
			container = lc.makeContainer(index,id(),parentC);
		}
		// fill container with initial values
		for (TreeNode tn:getChildren())
			if (tn instanceof ConstantValues)
				((ConstantValues) tn).fill(container.parameters());
		// compute secondary parameters if initialiser present
		Initialiser.computeSecondaryParameters(this, container, index);
		return container;
	}

	@Override
	public ComponentContainer getInstance(int id) {
		if (!sealed)
			initialise();
		if (!groups.containsKey(id))
			groups.put(id,makeContainer(id));
		return groups.get(id);
	}

}
