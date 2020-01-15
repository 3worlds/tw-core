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
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;

/**
 * Checks that an end node has a specific property with a non null value
 * 
 * @author Jacques Gignoux - 6 nov. 2019
 *
 */
public class EndNodeHasPropertyQuery extends Query {

	String propName = null;
	ReadOnlyDataHolder rodh = null;
	
	public EndNodeHasPropertyQuery(String propname) {
		super();
		propName = propname;
	}

	@Override
	public Query process(Object input) { // input is an Edge
		defaultProcess(input);
		rodh = (ReadOnlyDataHolder)((Edge)input).endNode();
		if (rodh.properties().hasProperty(propName))
			if (rodh.properties().getPropertyValue(propName)!=null)
				satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + "end node '" + rodh 
			+ "' must have the '"+propName+"' property.]";
	}

}
