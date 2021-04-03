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
 * generated by CentralResourceGenerator on Fri Apr 02 16:45:34 CEST 2021
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum SamplingMode {

// RANDOM: selects a random system component in each group
	RANDOM ("selects a random system component in each group"),

// FIRST: selects the first system component in each group as stored in the simulator’s lists (quite unpredictable unless there is only one item in the list)
	FIRST ("selects the first system component in each group as stored in the simulator’s lists (quite unpredictable unless there is only one item in the list)"),

// LAST: selects the last system component in each group as stored in the simulator’s lists (quite unpredictable unless there is only one item in the list)
	LAST ("selects the last system component in each group as stored in the simulator’s lists (quite unpredictable unless there is only one item in the list)");
	
	private final String description;

	private SamplingMode(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[SamplingMode.values().length];
		for (SamplingMode s: SamplingMode.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (SamplingMode e: SamplingMode.values())
			result.add(e.toString());
		return result;
	}

	public static SamplingMode defaultValue() {
		return RANDOM;
	}

	static {
		ValidPropertyTypes.recordPropertyType(SamplingMode.class.getSimpleName(), 
		SamplingMode.class.getName(),defaultValue());
	}

}

