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
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_APPLIESTO;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_EFFECTEDBY;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_FROMCATEGORY;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_FUNCTIONTYPE;

import java.util.List;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.Produce;
import au.edu.anu.twcore.ecosystem.structure.Recruit;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * checks that a process associated to a produce or recruit node (in a life
 * cycle) is valid, ie acts on the required categories and has a function of the
 * createOtherDecisionFunction or changeCategoryDecisionFunction class
 *
 * @author Jacques Gignoux - 11 sept. 2019
 *
 */
// checked ok 24/9/2019
// refactored 11/12/2020 to point to functions rather than processes
// refactored 9/2/2021 to allow for multiple from or to categories
// refactored 24/12/21 to prevent crashes when graph not finished
public class ValidLifeCycleProcessQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		if ((input instanceof Produce)||(input instanceof Recruit)) {
			TwFunctionTypes requiredFunc = null;
			String s = null;
			if (input instanceof Produce) {
				requiredFunc = TwFunctionTypes.CreateOtherDecision; // category function, not relation
				s = "produce";
			} else if (input instanceof Recruit) {
				requiredFunc = TwFunctionTypes.ChangeCategoryDecision; // category function, not relation
				s = "recruit";
			}
			TreeGraphNode pnode = (TreeGraphNode) input;
			FunctionNode func = (FunctionNode) get(pnode.edges(Direction.OUT), 
				selectZeroOrOne(hasTheLabel(E_EFFECTEDBY.label())),
				endNode());
			if (func!=null) {
				ProcessNode proc = (ProcessNode) func.getParent();
				if (proc!=null) {
					// 1 make sure the process categories contain the produce/recruit node ones
					List<Node> apps = (List<Node>) get(proc.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), 
						edgeListEndNodes());
					List<Category> fromprod = (List<Category>) get(pnode.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())), 
						edgeListEndNodes());
					if (!apps.containsAll(fromprod)) {
						String cats = " ";
						for (Category c : fromprod)
							cats += c.id() + " ";
						String[] msgs = TextTranslations.getValidLifeCycleProcessQuery1(s,cats,proc.toShortString());
						actionMsg = msgs[0];
						errorMsg = msgs[1];
						return this;
					}
					// 2 make sure the process has a function of the proper type
					if (func.properties().getPropertyValue(P_FUNCTIONTYPE.key()).equals(requiredFunc))
						return this;
			//		if ((message==null) && (!satisfied)) // means we didnt fall into the previous trap
					String[] msgs = TextTranslations.getValidLifeCycleProcessQuery2(requiredFunc.name(),proc.toShortString());
					actionMsg = msgs[0];
					errorMsg = msgs[1];
				}
			}
		}
		return this;
	}

}
