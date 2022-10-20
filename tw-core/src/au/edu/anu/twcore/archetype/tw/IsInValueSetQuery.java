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

import fr.cnrs.iees.omugi.collections.tables.*;
import fr.cnrs.iees.omugi.graph.property.Property;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;

/**
 * A Query to check a property value is within a set of valid values. Can be
 * constructed from a list given in the Archetype or an Enum class name. NB: in
 * case of an enum class, values are converted to Strings and checked as
 * Strings.
 * 
 * Modified to allow reference to the element that has this property list.
 * 
 * @author J. Gignoux - 22 nov. 2016
 *
 */
public class IsInValueSetQuery extends QueryAdaptor {
	private  Table valueSet;

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

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Property localItem = (Property) input;
		Object foundValue = localItem.getValue();
		boolean ok = true;
		if (ObjectTable.class.isAssignableFrom(foundValue.getClass())) {
			ObjectTable<?> table = (ObjectTable<?>) foundValue;
			for (int i = 0; i < table.size(); i++)
				ok = ok & valueInSet(table.getWithFlatIndex(i));
		} else
			ok = valueInSet(foundValue);
		if (!ok) {
			String[] msgs = TextTranslations.getIsInValueSetQuery(localItem.getKey(),valueSet,foundValue);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		return this;
	}

	/**
	 * build the query from the name of an Enum class
	 * 
	 * @param enumName
	 */
	@SuppressWarnings("unchecked")
	public IsInValueSetQuery(String enumName) {
		try {
			Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) Class.forName(enumName, true,
					Thread.currentThread().getContextClassLoader());
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
			} else if (valueSet instanceof DoubleTable) {
				if (value.equals(((DoubleTable) valueSet).getWithFlatIndex(i)))
					return true;
			} else if (valueSet instanceof IntTable) {
				if (value.equals(((IntTable) valueSet).getWithFlatIndex(i)))
					return true;
			}
		}
		return false;
	}

}
