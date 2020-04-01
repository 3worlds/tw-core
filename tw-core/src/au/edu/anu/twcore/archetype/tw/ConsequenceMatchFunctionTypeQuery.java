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
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.Collection;

/**
 * A Query to check that a consequence type is compatible with its master
 * function type
 * 
 * @author Jacques Gignoux - 8 Jan. 2020
 *
 */
public class ConsequenceMatchFunctionTypeQuery extends Query {

	private String consequenceType = null;
	private String functionType = null;
	private String strValidTypes;

	public ConsequenceMatchFunctionTypeQuery() {

	}

	@Override
	public Query process(Object input) { // object is a FunctionNode
		defaultProcess(input);
		FunctionNode fn = (FunctionNode) input;
		if (fn.getParent() instanceof FunctionNode) {
			TwFunctionTypes csqtype = (TwFunctionTypes) fn.properties().getPropertyValue(P_FUNCTIONTYPE.key());
			FunctionNode pn = (FunctionNode) fn.getParent();
			TwFunctionTypes ftype = (TwFunctionTypes) pn.properties().getPropertyValue(P_FUNCTIONTYPE.key());
			Collection<TwFunctionTypes> validTypes = TwFunction.consequenceTypes(ftype);
			satisfied = validTypes.contains(csqtype);
			String s = "";
			for (TwFunctionTypes ft : validTypes)
				s += ", " + ft.name();
			s = s.replaceFirst(", ", "[");
			s += "]";
			strValidTypes = s;
			consequenceType = csqtype.name();
			functionType = ftype.name();
		}
		return this;
	}

	public String toString() {
		return "[" + stateString() + "Consequence function type must be one of " + strValidTypes + " for function '"
				+ functionType + "' but found '" + consequenceType + "'.";
	}

}
