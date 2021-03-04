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
package au.edu.anu.twcore.archetype.tw.old;

import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_BASELINE;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_SYSTEM;

import java.util.List;

import au.edu.anu.rscs.aot.old.queries.Query;
import au.edu.anu.twcore.experiment.Experiment;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;

/**
 * @author Ian Davies
 *
 * @date 16 Jun 2020
 */
@Deprecated
public class BaselineQuery extends Query {

	@Override
	public Query process(Object input) {
		defaultProcess(input);
		Experiment exp = (Experiment) input;
		Object edge = get(exp.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_BASELINE.label())));
		// Don't care
		if (edge != null) {
			satisfied = true;
			return this;
		}
		TreeNode root = exp.getParent();
		// not ready
		if (root == null) {
			satisfied = true;
			return this;
		}

		@SuppressWarnings("unchecked")
		List<TreeNode> systems = (List<TreeNode>) get(root.getChildren(),
				selectZeroOrMany(hasTheLabel(N_SYSTEM.label())));
		// has no baseline but has multiple systems
		if (systems.size() > 1) {
			satisfied = false;
			return this;
		}
		satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + "requires `baseline:` edge to a `system:'.]";
	}

}
