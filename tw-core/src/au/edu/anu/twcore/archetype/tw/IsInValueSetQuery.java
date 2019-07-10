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

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.rscs.aot.collections.tables.IntTable;
import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.queries.Query;

/**
 * A Query to check a property value is within a set of valid values. Can be
 * constructed from a list given in the Archetype or an Enum class name.
 * NB: in case of an enum class, values are converted to Strings and checked
 * as Strings.
 * 
 * @author J. Gignoux - 22 nov. 2016
 *
 */
public class IsInValueSetQuery extends Query {

//	private ObjectTable<Object> valueSet = null;
	private Table valueSet = null;

	/**
	 * build the query from a list of values present in the Archetype
	 * 
	 * @param values
	 */
	public IsInValueSetQuery(StringTable values) {
		super();
		valueSet = values;
	}

	public IsInValueSetQuery(IntTable values) {
		super();
		valueSet = values;
	}

	public IsInValueSetQuery(DoubleTable values) {
		super();
		valueSet = values;
	}

	/**
	 * build the query from the name of an Enum class
	 * 
	 * @param enumName
	 */
	@SuppressWarnings("unchecked")
	public IsInValueSetQuery(String enumName) {
		try {
			Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) Class.forName(enumName);
			Object[] oo = e.getEnumConstants();
			valueSet = new StringTable(new Dimensioner(oo.length));
			for (int i = 0; i < oo.length; i++)
				valueSet.setWithFlatIndex(oo[i].toString(), i); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private boolean valueInSet(Object value) {
		for (int i = 0; i < valueSet.size(); i++) {
			if (valueSet instanceof StringTable) {
				if (value.getClass().isEnum())
					value = ((Enum<?>) value).name();// Ian - watch out - dirty Australian trick
				if (value.equals(((StringTable) valueSet).getWithFlatIndex(i)))
					return true;
			}
			else if (valueSet instanceof DoubleTable) {
				if (value.equals(((DoubleTable) valueSet).getWithFlatIndex(i)))
					return true;
			}
			else if (valueSet instanceof IntTable) {
				if (value.equals(((IntTable) valueSet).getWithFlatIndex(i)))
					return true;
			}
		}
		return false;
	}

	@Override
	public Query process(Object input) { // input is a property
		defaultProcess(input);
		Property localItem = (Property) input;
		Object o = localItem.getValue();
		if (ObjectTable.class.isAssignableFrom(o.getClass())) {
			ObjectTable<?> table = (ObjectTable<?>) o;
			satisfied = true;
			for (int i = 0; i < table.size(); i++)
				satisfied = satisfied & valueInSet(table.getWithFlatIndex(i));
		} else
			satisfied = valueInSet(o);
		return this;
	}

	public String toString() {
		return "[" + stateString() + " Property value must be one of " + valueSet.toString() + "]";
	}

}
