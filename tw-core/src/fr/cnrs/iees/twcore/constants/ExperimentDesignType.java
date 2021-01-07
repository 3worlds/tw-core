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
 * generated by CentralResourceGenerator on Thu Jan 07 16:34:50 CET 2021
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum ExperimentDesignType {

// singleRun: an experiment consisting of a single simulation run
	singleRun ("an experiment consisting of a single simulation run"),

// crossFactorial: a cross-factorial experiment based on a limited set of parameter values (factors)
	crossFactorial ("a cross-factorial experiment based on a limited set of parameter values (factors)");
	
	private final String description;

	private ExperimentDesignType(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static String[] toStrings() {
		String[] result = new String[ExperimentDesignType.values().length];
		for (ExperimentDesignType s: ExperimentDesignType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (ExperimentDesignType e: ExperimentDesignType.values())
			result.add(e.toString());
		return result;
	}

	public static ExperimentDesignType defaultValue() {
		return singleRun;
	}

	static {
		ValidPropertyTypes.recordPropertyType(ExperimentDesignType.class.getSimpleName(), 
		ExperimentDesignType.class.getName(),defaultValue());
	}

}

