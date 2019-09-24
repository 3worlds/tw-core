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
* generated by CentralResourceGenerator on Tue Sep 24 11:13:07 CEST 2019
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public enum StatisticalAggregates {

// MEAN: mean
	MEAN,

// VAR: variance
	VAR,

// SE: standard error
	SE,

// CV: coefficient of variation (%)
	CV,

// SUM: sum
	SUM,

// N: count
	N;
	
	public static String[] toStrings() {
		String[] result = new String[StatisticalAggregates.values().length];
		for (StatisticalAggregates s: StatisticalAggregates.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (StatisticalAggregates e: StatisticalAggregates.values())
			result.add(e.toString());
		return result;
	}

	public static StatisticalAggregates defaultValue() {
		return MEAN;
	}

	static {
		ValidPropertyTypes.recordPropertyType(StatisticalAggregates.class.getSimpleName(), 
		StatisticalAggregates.class.getName(),defaultValue());
	}

}

