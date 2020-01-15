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
import au.edu.anu.twcore.ecosystem.runtime.process.ComponentProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.RelationProcess;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.List;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * A Query to check that a function type is compatible with its ProcessNode type (component or relation process)
 * 
 * @author Jacques Gignoux - 16 sept. 2019
 *
 */
public class FunctionMatchProcessTypeQuery extends Query {
	
	private String functionType = null;
	private String processType = null;

	public FunctionMatchProcessTypeQuery() {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // object is a FunctionNode
		defaultProcess(input);
		FunctionNode fn = (FunctionNode) input;
		if (fn.getParent() instanceof ProcessNode) {
			TwFunctionTypes ftype = (TwFunctionTypes) fn.properties().getPropertyValue(P_FUNCTIONTYPE.key());
			functionType = ftype.name();
			ProcessNode pn = (ProcessNode) fn.getParent();
			List<Node> targets = (List<Node>) get(pn.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
				edgeListEndNodes());
			TwFunctionTypes[] validFunctions = null;
			if (targets.get(0) instanceof RelationType) {
				validFunctions = RelationProcess.compatibleFunctionTypes;
				processType = "relation";
			}
			else if (targets.get(0) instanceof Category) {
				validFunctions = ComponentProcess.compatibleFunctionTypes;
				processType = "component";
			}
			for (TwFunctionTypes ft:validFunctions)
				if (ft.equals(ftype)) {
					satisfied = true;
					break;
			}
		}
		return this;
	}

	public String toString() {
		return "[" + stateString() + "Function type '" + functionType + 
				"' incompatible with a " + processType + " process.";
	}

}
