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

import java.lang.reflect.Method;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.rscs.aot.queries.Query;
import static fr.cnrs.iees.io.parsing.TextGrammar.*;

/**
 * A Query to test that if a node property takes certain values, then another property must be set. eg, the
 * second property is not really optional: it is not needed in some cases, but it is in others.
 * @author Jacques Gignoux - 22-06-2018 
 *  
 *  TODO: this class is unfinished !
 *
 */
public class RequirePropertyQuery extends Query {
	
	private String p1;
	private String p2;
	private String type;
	private Object[] values = null;

//	  mustSatisfyQuery
//	    className: fr.ens.biologie.threeWorlds.ui.configuration.archetype3w.RequirePropertyQuery
//	    values: {"fr.ens.biologie.threeWorlds.resources.core.constants.Grouping","select",{[4],ALL,SPECIES,STAGE,SPECIES_STAGE}}
	
	@SuppressWarnings("unchecked")
	public RequirePropertyQuery(String prop1, String type1, String prop2, String valueList) {
		super();
		p1 = prop1;
		p2 = prop2;
		type = type1;
		char[][] bdel = new char[2][2];
		bdel[Table.DIMix] = DIM_BLOCK_DELIMITERS;
		bdel[Table.TABLEix] = TABLE_BLOCK_DELIMITERS;
		char[] isep = new char[2];
		isep[Table.DIMix] = DIM_ITEM_SEPARATOR;
		isep[Table.TABLEix] = TABLE_ITEM_SEPARATOR;
		StringTable s = StringTable.valueOf(valueList,bdel,isep);
		try {
			Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) Class.forName(type);
			Method m = e.getMethod("valueOf", String.class);
			values = new Object[s.size()];
			for (int i = 0; i < values.length; i++)
				values[i] = m.invoke(null,s.getWithFlatIndex(i));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public Query process(Object input) { // input is a node
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		return "[" + this.getClass().getName() +" Must have either '"+p1+"' or '"+p2+ "' property]";
	}

}
