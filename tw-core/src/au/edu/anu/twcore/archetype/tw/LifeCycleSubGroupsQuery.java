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
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;


/**
 * In InitialState, a group attached to a life cycle must have subgroups attached to each of
 * the categories contained in its categorySet. This Query checks this constraint.
 *
 * @author Jacques Gignoux - 10 janv. 2020
 *
 */
public class LifeCycleSubGroupsQuery extends Query {

	public LifeCycleSubGroupsQuery() { }

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a Group Node with an out edge to a life cycle
		defaultProcess(input);
		Group localItem = (Group) input;
		LifeCycleType lc = (LifeCycleType) get(localItem.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
			endNode());
		if (lc==null)
			satisfied = true;
		else {
			// categories of the life cycle
			Collection<Category> lccats = (Collection<Category>) get(lc.edges(Direction.OUT),
				selectOne(hasTheLabel(E_APPLIESTO.label())),
				endNode(),
				children());
			int ncats = lccats.size();
			Set<Category> foundcats = new HashSet<>();
			// categories of the input node's children, which should be only groups
			for (TreeNode n:localItem.getChildren()) {
				if (n instanceof Group) {
					Group g = (Group) n;
					Collection<Category> sgcats = (Collection<Category>) get(g.edges(Direction.OUT),
						selectOne(hasTheLabel(E_GROUPOF.label())),
						endNode(),
						outEdges(),
						selectOneOrMany(hasTheLabel(E_BELONGSTO.label())),
						edgeListEndNodes());
					// checks that at least one group category is found in the life cycle's categories
					for (Category cg:sgcats) {
						if (lccats.contains(cg))
							foundcats.add(cg);
					}
				}
			}
			satisfied = (foundcats.size()==ncats);
		}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString()
			+ "A life cycle group must have at least one child group "
			+ "belonging to each category of its categorySet."
			+ "]";
	}

}
