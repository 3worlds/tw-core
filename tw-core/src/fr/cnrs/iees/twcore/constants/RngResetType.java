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
 * generated by CentralResourceGenerator on Mon Aug 02 11:45:22 AEST 2021
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum RngResetType {

// NEVER: The random number seed is never reset after initialisation of the random number channel
	NEVER ("The random number seed is never reset after initialisation of the random number channel"),

// ONRUNSTART: The random number seed is reset to its former value for every simulation run (producing the same series of random numbers for every simulation)
	ONRUNSTART ("The random number seed is reset to its former value for every simulation run (producing the same series of random numbers for every simulation)");
	
	private final String description;

	private RngResetType(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[RngResetType.values().length];
		for (RngResetType s: RngResetType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (RngResetType e: RngResetType.values())
			result.add(e.toString());
		return result;
	}

	public static RngResetType defaultValue() {
		return NEVER;
	}

	static {
		ValidPropertyTypes.recordPropertyType(RngResetType.class.getSimpleName(), 
		RngResetType.class.getName(),defaultValue());
	}

}

