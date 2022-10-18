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

import au.edu.anu.qgraph.queries.QueryAdaptor;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.TreeNode;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import au.edu.anu.twcore.ecosystem.structure.ConstantsEdge;
import au.edu.anu.twcore.data.Record;

/**
 * <p>
 * Check that a constant is <em>not</em> being tracked.
 * </p>
 * <dl>
 * <dt>Expected input</dt>
 * <dd>Edge between two {@link TreeNode}s</dd>
 * <dt>Type of result</dt>
 * <dd>Same as input ({@code result=input})</dd>
 * <dt>Fails if</dt>
 * <ol>
 * <li>The end node is a {@link Record} with a {@link ConstantsEdge}</li>
 * </ol>
 * </dt>
 * </dl>
 * 
 * @author Ian Davies 19 June 2022
 */
public class CheckConstantTrackingQuery extends QueryAdaptor {
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode end = (TreeNode) ((Edge) input).endNode();
		if (!propose(end)) {
			String[] msg = TextTranslations.getCheckConstantTrackingQuery(((Edge) input).toShortString(),
					end.toShortString());
			actionMsg = msg[0];
			errorMsg = msg[1];
		}
		return this;
	}

	public static boolean propose(TreeNode fieldOrTable) {
		TreeNode parent = fieldOrTable.getParent();
		while (parent != null) {
			if (parent.classId().equals(N_RECORD.label())) {
				if (get(parent.edges(Direction.IN), selectZeroOrOne(hasTheLabel(E_CONSTANTS.label()))) != null)
					return false;
				// stop when/if data role is defined. This means if not defined no fields/tables
				// will show in the menus.
				else if (get(parent.edges(Direction.IN), selectZeroOrOne(hasTheLabel(E_DRIVERS.label()))) != null)
					return true;
				else if (get(parent.edges(Direction.IN), selectZeroOrOne(hasTheLabel(E_DECORATORS.label()))) != null)
					return true;
				else if (get(parent.edges(Direction.IN), selectZeroOrOne(hasTheLabel(E_AUTOVAR.label()))) != null)
					return true;
			}
			parent = parent.getParent();
		}
		return false;

	}

}
