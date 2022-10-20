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

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractRelationProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.ComponentProcess;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * A Query to check that a function type is compatible with its ProcessNode type
 * (component or relation process)
 * 
 * @author Jacques Gignoux - 16 sept. 2019
 *
 */
public class FunctionMatchProcessTypeQuery extends QueryAdaptor {

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		FunctionNode fn = (FunctionNode) input;
		// Note: Editing of graph may be in progress and a parent may not exit at this
		// time. If so, don't apply the query.
		if (fn.getParent() == null)
			return this;

		if (fn.getParent() instanceof ProcessNode) {
			TwFunctionTypes ftype = (TwFunctionTypes) fn.properties().getPropertyValue(P_FUNCTIONTYPE.key());
			String functionType = ftype.name();
			ProcessNode pn = (ProcessNode) fn.getParent();
			// avoid throwing a f)(&^ing select query error because its incomprehensible at
			// this level
			List<Node> targets = (List<Node>) get(pn.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
			if (targets.isEmpty())
				return this;

			String processType = null;
			TwFunctionTypes[] validFunctions = null;
			if (targets.get(0) instanceof RelationType) {
				validFunctions = AbstractRelationProcess.compatibleFunctionTypes;
				processType = "relation";
			} else if (targets.get(0) instanceof Category) {
				validFunctions = ComponentProcess.compatibleFunctionTypes;
				processType = "component";
			}
			boolean ok = false;
			for (TwFunctionTypes ft : validFunctions)
				if (ft.equals(ftype)) {
					ok = true;
					break;
				}
			if (!ok) {
				List<String> vt = new ArrayList<>();
				for (TwFunctionTypes ft : validFunctions)
					vt.add(ft.name());
				String[] msgs = TextTranslations.getFunctionMatchProcessTypeQuery(functionType, processType, vt,
						fn.toShortString());
				actionMsg = msgs[0];
				errorMsg = msgs[1];

				// errorMsg = "Function type '" + functionType +
//				"' is incompatible with a " + processType + " process.";
			}
		}
		return this;
	}

}
