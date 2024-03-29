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

import java.util.*;

import fr.cnrs.iees.omugi.io.parsing.ValidPropertyTypes;

public enum EdgeEffectCorrection {

// periodic: wrap-around in all dimensions, i.e. leaving objects enter through the other end
	periodic ("wrap-around in all dimensions, i.e. leaving objects enter through the other end"),

// reflective: all borders are reflective, i.e. objects bounce on borders as on rubber walls
	reflective ("all borders are reflective, i.e. objects bounce on borders as on rubber walls"),

// island: a space with oblivious borders in all directions, i.e. leaving objects are lost forever
	island ("a space with oblivious borders in all directions, i.e. leaving objects are lost forever"),

// unbounded: an infinite space adapting to location of items
	unbounded ("an infinite space adapting to location of items"),

// bounded: a space with sticky borders in all directions, i.e. objects that bump into the border stay there
	bounded ("a space with sticky borders in all directions, i.e. objects that bump into the border stay there"),

// tubular: wrap around borders in the first dimension, sticky borders in all other dimensions
	tubular ("wrap around borders in the first dimension, sticky borders in all other dimensions"),

// custom: user-specified border properties – provide a borderType property
	custom ("user-specified border properties – provide a borderType property");
	
	private final String description;

	private EdgeEffectCorrection(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[EdgeEffectCorrection.values().length];
		for (EdgeEffectCorrection s: EdgeEffectCorrection.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (EdgeEffectCorrection e: EdgeEffectCorrection.values())
			result.add(e.toString());
		return result;
	}

	public static EdgeEffectCorrection defaultValue() {
		return periodic;
	}

	static {
		ValidPropertyTypes.recordPropertyType(EdgeEffectCorrection.class.getSimpleName(), 
		EdgeEffectCorrection.class.getName(),defaultValue());
	}

    public BorderType[][] borderTypes(int ndim) {
        BorderType[][] result = new BorderType[2][ndim];
        switch(this) {
            case bounded:
                Arrays.fill(result[0],BorderType.sticky);
                Arrays.fill(result[1],BorderType.sticky);
                break;
            case custom:
                result = null;
                break;
            case island:
                Arrays.fill(result[0],BorderType.oblivion);
                Arrays.fill(result[1],BorderType.oblivion);
                break;
            case periodic:
                Arrays.fill(result[0],BorderType.wrap);
                Arrays.fill(result[1],BorderType.wrap);
                break;
            case reflective:
                Arrays.fill(result[0],BorderType.reflection);
                Arrays.fill(result[1],BorderType.reflection);
                break;
            case tubular:
                Arrays.fill(result[0],BorderType.sticky);
                Arrays.fill(result[1],BorderType.sticky);
                result[0][0] = BorderType.wrap;
                result[1][0] = BorderType.wrap;
                break;
            case unbounded:
                Arrays.fill(result[0],BorderType.infinite);
                Arrays.fill(result[1],BorderType.infinite);
                break;
            default:
                result = null;
                break;
        }
        return result;
    }

}

