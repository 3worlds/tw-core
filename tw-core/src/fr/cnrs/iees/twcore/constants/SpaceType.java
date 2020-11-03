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
 * generated by CentralResourceGenerator on Tue Nov 03 11:04:32 CET 2020
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum SpaceType {

// continuousFlatSurface: a flat plan with continuous coordinates
	continuousFlatSurface ("a flat plan with continuous coordinates",
		"au.edu.anu.twcore.ecosystem.runtime.space.FlatSurface",
		2),

// squareGrid: a flat square 2D grid for e.g. cellular automata
	squareGrid ("a flat square 2D grid for e.g. cellular automata",
		"au.edu.anu.twcore.ecosystem.runtime.space.SquareGrid",
		2),

// topographicSurface: a topographic plan with elevations
	topographicSurface ("a topographic plan with elevations",
		"au.edu.anu.twcore.ecosystem.runtime.space.TopoSurface",
		2),

// linearNetwork: a graph of connected segments for e.g., hydrologic networks
	linearNetwork ("a graph of connected segments for e.g., hydrologic networks",
		"au.edu.anu.twcore.ecosystem.runtime.space.LineNetwork",
		1);
	
	private final String description;
	private final String className;
	private final Integer dimensions;

	private SpaceType(String description, String className, Integer dimensions) {
		this.description = description;
		this.className = className;
		this.dimensions = dimensions;
	}

	public String description() {
		return description;
	}

	public String className() {
		return className;
	}

	public Integer dimensions() {
		return dimensions;
	}

	public static String[] toStrings() {
		String[] result = new String[SpaceType.values().length];
		for (SpaceType s: SpaceType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (SpaceType e: SpaceType.values())
			result.add(e.toString());
		return result;
	}

	public static SpaceType defaultValue() {
		return continuousFlatSurface;
	}

	static {
		ValidPropertyTypes.recordPropertyType(SpaceType.class.getSimpleName(), 
		SpaceType.class.getName(),defaultValue());
	}

}

