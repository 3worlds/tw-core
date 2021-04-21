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
 * generated by CentralResourceGenerator on Wed Apr 21 11:03:36 CEST 2021
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum BorderType {

// wrap: a soft teletransporting border which sends objects crossing it to the other side of the space
	wrap ("a soft teletransporting border which sends objects crossing it to the other side of the space"),

// reflection: a hard border on which objects bounce back if they try to escape
	reflection ("a hard border on which objects bounce back if they try to escape"),

// sticky: a hard border on which objects stick if they try to escape
	sticky ("a hard border on which objects stick if they try to escape"),

// oblivion: a soft border into oblivion – objects that cross it disappear into nothingness
	oblivion ("a soft border into oblivion – objects that cross it disappear into nothingness"),

// infinite: no border – space extends following object movements
	infinite ("no border – space extends following object movements");
	
	private final String description;

	private BorderType(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[BorderType.values().length];
		for (BorderType s: BorderType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (BorderType e: BorderType.values())
			result.add(e.toString());
		return result;
	}

	public static BorderType defaultValue() {
		return wrap;
	}

	static {
		ValidPropertyTypes.recordPropertyType(BorderType.class.getSimpleName(), 
		BorderType.class.getName(),defaultValue());
	}

}

