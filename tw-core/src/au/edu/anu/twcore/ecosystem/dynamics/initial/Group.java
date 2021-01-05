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
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A class matching the "system/structure/groupType/group" node of the 3w configuration
 *
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class Group
		extends InitialisableNode
		implements Sealable, LimitedEdition<GroupComponent> {

	private boolean sealed = false;
	private Map<Integer,GroupComponent> groups = new HashMap<>();
	private static final int baseInitRank = N_GROUP.initRank();
	private GroupType groupType = null;
	private LifeCycle lifeCycle = null;
	private ComponentType groupOf = null;

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
		groupType = (GroupType) getParent();
		Edge cycle = (Edge) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())));
		if (cycle!=null)
			lifeCycle = (LifeCycle) cycle.endNode();
		groupOf = (ComponentType) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_GROUPOF.label())),
			endNode());
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

	@Override
	public GroupComponent getInstance(int id) {
		if (!sealed)
			initialise();
		if (!groups.containsKey(id)) {
			// instantiate GroupComponent (with container, and super container)
			// with this group's id as name
			groupType.getInstance(id).setName(id());
			GroupFactory gf = groupType.getInstance(id);
			// put group into container hierarchy
			ComponentContainer superContainer = null;
			LifeCycleComponent lcc = null;
			TreeNode parent = null;
			// 1st case: there is a lifeCycle
			if (lifeCycle!=null) {
				lcc = lifeCycle.getInstance(id);
				superContainer = (ComponentContainer) lcc.content();
				parent = lcc;
			}
			// 2nd case: there is no lifeCycle
			else {							// groupType	structure	system
//				ArenaType system = (ArenaType) getParent().getParent().getParent();
				ArenaType system = (ArenaType) get(this,parent(isClass(ArenaType.class)));
				superContainer = (ComponentContainer)system.getInstance(id).getInstance().content();
				parent = system;
			}
			GroupComponent gc = gf.newInstance(superContainer);
			gc.connectParent(parent);
			// fill group with initial values
			for (TreeNode tn:getChildren())
				if (tn instanceof VariableValues)
					((VariableValues)tn).fill(gc.currentState());
				else if (tn instanceof ConstantValues)
					((ConstantValues) tn).fill(gc.constants());
			// if no components, then some more inits must be done
			if (groupOf!=null) {
				// set itemCategories
				gc.content().setCategorized(groupOf.getInstance(id));
				gc.addGroupIntoLifeCycle();
			}
			groups.put(id,gc);
		}
		return groups.get(id);
	}

}
