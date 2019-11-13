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
* generated by CentralResourceGenerator on Wed Nov 13 16:15:13 CET 2019
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum PopulationVariables {

// COUNT: population size
	COUNT ("population.size", "n"),

// NADDED: number of births in population
	NADDED ("population.births", "n+"),

// NREMOVED: number of deaths in population
	NREMOVED ("population.deaths", "n-"),

// TCOUNT: total population size (including sub-populations)
	TCOUNT ("total.population.size", "N"),

// TNADDED: total number of births in population (including sub-populations)
	TNADDED ("total.population.births", "N+"),

// TNREMOVED: total number of deaths in population (including sub-populations)
	TNREMOVED ("total.population.deaths", "N-");
	
	private final String longName;
	private final String shortName;

	private PopulationVariables(String longName, String shortName) {
		this.longName = longName;
		this.shortName = shortName;
	}

	public String longName() {
		return longName;
	}

	public String shortName() {
		return shortName;
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

