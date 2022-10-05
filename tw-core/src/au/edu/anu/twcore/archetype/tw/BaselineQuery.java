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

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_BASELINE;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_SYSTEM;

import java.util.List;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;

/**
 * 
 * @author Ian Davies - 24 Feb. 2021
 * 
 */
@Deprecated // not needed in single system configurations. May be required if this
			// constraint changes.
public class BaselineQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode exp = (TreeNode) input;
		Object edge = get(exp.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_BASELINE.label())));
		// ok
		if (edge != null)
			return this;
		TreeNode root = exp.getParent();
		// Not ready to decide
		if (root == null)
			return this;
		List<TreeNode> systems = (List<TreeNode>) get(root.getChildren(),
				selectZeroOrMany(hasTheLabel(N_SYSTEM.label())));
		// has no baseline but has multiple systems
		if (systems.size() > 1) {
			String[] sys = new String[systems.size()];
			int i = 0;
			for (TreeNode s : systems)
				sys[i++] = s.toShortString();
			String[] msgs = TextTranslations.getBaselineQuery(exp.toShortString(), sys, E_BASELINE.label());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
			return this;
		}
		return this;
	}

}
