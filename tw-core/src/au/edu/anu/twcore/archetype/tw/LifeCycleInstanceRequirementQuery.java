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
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_LIFECYCLE;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LifeCycle;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.Direction;

/**
 * Check that a Group instance has an edge to a lifeCycle if its groupType is
 * under a LifeCycleType, and also that the lifeCycle is under the same
 * LifeCycleType
 *
 * @author J. Gignoux - 22 déc. 2020
 *
 */
@Deprecated
public class LifeCycleInstanceRequirementQuery extends QueryAdaptor {

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Group group = (Group) input;
		GroupType groupType = (GroupType) group.getParent();
		// if groupType is under a lifeCycle, then group must have an edge to a
		// LifeCycle
		if (groupType.getParent() instanceof LifeCycleType) {
			LifeCycle lc = (LifeCycle) get(group.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
					endNode());
			// no 'cycle' edge found
			// TODO: Fix msgs
			if (lc == null) {
				String[] msgs = TextTranslations.getLifeCycleInstanceRequirementQuery1(E_CYCLE.label(),N_LIFECYCLE.label());
				actionMsg= msgs[0];
				errorMsg = msgs[1];
//				errorMsg = "Group must have a 'cycle' edge to a LifeCycle.";
//				actionMsg = "What should I do?";
				return this;
			}

			// a 'cycle' edge found, check it is under the same lifeCycle as groupType
			if (lc.getParent() != groupType.getParent()) {
				String[] msgs = TextTranslations.getLifeCycleInstanceRequirementQuery2(E_CYCLE.label(),lc.toShortString(),lc.getParent().toShortString(),groupType.toShortString(),groupType.getParent().toShortString());
				actionMsg = msgs[0];
				errorMsg = msgs[1];
//				errorMsg = "Group 'cycle' edge must be connected to the same\"cycle\" as its GroupType.";
//				actionMsg = "What should I do?";
				return this;
			}
		}
		return this;
	}
}