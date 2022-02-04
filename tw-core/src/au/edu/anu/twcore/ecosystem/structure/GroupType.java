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

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.*;

import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupFactory;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * Replacement for the Group class
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class GroupType extends ElementType<GroupFactory,GroupComponent> {

	private static final int baseInitRank = N_GROUPTYPE.initRank();

	public GroupType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public GroupType(Identity id,  GraphFactory gfactory) {
		super(id, gfactory);
	}

	// this to call groups in proper dependency order, i.e. higher groups must be initialised first
	private int initRank(GroupType g, int rank) {
		if (g.getParent() instanceof GroupType)
			rank = initRank((GroupType)g.getParent(),rank) + 1;
		return rank;
	}

	@Override
	public int initRank() {
		return initRank(this,baseInitRank);
	}

	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		// containerData initialised in GroupFactory
		sealed = true;
	}

	@Override
	protected GroupFactory makeTemplate(int id) {
		if (setinit!=null)
			return new GroupFactory(categories,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id),id(),id);
		else
			return new GroupFactory(categories,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				null,id(),id);
	}

	/**
	 * The list of function types that are compatible with a GroupType
	 */
	public static TwFunctionTypes[] compatibleFunctionTypes = {
		ChangeState,				// a group may change its drivers
		CreateOtherDecision,		// a group may create new items of its ComponentType
		SetInitialState,			// a group may set its constants at creation time
// THESE are not possible because relations are only between SystemComponents
//		ChangeOtherCategoryDecision,// a group may change the category of a component
//		ChangeOtherState,			// a group may change the state of a component
//		DeleteOtherDecision,		// a group may delete another component
//		ChangeRelationState,		// a group may change the state of a relation
//		MaintainRelationDecision,	// a group may maintain a relation
//		RelateToDecision,			// a group may relate to a new component (ALWAYS unindexed search)
//		SetOtherInitialState		// a group may set the initial state of another component ???
	};

}
