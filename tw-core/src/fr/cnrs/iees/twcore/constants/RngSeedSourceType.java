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
 * generated by CentralResourceGenerator on Wed Jan 26 09:47:44 AEDT 2022
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.omugi.io.parsing.ValidPropertyTypes;

public enum RngSeedSourceType {

// RANDOM: The random number seed is produced from a call to a unique instance of `java.util.Random.Random()`. It uses time to the nanosecond to produce a ‘very likely to be distinct’ seed
	RANDOM ("The random number seed is produced from a call to a unique instance of `java.util.Random.Random()`. It uses time to the nanosecond to produce a ‘very likely to be distinct’ seed"),

// TABLE: The random number seed is taken as an element in a table of 1000 natural random numbers that have been obtained from atmospheric noise. Use the property `tableIndex` to specify which item in this table should be taken for the seed
	TABLE ("The random number seed is taken as an element in a table of 1000 natural random numbers that have been obtained from atmospheric noise. Use the property `tableIndex` to specify which item in this table should be taken for the seed");
	
	private final String description;

	private RngSeedSourceType(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[RngSeedSourceType.values().length];
		for (RngSeedSourceType s: RngSeedSourceType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (RngSeedSourceType e: RngSeedSourceType.values())
			result.add(e.toString());
		return result;
	}

	public static RngSeedSourceType defaultValue() {
		return RANDOM;
	}

	static {
		ValidPropertyTypes.recordPropertyType(RngSeedSourceType.class.getSimpleName(), 
		RngSeedSourceType.class.getName(),defaultValue());
	}

}

