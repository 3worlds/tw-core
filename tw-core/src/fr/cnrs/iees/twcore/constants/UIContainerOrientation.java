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

public enum UIContainerOrientation {

// horizontal: Display panel is split by a vertical spliter into two panels, first on left and second on right
	horizontal ("Display panel is split by a vertical spliter into two panels, first on left and second on right"),

// vertical: Display panel is split by a horizontal spliter into two panels, first above second
	vertical ("Display panel is split by a horizontal spliter into two panels, first above second");
	
	private final String description;

	private UIContainerOrientation(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[UIContainerOrientation.values().length];
		for (UIContainerOrientation s: UIContainerOrientation.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (UIContainerOrientation e: UIContainerOrientation.values())
			result.add(e.toString());
		return result;
	}

	public static UIContainerOrientation defaultValue() {
		return horizontal;
	}

	static {
		ValidPropertyTypes.recordPropertyType(UIContainerOrientation.class.getSimpleName(), 
		UIContainerOrientation.class.getName(),defaultValue());
	}

}

