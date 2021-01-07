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
 * generated by CentralResourceGenerator on Tue Dec 22 11:55:44 CET 2020
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum LifespanType {

// permanent: (1) system component stays forever during a simulation, (2) relation stays as long as both its ends stay
	permanent ("(1) system component stays forever during a simulation, (2) relation stays as long as both its ends stay"),

// ephemeral: system component / relations are created and deleted during a simulation by the means of the appropriate  `Function` classes (e.g. `DeleteDecision`, `CreateOtherDecision`, etc. cf. `TwFunctionTypes`)
	ephemeral ("system component / relations are created and deleted during a simulation by the means of the appropriate  `Function` classes (e.g. `DeleteDecision`, `CreateOtherDecision`, etc. cf. `TwFunctionTypes`)");
	
	private final String description;

	private LifespanType(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[LifespanType.values().length];
		for (LifespanType s: LifespanType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (LifespanType e: LifespanType.values())
			result.add(e.toString());
		return result;
	}

	public static LifespanType defaultValue() {
		return permanent;
	}

	static {
		ValidPropertyTypes.recordPropertyType(LifespanType.class.getSimpleName(), 
		LifespanType.class.getName(),defaultValue());
	}

}

