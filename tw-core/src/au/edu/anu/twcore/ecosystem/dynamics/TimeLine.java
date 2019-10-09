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
package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.DateTimeType;
import fr.cnrs.iees.twcore.constants.TimeScaleType;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.timer.TimeUtil;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * Time line common to all time models within a simulation. Internally time is
 * represented as longs, simulation start occurs at t=0 and one tick matches one
 * timeGrain.
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class TimeLine extends InitialisableNode implements Sealable {

	private boolean sealed = false;

	/** the type of time scale used for this time line */
	private TimeScaleType timeScale;
	/** the set of time units compatible with this time scale type */
	private SortedSet<TimeUnits> timeUnits;
	/** time origin in shortestTimeUnit units */
	private long timeOrigin = 0L;
	/** Calendar value at originTime (0L = 1970/1/1 midnight) */
	private LocalDateTime startDateTime;

	public TimeLine(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public TimeLine(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		timeScale = (TimeScaleType) properties().getPropertyValue(P_TIMELINE_SCALE.key());
		TimeUnits minTU = (TimeUnits) properties().getPropertyValue(P_TIMELINE_SHORTTU.key());
		TimeUnits maxTU = (TimeUnits) properties().getPropertyValue(P_TIMELINE_LONGTU.key());
		timeOrigin = 0L;
		if (properties().hasProperty(P_TIMELINE_TIMEORIGIN.key())) {
			DateTimeType dtt = (DateTimeType) properties().getPropertyValue(P_TIMELINE_TIMEORIGIN.key());
			timeOrigin = dtt.getDateTime();
		}

		// This is now in the TimeScaleType.validTimeUnits() BUT should be made static
		timeUnits = new TreeSet<TimeUnits>();
		if (timeScale.equals(TimeScaleType.ARBITRARY))
			timeUnits.add(TimeUnits.UNSPECIFIED);
		else if (timeScale.equals(TimeScaleType.MONO_UNIT))
			timeUnits.add(minTU); // assuming MaxTU==minTU== the time unit to use for this time scale
		else {
			for (TimeUnits tu : TimeUnits.values()) {
				if (tu.compareTo(maxTU) <= 0)
					if (tu.compareTo(minTU) >= 0)
						switch (tu) {
						case MILLENNIUM:
						case CENTURY:
						case DECADE:
						case DAY:
						case HOUR:
						case MINUTE:
						case SECOND:
						case MILLISECOND:
						case MICROSECOND:
							timeUnits.add(tu);
							break;
						default:
							;
						}
			}
			TimeUnits u = timeScale.yearUnit();
			if (u != null)
				if (u.compareTo(maxTU) <= 0)
					if (u.compareTo(minTU) >= 0)
						timeUnits.add(u);
			u = timeScale.monthUnit();
			if (u != null)
				if (u.compareTo(maxTU) <= 0)
					if (u.compareTo(minTU) >= 0)
						timeUnits.add(u);
			u = timeScale.weekUnit();
			if (u != null)
				if (u.compareTo(maxTU) <= 0)
					if (u.compareTo(minTU) >= 0)
						timeUnits.add(u);
		}
		startDateTime = TimeUtil.longToDate(timeOrigin, minTU);
		sealed = true;
	}

	public LocalDateTime getTimeOrigin() {
		if (sealed)
			return startDateTime;
		throw new TwcoreException("attempt to access uninitialised data");
	}

	@Override
	public int initRank() {
		return N_TIMELINE.initRank();
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

	// all these methods are valid only after initialise() has been called;

	public final TimeScaleType getTimeScale() {
		if (sealed)
			return timeScale;
		throw new TwcoreException("attempt to access uninitialised data");
	}

	public final Set<TimeUnits> timeUnits() {
		if (sealed)
			return timeUnits;
		throw new TwcoreException("attempt to access uninitialised data");
	}

	public final TimeUnits shortestTimeUnit() {
		if (sealed)
			return timeUnits.first();
		throw new TwcoreException("attempt to access uninitialised data");
	}

	public final TimeUnits longestTimeUnit() {
		if (sealed)
			return timeUnits.last();
		throw new TwcoreException("attempt to access uninitialised data");
	}

}
