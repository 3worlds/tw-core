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

import au.edu.anu.rscs.aot.old.queries.Query;
import au.edu.anu.rscs.aot.util.IntegerRange;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.ExperimentDesignType;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.get;

/**
 * @author Ian Davies
 *
 * @date 20 Dec. 2020
 */

/**
 * Check that the range of simulator ids a widget is listening to is in fact
 * within the range [0..number of simulators-1].
 */
@Deprecated
public class SenderInRangeQuery extends Query {
	private static int nReps = 1;
	private static IntegerRange listenerRange = new IntegerRange(0, 0);

	@Override
	public Query process(Object input) {
		defaultProcess(input);
		// input is a widget
		TreeGraphDataNode widget = (TreeGraphDataNode) input;

		// find config root.
		TreeNode root = getRoot(widget);
		if (root == null) {
			// not ready to decide yet
			satisfied = true;
			return this;
		}
		TreeGraphDataNode exp = (TreeGraphDataNode) get(root.getChildren(),
				selectZeroOrOne(hasTheLabel(N_EXPERIMENT.label())));
		if (exp == null) {
			// not ready to decide yet
			satisfied = true;
			return this;
		}
		TreeGraphDataNode dsgn = (TreeGraphDataNode) get(exp.getChildren(),
				selectZeroOrOne(hasTheLabel(N_DESIGN.label())));
		if (dsgn == null) {
			// not ready to decide yet
			satisfied = true;
			return this;
		}
		if (!dsgn.properties().hasProperty(P_DESIGN_TYPE.key())) {
			// Not designed for this query
			satisfied = true;
			return this;
		}
		ExperimentDesignType edt = (ExperimentDesignType) dsgn.properties().getPropertyValue(P_DESIGN_TYPE.key());
		if (!edt.equals(ExperimentDesignType.singleRun)) {
			// Query not designed for other exp types (yet?)
			satisfied = true;
			return this;
		}

		nReps = 1;
		if (exp.properties().hasProperty(P_EXP_NREPLICATES.key()))
			nReps = (Integer) exp.properties().getPropertyValue(P_EXP_NREPLICATES.key());
		// Depends on widget policy
		int nSenders = 1;
		int firstSender = 0;
		if (widget.properties().hasProperty(P_WIDGET_SENDER.key())) {
			firstSender = (Integer) widget.properties().getPropertyValue(P_WIDGET_SENDER.key());
		} else if (widget.properties().hasProperty(P_WIDGET_FIRSTSENDER.key())) {
			firstSender = (Integer) widget.properties().getPropertyValue(P_WIDGET_FIRSTSENDER.key());
			if (widget.properties().hasProperty(P_WIDGET_NSENDERS.key())) {
				nSenders = (Integer) widget.properties().getPropertyValue(P_WIDGET_NSENDERS.key());
				nSenders = Math.max(1, nSenders);// IsInRangeQuery will cover this
			}
		}
		listenerRange = new IntegerRange(firstSender, firstSender + (nSenders - 1));
		IntegerRange simRange = new IntegerRange(0, nReps - 1);
		if (simRange.contains(listenerRange)) {
			satisfied = true;
			return this;
		}
		return this;
	}

	private static TreeNode getRoot(TreeNode n) {
		while (n.getParent() != null)
			n = n.getParent();
		if (n.classId().equals(N_ROOT.label()))
			return n;
		return null;
	}

	@Override
	public String toString() {
		return "[" + stateString() + " Widget is listening to simulators within the range " + listenerRange
				+ " but there are only " + nReps + " simulators.]";
	}
}
