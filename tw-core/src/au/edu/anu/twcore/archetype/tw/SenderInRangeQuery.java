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
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.ExperimentDesignType;

/**
 * @author Ian Davies - 20 Dec. 2020
 */

/**
 * Check that the range of simulator ids a widget is listening to is in fact
 * within the range [0..number of simulators-1].
 */
public class SenderInRangeQuery extends QueryAdaptor {

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		// input is a widget
		TreeGraphDataNode widget = (TreeGraphDataNode) input;

		// find config root.
		TreeNode root = getRoot(widget);
		if (root == null)
			return this;

		TreeGraphDataNode exp = (TreeGraphDataNode) get(root.getChildren(),
				selectZeroOrOne(hasTheLabel(N_EXPERIMENT.label())));
		if (exp == null)
			return this;

		TreeGraphDataNode dsgn = (TreeGraphDataNode) get(exp.getChildren(),
				selectZeroOrOne(hasTheLabel(N_DESIGN.label())));
		if (dsgn == null)
			return this;

		if (!dsgn.properties().hasProperty(P_DESIGN_TYPE.key()))
			return this;

		ExperimentDesignType edt = (ExperimentDesignType) dsgn.properties().getPropertyValue(P_DESIGN_TYPE.key());
		if (!edt.equals(ExperimentDesignType.singleRun))
			return this;

		int nReps = 1;
		if (exp.properties().hasProperty(P_EXP_NREPLICATES.key()))
			nReps = (Integer) exp.properties().getPropertyValue(P_EXP_NREPLICATES.key());
		// Depends on widget policy
		int nSenders = 1;
		int firstSender = 0;
		String pKey = P_WIDGET_SENDER.key();
		if (widget.properties().hasProperty(P_WIDGET_SENDER.key())) {
			firstSender = (Integer) widget.properties().getPropertyValue(P_WIDGET_SENDER.key());
		} else if (widget.properties().hasProperty(P_WIDGET_FIRSTSENDER.key())) {
			firstSender = (Integer) widget.properties().getPropertyValue(P_WIDGET_FIRSTSENDER.key());
			pKey=P_WIDGET_FIRSTSENDER.key();
			if (widget.properties().hasProperty(P_WIDGET_NSENDERS.key())) {
				nSenders = (Integer) widget.properties().getPropertyValue(P_WIDGET_NSENDERS.key());
				nSenders = Math.max(1, nSenders);// IsInRangeQuery will cover this
			}
		}
		IntegerRange listenerRange = new IntegerRange(firstSender, firstSender + (nSenders - 1));
		//NB: integer range will crash : nReps==0 is taken care of by another query
		if (nReps<=0)
			return this;
		//NB Not yet tested with Multi sim (range) widgets
		IntegerRange simRange = new IntegerRange(0, nReps - 1);
		if (!simRange.contains(listenerRange)) {
			String[] msgs = TextTranslations.getSenderInRangeQuery(pKey,simRange,listenerRange,nReps,firstSender);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//			actionMsg = "Edit property '" +pKey+"' to receive data in the range ["+simRange+"].";
//			errorMsg = "Expected sufficent simulator(s) to send data in the range [" + listenerRange + "] but found only '"
//					+ nReps + "' simulator(s). ["+pKey+"="+firstSender+"]";
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

}
