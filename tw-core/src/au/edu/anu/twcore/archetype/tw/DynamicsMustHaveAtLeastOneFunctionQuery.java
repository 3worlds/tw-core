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

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.omugi.graph.TreeNode;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ian Davies - 9 Apr. 2021
 */

/** Check that each simulator has at least one function. */
public class DynamicsMustHaveAtLeastOneFunctionQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode dynamics = (TreeNode) input;
		// Ignore this query until there is at least one process;
		TreeNode timeline = (TreeNode) get(dynamics, children(), selectZeroOrOne(hasTheLabel(N_TIMELINE.label())));
		if (timeline == null)
			return this;
		
		List<TreeNode> timers = (List<TreeNode>) get(timeline, children(),
				selectZeroOrMany(hasTheLabel(N_TIMER.label())));
		if (timers.isEmpty())
			return this;
		
		List<TreeNode> procs = new ArrayList<>();
		for (TreeNode timer : timers) {
			List<TreeNode> lst = (List<TreeNode>) get(timer, children(),
					selectZeroOrMany(hasTheLabel(N_PROCESS.label())));
			procs.addAll(lst);
		}

		if (procs.isEmpty())
			return this;

		List<TreeNode> funcs = new ArrayList<>();
		for (TreeNode proc : procs) {
			List<TreeNode> lst = (List<TreeNode>) get(proc, children(),
					selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
			funcs.addAll(lst);
		}
		
		if (funcs.isEmpty()) {
			String[] msgs = TextTranslations.DynamicsMustHaveAtLeastOneFunctionQuery();
			actionMsg = msgs[0];
			errorMsg = msgs[1];
			return this;
		}
		return this;
	}

}
