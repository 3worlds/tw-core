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
import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Element;
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

	// WRONG!
	/**
	 * Action: Decrease 'longestTimeUnit' in 'timer:tmr1' to be greater than or
	 * equal to UNSPECIFIED or change the time unit range in 'timeline:tmLn1'.
	 * Constraint: Expected property 'longestTimeUnit' of 'timer:tmr1' to be <=
	 * UNSPECIFIED but found MICROSECOND Query class: TimeIntervalValidityQuery
	 * Constraint Specification: mustSatisfyQuery:TimeIntervalValidityQuery Query
	 * item: timeline:tmLn1
	 */
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
		} else {
			allowedMax = TimeUnits.MICROSECOND;
			allowedMin = TimeUnits.MILLENNIUM;
		}
		boolean timeModelRangeError = false;

		// If there is no refScale property we should crash.
//		boolean ok;
		if (timeScaleType.equals(TimeScaleType.MONO_UNIT)) {
			// !ok: Time units of x and y must be the same
			if (shortestTimeUnit != longestTimeUnit) {
				errorMsg = "'" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '"
						+ timeLineNode.toShortString() + "' must be the same for " + scaleKey + "'.";
				actionMsg = "Set '" + shortestTimeUnitKey + "' or '" + longestTimeUnitKey + "' of '"
						+ timeLineNode.toShortString() + "' to the same value.";
				return this;
			}

		} else if (shortestTimeUnit.compareTo(longestTimeUnit) > 0) {
			// !ok: min must be less than max
			errorMsg = "'" + shortestTimeUnitKey + "' must be <= '" + longestTimeUnitKey + "' for time scale type '"
					+ scaleKey + ".";
			actionMsg = "Set '" + shortestTimeUnitKey + "' to be greater than or equal to '" + longestTimeUnitKey
					+ "'.";
			return this;
		}

		if (timeLineNode.hasChildren()) {
			TimeUnits foundTimeUnitsMax = TimeUnits.UNSPECIFIED;
			TimeUnits foundTimeUnitsMin = TimeUnits.MILLENNIUM;
			Iterable<ReadOnlyDataHolder> timers = (Iterable<ReadOnlyDataHolder>) timeLineNode.getChildren();
			for (ReadOnlyDataHolder timer : timers) {
				TimerNode timerNode = (TimerNode) timer;
				if (!timer.properties().hasProperty(P_TIMEMODEL_TU.key()))
					break;

				TimeUnits timerTimeUnits = (TimeUnits) timer.properties().getPropertyValue(P_TIMEMODEL_TU.key());
				// check if outside range
				if (timerTimeUnits.compareTo(shortestTimeUnit) < 0) {
					// increase timer tu to be greater than or equal to shortestTimeUnit;
					actionMsg = "Increase '" + timerNode.toShortString() + "#" + P_TIMEMODEL_TU.key()
							+ "' to be greater than or equal to '" + shortestTimeUnit + "'.";
					// expected property timer#TUname to be >= shortest but found this
					errorMsg = "Expected '" + timerNode.toShortString() + "#" + P_TIMEMODEL_TU.key() + "' to be >= "
							+ shortestTimeUnit + " but found " + timerTimeUnits+"'.";
					return this;
				}
				if (timerTimeUnits.compareTo(longestTimeUnit) > 0) {
					// Decrease timer tu to be less than or equal to longestTimeUnit;
					actionMsg = "Decrease '" + timerNode.toShortString() + "#" + P_TIMEMODEL_TU.key()
							+ "' to be less than or equal to '" + longestTimeUnit + "'.";
					// expected property timer#TUname to be<>= longest shortest but found this
					errorMsg = "Expected '" + timerNode.toShortString() + "#" + P_TIMEMODEL_TU.key() + "' to be >= "
							+ longestTimeUnit + " but found '" + timerTimeUnits+"'.";
					return this;
				}
				

				if (timerTimeUnits.compareTo(foundTimeUnitsMin) < 0)
					foundTimeUnitsMin = timerTimeUnits;
				if (timerTimeUnits.compareTo(foundTimeUnitsMax) > 0)
					foundTimeUnitsMax = timerTimeUnits;
			}
			if (!foundTimeUnitsMin.equals(shortestTimeUnit)) {
				// set at least one of the timers to have min = allowedMin or change this range
				// of time scale

				
//					ok = false;
			} 
			if (!foundTimeUnitsMax.equals(longestTimeUnit)) {
				// set at least one of the timers to have max = allowedMax or change this range
				// of time scale
				timeModelRangeError = true;
//					ok = false;
			}
		}
//		}
//		if (!ok) {
//			if (timeModelRangeError)
//				errorMsg = "Time models collectively must span the whole range of possible values of the time line, i.e. from "
//						+ currentMinTU + " to " + currentMaxTU + ".";
//			else if (refScale.equals(TimeScaleType.MONO_UNIT))
//				errorMsg = "For " + TimeScaleType.MONO_UNIT + ", " + pmin + " must be equal to " + pmax + ".";
//			else
//				errorMsg = pmin + " must be shorter than or equal to " + pmax + ".";
//		}
		return this;
	}

}
