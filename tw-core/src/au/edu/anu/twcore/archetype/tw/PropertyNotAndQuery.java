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

import au.edu.anu.omugi.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.*;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;

/**
 * A Query to test that a node, edge or treenode doest not have two properties together
 * @author gignoux - 22 nov. 2016
 *
 */
public class PropertyNotAndQuery extends QueryAdaptor {
	
	private final String name1;
	private final String name2;

	public PropertyNotAndQuery(String name1, String name2) {
		this.name1 = name1;
		this.name2 = name2;
	}

	public PropertyNotAndQuery(StringTable ot) {
		name1 = ot.getWithFlatIndex(0);
		name2 = ot.getWithFlatIndex(1);
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		if (input instanceof ReadOnlyDataHolder) {
			ReadOnlyDataHolder e = (ReadOnlyDataHolder) input;
			// This is only false when both properties are present
			if (e.properties().hasProperty(name1) && e.properties().hasProperty(name2)) {
	//			String[] msgs = TextTranslations.getPropertyXorQuery(name1,name2);
				actionMsg =  "Remove at least one of the properties '"
					+name1+"' and '"+name2+"'";
				errorMsg = "Element '"+e.toString()+"' cannot have both properties '"
					+name1+"' and '"+name2+"'";
			}
		}
		return this;
	}

}
