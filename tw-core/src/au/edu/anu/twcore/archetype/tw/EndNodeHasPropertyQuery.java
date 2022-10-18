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

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Element;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;

/**
 * Checks that an end node has a specific property with a non null value
 * 
 * @author Jacques Gignoux - 6 nov. 2019
 *
 */

public class EndNodeHasPropertyQuery extends QueryAdaptor {
	String propName;

	public EndNodeHasPropertyQuery(String propname) {
		super();
		propName = propname;
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ReadOnlyDataHolder rodh = (ReadOnlyDataHolder) ((Edge) input).endNode();
		if (rodh.properties().hasProperty(propName))
			if (rodh.properties().getPropertyValue(propName) == null) {
				String item = ((Element) rodh).toShortString();
				String[] msgs = TextTranslations.getEndNodeHasPropertyQuery(item, propName);
				actionMsg = msgs[0];
				errorMsg = msgs[1];
//				errorMsg = ((Element) rodh).toShortString() + "' is a leaf node and must have the '" + propName
//						+ "' property.";
			}
		return this;
	}

}
