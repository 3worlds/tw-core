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
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.structure.newapi.GroupType;
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
			GroupComponent gc = groupType.getInstance(id).newInstance();
			// fill group with initial values
			for (TreeNode tn:getChildren())
				if (tn instanceof VariableValues)
					((VariableValues)tn).fill(gc.currentState());
				else if (tn instanceof ConstantValues)
					((ConstantValues) tn).fill(gc.constants());
			groups.put(id,gc);
		}
		return groups.get(id);
	}

}
