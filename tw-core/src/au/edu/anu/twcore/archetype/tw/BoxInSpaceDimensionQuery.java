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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_SPACETYPE;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.uit.space.Box;

/**
 * checks that a box property in a space has the same dimension as the space
 * 
 * @author Jacques Gignoux - 16 sept. 2020
 *
 */
// Tested OK 16/6/2020
public class BoxInSpaceDimensionQuery extends Query {

	private String propName;
	
	public BoxInSpaceDimensionQuery(String boxProp) {
		super();
		propName = boxProp;
	}

	@Override
	public Query process(Object input) { // input is a space node
		defaultProcess(input);
		SpaceNode spn = (SpaceNode) input;
		SpaceType stype = (SpaceType) spn.properties().getPropertyValue(P_SPACETYPE.key());
		if (spn.properties().hasProperty(propName)) {
			Box prop = (Box)spn.properties().getPropertyValue(propName);
			satisfied = (prop.dim()==stype.dimensions());
		}
		else
			satisfied = true; // no problem if no Box property
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() +"'"+ propName + "' must have the same dimensions as its containing space]";
	}

}
