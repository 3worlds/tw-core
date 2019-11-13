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

public enum Grouping {

// NO_GROUPING: a single data channel will be created for every system component
	NO_GROUPING ("N"),

// ALL: a single data tracking channel will be created for all system components in the simulation, i.e. at the whole system level
	ALL ("1"),

// SPECIES: a data tracking channel per species will be created, across compatible stages
	SPECIES ("n~sp~"),

// STAGE: a data tracking channel per stage will be created, across all species
	STAGE ("n~st~"),

// SPECIES_STAGE: a data tracking channel will be created per stage and species
	SPECIES_STAGE ("n~sp~ x n~st~");
	
	private final String max_channels;

	private Grouping(String max_channels) {
		this.max_channels = max_channels;
	}

	public String max_channels() {
		return max_channels;
	}

	public static String[] toStrings() {
		String[] result = new String[Grouping.values().length];
		for (Grouping s: Grouping.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (Grouping e: Grouping.values())
			result.add(e.toString());
		return result;
	}

	public static Grouping defaultValue() {
		return NO_GROUPING;
	}

	static {
		ValidPropertyTypes.recordPropertyType(Grouping.class.getSimpleName(), 
		Grouping.class.getName(),defaultValue());
	}

}

