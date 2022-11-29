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
package au.edu.anu.twcore.experiment;

import java.util.ArrayList;
import java.util.List;

import fr.cnrs.iees.omugi.collections.tables.StringTable;
import fr.cnrs.iees.omugi.graph.property.Property;

/**
 * @author Ian Davies - 5 Mar 2022
 */
public class ExpFactor {
	private final String name;
	private final List<Property> values;
	private final List<String> valueNames;

	public ExpFactor(String name, List<Property> values, StringTable valueNames) {
		this.name = name;
		this.values = values;
		this.valueNames = new ArrayList<>();
		for (int i = 0; i < valueNames.size(); i++)
			this.valueNames.add(valueNames.getByInt(i));
	}

	public String getName() {
		return name;
	}

	public String getValueName(Property p) {
		return valueNames.get(values.indexOf(p));
	}

	public String getValueName(int i) {
		return valueNames.get(i);
	}

	public int getValueLevel(String level) {
		return valueNames.indexOf(level);
	}

	public int nLevels() {
		return values.size();
	}

	public String toShortString() {
		// dd[short,long]
		StringBuilder result = new StringBuilder().append(name).append("[");
		for (int i = 0; i < valueNames.size(); i++) {
			String vn = valueNames.get(i);
			if (i != 0)
				result.append(",");
			result.append(vn);
		}
		result.append("]");
		return result.toString();
	}

	@Override
	public String toString() {
		// name[short(true),long(false)]{
		StringBuilder result = new StringBuilder().append(name).append("[");
		for (int i = 0; i < values.size(); i++) {
			String label = valueNames.get(i);
			String value = values.get(i).getValue().toString();
			if (i != 0)
				result.append(",");
			result.append(label).append("(").append(value).append(")");
		}
		result.append("]");

		return result.toString();
	}

}
