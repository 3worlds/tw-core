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

import au.edu.anu.rscs.aot.queries.QueryAdaptor;

import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.List;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;


/**
 * CreateOtherDecision & ChangeCategoryDecision must have an incoming edge from a Recruit or Produce node
 * 
 * @author gignoux 14/1/2022
 *
 */
public class FunctionHasLifeCycleEdgeQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { // input is a functionNode
		initInput(input);
		if (input instanceof FunctionNode) {
			TwFunctionTypes type = (TwFunctionTypes)((FunctionNode)input).properties().getPropertyValue(P_FUNCTIONTYPE.key());
			// Change category always require a link to a recruit node
			if (type==TwFunctionTypes.ChangeCategoryDecision) {
				Edge e = (Edge) get(input,inEdges(),selectZeroOrOne(hasTheLabel(E_EFFECTEDBY.label())));
				if (e==null) {
					errorMsg = "Missing '"+E_EFFECTEDBY.label()+"' in-edge to function node '"+((Node)input).id()+"'";
					actionMsg = "Add '"+E_EFFECTEDBY.label()+"' in-edge to, or remove function node '"+((Node)input).id()+"'";
				}
			}
			// more difficult - create other may do without a life cycle, but sometimes requires one.
			// check this: function ↑process →categories ↑categorySets [←LifeCycleType]
			else if (type==TwFunctionTypes.CreateOtherDecision) {
				TreeGraphNode process = (TreeGraphNode)((FunctionNode)input).getParent(); // because cannot be a consequence
				List<TreeGraphNode> cats = (List<TreeGraphNode>) get(process,
					outEdges(),
					selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
					edgeListEndNodes());
				for (TreeGraphNode cat:cats) {
					TreeGraphNode catset = (TreeGraphNode)cat.getParent();
					List<TreeGraphNode> llct = (List<TreeGraphNode>) get(catset, 
						inEdges(),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
						edgeListStartNodes());
					if (!llct.isEmpty()) {
						Edge e = (Edge) get(input,inEdges(),selectZeroOrOne(hasTheLabel(E_EFFECTEDBY.label())));
						if (e==null) {
							errorMsg = "Missing '"+E_EFFECTEDBY.label()+"' in-edge to function node '"+((Node)input).id()+"'";
							actionMsg = "Add '"+E_EFFECTEDBY.label()+"' in-edge to, or remove function node '"+((Node)input).id()+"'";
						}
					}
				}
			}
		}
		return this;
	}

}
