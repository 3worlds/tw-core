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

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.Produce;
import au.edu.anu.twcore.ecosystem.structure.Recruit;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import java.util.List;

/**
 * checks that a process associated to a produce or recruit node (in a life cycle) is valid, ie
 * acts on the required categories and has a function of the createOtherDecisionFunction
 * or changeCategoryDecisionFunction class
 *
 * @author Jacques Gignoux - 11 sept. 2019
 *
 */
// checked ok 24/9/2019
// refactored 11/12/2020 to point to functions rather than processes
public class ValidLifeCycleProcessQuery extends Query {

	private String message = null;

	public ValidLifeCycleProcessQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a produce node
		defaultProcess(input);
		TwFunctionTypes requiredFunc = null;
		String s = null;
		if (input instanceof Produce) {
			requiredFunc = TwFunctionTypes.CreateOtherDecision; // category function, not relation
			s = "produce";
		}
		else if (input instanceof Recruit) {
			requiredFunc = TwFunctionTypes.ChangeCategoryDecision; // category function, not relation
			s = "recruit";
		}
		TreeGraphNode pnode = (TreeGraphNode) input;
//		ProcessNode proc = (ProcessNode) get(pnode.edges(Direction.OUT),
//			selectOne(hasTheLabel(E_EFFECTEDBY.label())),
//			endNode());
		FunctionNode func = (FunctionNode) get(pnode.edges(Direction.OUT),
			selectOne(hasTheLabel(E_EFFECTEDBY.label())),
			endNode());
		ProcessNode proc = (ProcessNode) func.getParent();
		// 1 make sure the process categories contain the produce node one
		List<Node> apps = (List<Node>) get(proc.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
			edgeListEndNodes());
		Category fromprod = (Category) get(pnode.edges(Direction.OUT),
			selectOne(hasTheLabel(E_FROMCATEGORY.label())),
			endNode());
		if (apps.contains(fromprod))
			satisfied = true;
		else
			message = s+ " node fromCategory '"+fromprod.id()+"' not found in process '"+proc.id()+"'";
		// 2 make sure the process has a function of the proper type
		if (func.properties().getPropertyValue(P_FUNCTIONTYPE.key()).equals(requiredFunc))
			satisfied &= true;
//		List<FunctionNode> funcs = (List<FunctionNode>) get(proc.getChildren(),
//			selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
//		for (FunctionNode func:funcs)
//			if (func.properties().getPropertyValue(P_FUNCTIONTYPE.key())
//				.equals(requiredFunc)) {
//				satisfied &= true;
//				break;
//		}
		if ((message==null) && (!satisfied)) // means we didnt fall into the previous trap
			message = "missing '"+requiredFunc+"' function type in process '"+proc.id()+"'";
		if (message==null)
			message = "checking "+s+" node category and function type";
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + message+"]";
	}

}
