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
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.List;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;

/**
 * A Query to check that both nodes of an edge have a common parent of a
 * specified type
 *
 * @author Jacques Gignoux - 6 nov. 2019
 *
 */
public class FindCommonCategoryQuery extends QueryAdaptor {
	/**
	 * input is an edge between a datatracker (start) OR a function (relateTo) and a
	 * field or table (end)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode start = (TreeNode) ((Edge) input).startNode();
		TreeNode end = (TreeNode) ((Edge) input).endNode();
		Node process = start.getParent();
		if (process == null)
			return this;

		List<Node> ln = (List<Node>) get(process.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
		if (ln.isEmpty())
			return this;

		boolean ok = true;
		if (ln.get(0) instanceof Category) {
			ok = false;
			for (Node cat : ln) {
				Record topRec = (Record) get(cat.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_DRIVERS.label())),
						endNode());

				if (topRec != null)
					ok = matchTopRec(topRec, end);
				if (!ok) {
					topRec = (Record) get(cat.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_DECORATORS.label())),
							endNode());
					if (topRec != null)
						ok = matchTopRec(topRec, end);
					if (!ok) {
						topRec = (Record) get(cat.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_AUTOVAR.label())),
								endNode());
						if (topRec != null)
							ok = matchTopRec(topRec, end);
					}
				}
				if (ok)
					return this;
			}
		}
		if (!ok) {
			String[] msgs = TextTranslations.getFindCommonCategoryQuery(end.toShortString(), process.toShortString());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		return this;
	}

	private boolean matchTopRec(Record topRec, TreeNode parent) {
		while (parent != null) {
			if (parent.equals(topRec)) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

}
