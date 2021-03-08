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
	private final String pscale;
	private final String pmin;
	private final String pmax;

	public TimeIntervalValidityQuery(StringTable parameters) {
		super();
		pmin = parameters.getWithFlatIndex(0); // name of the minimal time unit property
		pmax = parameters.getWithFlatIndex(1); // name of the maximal time unit property
		pscale = parameters.getWithFlatIndex(2); // name of the time scale property
	}
	// WRONG!
/**Action: Decrease 'longestTimeUnit' in 'timer:tmr1' to be greater than or equal to UNSPECIFIED or change the time unit range in 'timeline:tmLn1'.
Constraint: Expected property 'longestTimeUnit' of 'timer:tmr1' to be <= UNSPECIFIED but found MICROSECOND
Query class: TimeIntervalValidityQuery
Constraint Specification: mustSatisfyQuery:TimeIntervalValidityQuery
Query item: timeline:tmLn1
*/
	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ReadOnlyDataHolder timeLine = (ReadOnlyDataHolder) input;
		TreeNode timeLineNode = (TreeNode) input;
		TimeScaleType refScale = (TimeScaleType) timeLine.properties().getPropertyValue(pscale);
		TimeUnits currentMinTU = (TimeUnits) timeLine.properties().getProperty(pmin).getValue();
		TimeUnits currentMaxTU = (TimeUnits) timeLine.properties().getProperty(pmax).getValue();
		TimeUnits allowedMax;
		TimeUnits allowedMin;
		if (refScale.equals(TimeScaleType.ARBITRARY)) {
			allowedMax = TimeUnits.UNSPECIFIED;
			allowedMin = TimeUnits.UNSPECIFIED;
		} else {
			allowedMax = TimeUnits.MICROSECOND;
			allowedMin = TimeUnits.MILLENNIUM;
		}
		boolean timeModelRangeError = false;

		// If there is no refScale property we should crash.
//		boolean ok;
		if (refScale.equals(TimeScaleType.MONO_UNIT)) {
			// !ok: Time units of x and y must be the same
			if (currentMinTU != currentMaxTU) {
				errorMsg = "'" + pmin + "' and '" + pmax + "' of '" + timeLineNode.toShortString()
						+ "' must be the same for " + pscale + "'.";
				actionMsg = "Set '" + pmin + "' or '" + pmax + "' of '" + timeLineNode.toShortString()
						+ "' to the same value.";
				return this;
			}

		} else if (currentMinTU.compareTo(currentMaxTU) > 0) {
			// !ok: min must be less than max
			errorMsg = "'" + pmin + "' must be <= '" + pmax + "' for time scale type '" + pscale + ".";
			actionMsg = "Set '" + pmin + "' to be greater than or equal to '" + pmax + "' in '"
					+ timeLineNode.toShortString() + "'.";
			return this;
		}

		if (timeLineNode.hasChildren()) {
			Iterable<ReadOnlyDataHolder> timeModels = (Iterable<ReadOnlyDataHolder>) timeLineNode.getChildren();
			for (ReadOnlyDataHolder timeModel : timeModels) {
				if (!timeModel.properties().hasProperty(P_TIMEMODEL_TU.key()))
					break;

				TimeUnits tu = (TimeUnits) timeModel.properties().getPropertyValue(P_TIMEMODEL_TU.key());

				// if tu outside range of currentTU list error here
				if (tu.compareTo(currentMinTU) < 0) {
					// this timer must have time unit >= min WRONG!
					errorMsg = "Expected property '" + P_TIMEMODEL_TU.key() + "' of '" + ((Element) timeModel).toShortString()
							+ "' to be >= " + currentMinTU + " but found " + tu;
					actionMsg = "Increase '" + P_TIMEMODEL_TU.key() + "' in '" + ((Element) timeModel).toShortString()
							+ "' to be greater than or equal to " + currentMinTU + " or change the time unit range in '"
							+ timeLineNode.toShortString() + "'.";
					return this;
				}
				if (tu.compareTo(currentMaxTU) > 0) {
					// this timer must have tu >=max
					errorMsg = "Expected property '" + pmax + "' of '" + ((Element) timeModel).toShortString()
							+ "' to be <= " + currentMaxTU + " but found " + tu;
					actionMsg = "Decrease '" + pmax + "' in '" + ((Element) timeModel).toShortString()
							+ "' to be greater than or equal to " + currentMinTU + " or change the time unit range in '"
							+ timeLineNode.toShortString() + "'.";
					return this;
				}

				if (tu.compareTo(allowedMin) < 0)
					allowedMin = tu;
				if (tu.compareTo(allowedMax) > 0)
					allowedMax = tu;
			}
			if (!allowedMin.equals(currentMinTU)) {
				// set at least one of the timers to have min = allowedMin or change this range
				// of time scale
				
				timeModelRangeError = true;
				throw new TwcoreException("TODO");
//					ok = false;
			} else if (!allowedMax.equals(currentMaxTU)) {
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
