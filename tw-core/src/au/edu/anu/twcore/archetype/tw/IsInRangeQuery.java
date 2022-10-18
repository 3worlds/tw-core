/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.omugi.collections.tables.*;
import au.edu.anu.omugi.graph.property.Property;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;

/**
 * @author Jacques Gignoux - 21/3/2018 Constraint on numeric properties: value
 *         must be within a given interval
 */
public class IsInRangeQuery extends QueryAdaptor {
	private final double min;
	private final double max;

	/**
	 * Use this constructors if values are provided as a table of 2 values, min
	 * first
	 * 
	 * @param interval
	 */
	public IsInRangeQuery(IntTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}

	public IsInRangeQuery(LongTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}

	public IsInRangeQuery(ShortTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}

	public IsInRangeQuery(FloatTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}

	public IsInRangeQuery(DoubleTable interval) {
		super();
		min = interval.getWithFlatIndex(0);
		max = interval.getWithFlatIndex(1);
	}

	public IsInRangeQuery(ByteTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}

	/**
	 * Use this constructor if two values are provided as two numbers (int or
	 * double)
	 * 
	 * @param mini
	 * @param maxi
	 */
	public IsInRangeQuery(Number mini, Number maxi) {
		super();
		double tmin = mini.doubleValue();
		double tmax = maxi.doubleValue();
		if (tmin > tmax) {
			double d = tmin;
			tmin = tmax;
			tmax = d;
		}
		min = tmin;
		max = tmax;
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Property localItem = (Property) input;
		double value = ((Number) localItem.getValue()).doubleValue();
		if (!(value >= min) & (value <= max)) {
			String[] msgs = TextTranslations.getIsInRangeQuery(localItem.getKey(),value,min,max);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}
		return this;
	}

}
