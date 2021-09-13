/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *                    *** 3Worlds - A software for the simulation of ecosystems ***
 *                    *                                                           *
 *                    *        by:  Jacques Gignoux - jacques.gignoux@upmc.fr     *
 *                    *             Ian D. Davies   - ian.davies@anu.edu.au       *
 *                    *             Shayne R. Flint - shayne.flint@anu.edu.au     *
 *                    *                                                           *
 *                    *         http:// ???                                       *
 *                    *                                                           *
 *                    *************************************************************
 * CAUTION: generated code - do not modify
 * generated by CentralResourceGenerator on Mon Sep 13 11:34:34 AEST 2021
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum PopulationVariables {

// COUNT: population size
	COUNT ("population size",
		"population.size",
		"n",
		"0..*",
		"java.lang.Integer",
		"#",
		"count"),

// NADDED: number of births in population
	NADDED ("number of births in population",
		"population.births",
		"n+",
		"0..*",
		"java.lang.Integer",
		"#",
		"nAdded"),

// NREMOVED: number of deaths in population
	NREMOVED ("number of deaths in population",
		"population.deaths",
		"n-",
		"0..*",
		"java.lang.Integer",
		"#",
		"nRemoved"),

// TCOUNT: total population size (including sub-populations)
	TCOUNT ("total population size (including sub-populations)",
		"total.population.size",
		"N",
		"0..*",
		"java.lang.Integer",
		"#",
		"totalCount"),

// TNADDED: total number of births in population (including sub-populations)
	TNADDED ("total number of births in population (including sub-populations)",
		"total.population.births",
		"N+",
		"0..*",
		"java.lang.Integer",
		"#",
		"totalAdded"),

// TNREMOVED: total number of deaths in population (including sub-populations)
	TNREMOVED ("total number of deaths in population (including sub-populations)",
		"total.population.deaths",
		"N-",
		"0..*",
		"java.lang.Integer",
		"#",
		"totalRemoved");
	
	private final String description;
	private final String longName;
	private final String shortName;
	private final String range;
	private final String type;
	private final String units;
	private final String getter;

	private PopulationVariables(String description, String longName, String shortName, String range, String type, String units, String getter) {
		this.description = description;
		this.longName = longName;
		this.shortName = shortName;
		this.range = range;
		this.type = type;
		this.units = units;
		this.getter = getter;
	}

	public String description() {
		return description;
	}

	public String longName() {
		return longName;
	}

	public String shortName() {
		return shortName;
	}

	public String range() {
		return range;
	}

	public String type() {
		return type;
	}

	public String units() {
		return units;
	}

	public String getter() {
		return getter;
	}

	public static String[] toStrings() {
		String[] result = new String[PopulationVariables.values().length];
		for (PopulationVariables s: PopulationVariables.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (PopulationVariables e: PopulationVariables.values())
			result.add(e.toString());
		return result;
	}

	public static PopulationVariables defaultValue() {
		return COUNT;
	}

	static {
		ValidPropertyTypes.recordPropertyType(PopulationVariables.class.getSimpleName(), 
		PopulationVariables.class.getName(),defaultValue());
	}

}

