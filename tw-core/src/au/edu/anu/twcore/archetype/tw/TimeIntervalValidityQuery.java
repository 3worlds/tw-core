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
	private final String pscale;
	private final String pmin;
	private final String pmax;

	public TimeIntervalValidityQuery(StringTable parameters) {
		super();
		pmin = parameters.getWithFlatIndex(0); // name of the minimal time unit property
		pmax = parameters.getWithFlatIndex(1); // name of the maximal time unit property
		pscale = parameters.getWithFlatIndex(2); // name of the time scale property
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		ReadOnlyDataHolder timeLine = (ReadOnlyDataHolder) input;
		TreeNode timeLineNode = (TreeNode) input;
		TimeScaleType refScale = (TimeScaleType) timeLine.properties().getPropertyValue(pscale);
		TimeUnits minTU = (TimeUnits) timeLine.properties().getProperty(pmin).getValue();
		TimeUnits maxTU = (TimeUnits) timeLine.properties().getProperty(pmax).getValue();
		TimeUnits modelMax;
		TimeUnits modelMin;
		if (refScale.equals(TimeScaleType.ARBITRARY)) {
			modelMax = TimeUnits.UNSPECIFIED;
			modelMin = TimeUnits.UNSPECIFIED;
		} else {
			modelMax = TimeUnits.MICROSECOND;
			modelMin = TimeUnits.MILLENNIUM;
		}
		boolean timeModelRangeError = false;

		// If there is no refScale property we should crash.
		boolean ok;
		if (refScale.equals(TimeScaleType.MONO_UNIT))
			ok = (minTU == maxTU);
		else
			ok = (minTU.compareTo(maxTU) <= 0);
		if (ok) {
			if (timeLineNode.hasChildren()) {
				Iterable<ReadOnlyDataHolder> timeModels = (Iterable<ReadOnlyDataHolder>) timeLineNode.getChildren();
				for (ReadOnlyDataHolder timeModel : timeModels) {
					if (!timeModel.properties().hasProperty(P_TIMEMODEL_TU.key()))
						return this;

					TimeUnits tu = (TimeUnits) timeModel.properties().getPropertyValue(P_TIMEMODEL_TU.key());
					if (tu.compareTo(modelMin) < 0)
						modelMin = tu;
					if (tu.compareTo(modelMax) > 0)
						modelMax = tu;
				}
				if (!modelMin.equals(minTU)) {
					timeModelRangeError = true;
					ok = false;
				} else if (!modelMax.equals(maxTU)) {
					timeModelRangeError = true;
					ok = false;
				}
			}
		}
		if (!ok) {
			if (timeModelRangeError)
				errorMsg = "Time models collectively must span the whole range of possible values of the time line, i.e. from "
						+ minTU + " to " + maxTU + ".";
			else if (refScale.equals(TimeScaleType.MONO_UNIT))
				errorMsg = "For " + TimeScaleType.MONO_UNIT + ", " + pmin + " must be equal to " + pmax + ".";
			else
				errorMsg = pmin + " must be shorter than or equal to " + pmax + ".";
		}
		return this;
	}

}
