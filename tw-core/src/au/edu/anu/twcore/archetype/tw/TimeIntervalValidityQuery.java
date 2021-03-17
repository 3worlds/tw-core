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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_TIMEMODEL_TU;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.twcore.constants.TimeScaleType;
import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * A Query to check that minimum and maximum time units are consistent
 * 
 * @author gignoux
 *
 */

public class TimeIntervalValidityQuery extends QueryAdaptor {
	private final String scaleKey;
	private final String shortestTimeUnitKey;
	private final String longestTimeUnitKey;

	public TimeIntervalValidityQuery(StringTable parameters) {
		super();
		shortestTimeUnitKey = parameters.getWithFlatIndex(0); // name of the minimal time unit property
		longestTimeUnitKey = parameters.getWithFlatIndex(1); // name of the maximal time unit property
		scaleKey = parameters.getWithFlatIndex(2); // name of the time scale property
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ReadOnlyDataHolder timeLine = (ReadOnlyDataHolder) input;
		TreeNode timeLineNode = (TreeNode) input;
		TimeScaleType timeScaleType = (TimeScaleType) timeLine.properties().getPropertyValue(scaleKey);
		TimeUnits shortestTimeUnit = (TimeUnits) timeLine.properties().getProperty(shortestTimeUnitKey).getValue();
		TimeUnits longestTimeUnit = (TimeUnits) timeLine.properties().getProperty(longestTimeUnitKey).getValue();
		TimeUnits allowedMax;
		TimeUnits allowedMin;
		if (timeScaleType.equals(TimeScaleType.ARBITRARY)) {
			allowedMax = TimeUnits.UNSPECIFIED;
			allowedMin = TimeUnits.UNSPECIFIED;
		} 
		else {
			allowedMax = TimeUnits.MICROSECOND;
			allowedMin = TimeUnits.MILLENNIUM;
		}
		
		if (timeScaleType.equals(TimeScaleType.ARBITRARY)) {
			if ((shortestTimeUnit != allowedMin)||(longestTimeUnit != allowedMax)) {
				errorMsg = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '"
						+ timeLineNode.toShortString() + "' must be '"+allowedMax+"' for '" + scaleKey + "' but found '"
						+ shortestTimeUnit + "' and '" + longestTimeUnit + "'.";
				actionMsg = "Set '" + shortestTimeUnitKey + "' or '" + longestTimeUnitKey + "' of '"
						+ timeLineNode.toShortString() + "' to '"+allowedMin+"'.";
				return this;
			}
		}
		else if (timeScaleType.equals(TimeScaleType.MONO_UNIT)) {
			// Time units of min and max must be the same
			if (shortestTimeUnit != longestTimeUnit) {
				errorMsg = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '"
						+ timeLineNode.toShortString() + "' must be the same for '" + scaleKey + "' but found '"
						+ shortestTimeUnit + "' and '" + longestTimeUnit + "'.";
				actionMsg = "Set '" + shortestTimeUnitKey + "' or '" + longestTimeUnitKey + "' of '"
						+ timeLineNode.toShortString() + "' to the same value.";
				return this;
			}
		} 
		else if (shortestTimeUnit.compareTo(longestTimeUnit) > 0) {
			// min must be less than max
			errorMsg = "Expected '" + shortestTimeUnitKey + "' to be <= '" + longestTimeUnitKey
					+ "' for time scale type '" + scaleKey + "' but found '" + shortestTimeUnit + "' and '"
					+ longestTimeUnit + "'.";
			;
			actionMsg = "Set '" + shortestTimeUnitKey + "' to be less than or equal to '" + longestTimeUnitKey
					+ "'.";
			return this;
		}

		if (timeLineNode.hasChildren()) {
			TimeUnits foundTimeUnitsMax = TimeUnits.UNSPECIFIED;
			TimeUnits foundTimeUnitsMin = TimeUnits.MILLENNIUM;
			Iterable<ReadOnlyDataHolder> timers = (Iterable<ReadOnlyDataHolder>) timeLineNode.getChildren();
			for (ReadOnlyDataHolder timer : timers) {
				if (!timer.properties().hasProperty(P_TIMEMODEL_TU.key()))
					break;

				TimeUnits timerTimeUnits = (TimeUnits) timer.properties().getPropertyValue(P_TIMEMODEL_TU.key());
				// check if outside range
				if (timerTimeUnits.compareTo(foundTimeUnitsMin) < 0)
					foundTimeUnitsMin = timerTimeUnits;
				if (timerTimeUnits.compareTo(foundTimeUnitsMax) > 0)
					foundTimeUnitsMax = timerTimeUnits;
			}
			if (foundTimeUnitsMin.compareTo(shortestTimeUnit) > 0) {
				// set shortest to found
				actionMsg = "Set property '" + shortestTimeUnitKey + "' to '" + foundTimeUnitsMin + "'.";
				errorMsg = "Expected '" + shortestTimeUnitKey + "=" + foundTimeUnitsMin + "' but found '"
						+ shortestTimeUnit + "'.";
				return this;
			}
			if (foundTimeUnitsMax.compareTo(longestTimeUnit) < 0) {
				// set longest to found;
				actionMsg = "Set property '" + longestTimeUnitKey + "' to '" + foundTimeUnitsMax + "'.";
				errorMsg = "Expected '" + longestTimeUnitKey + "=" + foundTimeUnitsMax + "' but found '"
						+ longestTimeUnit + "'.";
				return this;
			}
		}
		return this;
	}

}
