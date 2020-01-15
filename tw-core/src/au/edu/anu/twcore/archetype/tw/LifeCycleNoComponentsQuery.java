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

import static au.edu.anu.rscs.aot.queries.CoreQueries.endNode;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrOne;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_CYCLE;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Component;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;

/**
 * In InitialState, a group attached to a life cycle cannot have components as chlidren. They
 * must be contained in subgroups. This Query checks there are no components in this 
 * group's children if it's a lifecycle.
 * 
 * @author Jacques Gignoux - 10 janv. 2020
 *
 */
public class LifeCycleNoComponentsQuery extends Query {

	public LifeCycleNoComponentsQuery() { }

	@Override
	public Query process(Object input) { // input is a Group Node with an out edge to a life cycle
		defaultProcess(input);
		Group localItem = (Group) input;
		LifeCycle lc = (LifeCycle) get(localItem.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
			endNode());
		satisfied = true;
		if (lc!=null)
			for (TreeNode tn:localItem.getChildren())
				if (tn instanceof Component) {
					satisfied = false;					
					return this;
				}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString()
			+ "A life cycle group cannot have components."
			+ "]";
	}

}
