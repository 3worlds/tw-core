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
package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_CYCLE;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_GROUPTYPE;

import java.util.Collection;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LifeCycle;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.Direction;

/**
 * Checks that a lifeCycle instance has exactly one Group of each of its LifeCycleType
 * GroupTypes (repeat ten times and then ask).
 *
 * @author J. Gignoux - 22 déc. 2020
 *
 */
public class GroupInstanceRequirementQuery extends QueryAdaptor{

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		LifeCycle lifeCycle = (LifeCycle) input;
		LifeCycleType lct = (LifeCycleType) lifeCycle.getParent();
		Collection<GroupType> gts = (Collection<GroupType>) get(lct.getChildren(),
			selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
		Collection<Group> gs = (Collection<Group>) get(lifeCycle.edges(Direction.IN),
			selectZeroOrMany(hasTheLabel(E_CYCLE.label())),
			edgeListStartNodes());
		boolean ok = true;
		// lists must be of the same length
		if (gs.size()!=gts.size())
			ok = false;
		// for each group, check its grouptype is in the grouptype list by removing it
		// from the list
		for (Group g:gs)
			ok &= gts.remove(g.getParent());
		// if there was no error (ie exactly one group per grouptype) then the list should be empty:
		ok &= gts.isEmpty();
		if (!ok) {
			String[] msgs = TextTranslations.getGroupInstanceRequirementQuery();
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//			errorMsg = "LifeCycle must have exactly one instance of Group per GroupType of its LifeCycleType.";
		}
		return this;
	}

}
