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

import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.DataHolder;

/**
 * @author Ian Davies - 7 Mar 2022
 */

/**
 * Constraint: String tables of given property keys must have the same
 * dimensions.
 * 
 * Input is dataHolder
 */
public class TableDimsMustMatchQuery extends QueryAdaptor {
	private String key1;
	private String key2;

	public TableDimsMustMatchQuery(StringTable args) {
		key1 = args.getWithFlatIndex(0);
		key2 = args.getWithFlatIndex(1);
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		DataHolder dh = (DataHolder) input;
		StringTable t1;
		StringTable t2;
		if (dh.properties().hasProperty(key1))
			t1 = (StringTable) dh.properties().getPropertyValue(key1);
		else
			return this;
		if (dh.properties().hasProperty(key2))
			t2 = (StringTable) dh.properties().getPropertyValue(key2);
		else
			return this;
		if (!t1.sameDimensionsAs(t2)){
			String[] msgs = TextTranslations.getTableDimsMustMatch(key1,key2);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		
		return this;
	}

}
