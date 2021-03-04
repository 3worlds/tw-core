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

import au.edu.anu.rscs.aot.graph.property.Property;

import au.edu.anu.rscs.aot.old.queries.Query;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

/**
 * @author Jacques Gignoux - 5/9/2016
 * Constraint on type properties: content must be a PrimitiveType value
 * 
 * NB I looked for this Query in the aot packages but couldnt find one, so here it is.
 * maybe it should be moved back to aot as it may be useful there
 * In tw, primitive is replaced by enum fr.ens.biologie.threeWorlds.resources.core.constants.DataElementType
 * so its no longer required unless the hasProperty type is a String
 */
@Deprecated
public class IsPrimitiveTypeQuery extends Query {
	
	public IsPrimitiveTypeQuery() {
		super();
	}

	private Property localItem;
	@Override
	public Query process(Object input) { // input is a prop here
		defaultProcess(input);
		localItem = (Property) input;
		String name = (String)localItem.getValue();
		satisfied = ValidPropertyTypes.isPrimitiveType(name);
		return this;
	}

	public String toString() {
		return "[" + stateString() + "Property '"+localItem.getKey()+"' value must be a primitive.]";
	}

}
