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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventTimer;
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
		} else {
			allowedMax = TimeUnits.MICROSECOND;
			allowedMin = TimeUnits.MILLENNIUM;
		}

		if (timeScaleType.equals(TimeScaleType.ARBITRARY)) {
			if ((shortestTimeUnit != allowedMin) || (longestTimeUnit != allowedMax)) {
				String[] msgs = TextTranslations.getTimeIntervalValidityQuery1(shortestTimeUnitKey, longestTimeUnitKey,
						timeLineNode.toShortString(), allowedMin.name(), allowedMax.name(), shortestTimeUnit.name(),
						longestTimeUnit.name(), scaleKey);
				actionMsg = msgs[0];
				errorMsg = msgs[1];
				return this;
			}
		} else if (timeScaleType.equals(TimeScaleType.MONO_UNIT)) {
			// Time units of min and max must be the same
			if (shortestTimeUnit != longestTimeUnit) {
				String[] msgs = TextTranslations.getTimeIntervalValidityQuery2(shortestTimeUnitKey, longestTimeUnitKey,
						timeLineNode.toShortString(), scaleKey, shortestTimeUnit.name(), longestTimeUnit.name());
				actionMsg = msgs[0];
				errorMsg = msgs[1];
				return this;
			}
		} else if (shortestTimeUnit.compareTo(longestTimeUnit) > 0) {
			// min must be less than max
			String[] msgs = TextTranslations.getTimeIntervalValidityQuery3(shortestTimeUnitKey, longestTimeUnitKey,
					scaleKey, shortestTimeUnit.name(), longestTimeUnit.name());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
			return this;
		}

		if (timeLineNode.hasChildren()) {
			TimeUnits foundTimeUnitsMax = TimeUnits.UNSPECIFIED;
			TimeUnits foundTimeUnitsMin = TimeUnits.MILLENNIUM;
			Iterable<ReadOnlyDataHolder> timers = (Iterable<ReadOnlyDataHolder>) timeLineNode.getChildren();
			for (ReadOnlyDataHolder timer : timers) {
				if (timer.properties().hasProperty(P_TIMEMODEL_TU.key())) {
					TimeUnits timerTimeUnits = (TimeUnits) timer.properties().getPropertyValue(P_TIMEMODEL_TU.key());
					// if there is an offset, this will be next smallest time unit value
					TimeUnits timerTimeUnitsMin = timerTimeUnits;
					/**
					 * if has an offset, allow a finer time unit
					 * 
					 * 1) offset must round to at least 1 of the next smaller unit. I don't like
					 * this. It should be an integer with no need to round. E.g. if year is the time
					 * unit then offset should be expressed in a number of months.
					 * 
					 * 2) Offset not allowed for ARBITRARY or MONO_UNIT
					 * 
					 * 3) Maybe we can eventually get rid of this - is it useful anymore since the
					 * causal loop has changed?
					 */
					if (timer.properties().hasProperty(P_TIMEMODEL_OFFSET.key()))
						timerTimeUnitsMin = TimeScaleType.getPrev(timeScaleType, timerTimeUnits);
					// check if outside range
					if (timerTimeUnitsMin.compareTo(foundTimeUnitsMin) < 0)
						foundTimeUnitsMin = timerTimeUnitsMin;
					if (timerTimeUnits.compareTo(foundTimeUnitsMax) > 0)
						foundTimeUnitsMax = timerTimeUnits;
				} // EventTimers always run at the shortest time unit
				else if (timer.properties().getPropertyValue(P_TIMEMODEL_SUBCLASS.key())
					.equals(EventTimer.class.getName())){
					TimeUnits timerTimeUnits = shortestTimeUnit;
					if (timerTimeUnits.compareTo(foundTimeUnitsMin) < 0)
						foundTimeUnitsMin = timerTimeUnits;
					if (timerTimeUnits.compareTo(foundTimeUnitsMax) > 0)
						foundTimeUnitsMax = timerTimeUnits;
				}				
			}
//			if (foundTimeUnitsMin.compareTo(shortestTimeUnit) > 0) {
			if (foundTimeUnitsMin.compareTo(shortestTimeUnit) != 0) {
				// set shortest to found
				String[] msgs = TextTranslations.getTimeIntervalValidityQuery4(shortestTimeUnitKey,
						foundTimeUnitsMin.name(), shortestTimeUnit.name());
				actionMsg = msgs[0];
				errorMsg = msgs[1];
				return this;
			}
//			if (foundTimeUnitsMax.compareTo(longestTimeUnit) < 0) {
			if (foundTimeUnitsMax.compareTo(longestTimeUnit) != 0) {
				// set longest to found;
				String[] msgs = TextTranslations.getTimeIntervalValidityQuery4(longestTimeUnitKey,
						foundTimeUnitsMax.name(), longestTimeUnit.name());
				actionMsg = msgs[0];
				errorMsg = msgs[1];
				return this;
			}
		}
		return this;
	}

}
