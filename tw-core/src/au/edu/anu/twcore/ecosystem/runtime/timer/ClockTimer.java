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
package au.edu.anu.twcore.ecosystem.runtime.timer;

import java.time.LocalDateTime;

import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import au.edu.anu.twcore.ecosystem.dynamics.Timeline;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Implementation of Timer with constant time steps
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class ClockTimer extends AbstractTimer {

	/** The time step of this model (the clock tick)*/
	private long dt;
	/** The time unit used by this timer, eg hour or month */
	private TimeUnits timeUnit;
	/** The shortest time unit of its TimeLine*/
	private TimeUnits baseUnit;
	/** The number of timeUnits use to count time ticks in this model, ie 1= 1hour or 1 month, 2
	 * = 2hours or 2 monts */
	private int nTimeUnits;
	/** true if this timer doesnt use calendar time, ie dt is exact */
	private boolean isExact;
	/** number of baseUnits in nTimeUnits*TimeUnit, ie the smallest time tick managed by this timer 
	 * if isExact is false grainsPerBaseUnit will be zero */
	protected long grainsPerBaseUnit;
	/** the number of baseunits (or time grains) to shift the start of this timer at simulation start. 
	 * Use this eg when you want two clock timers with the same units to always run out of sync*/
	private long offset = 0;

//	private LocalDateTime startDateTime;

	public ClockTimer(TimerNode timeModel) {
		super(timeModel);
		this.timeModel = timeModel;
		dt = (long) timeModel.properties().getPropertyValue(P_TIMEMODEL_DT.key());
		timeUnit = (TimeUnits) timeModel.properties().getPropertyValue(P_TIMEMODEL_TU.key());
		nTimeUnits = (Integer) timeModel.properties().getPropertyValue(P_TIMEMODEL_NTU.key());
		baseUnit = ((Timeline)timeModel.getParent()).shortestTimeUnit();
//		startDateTime = ((TimeLine)timeModel.getParent()).getTimeOrigin();
		long f = TimeUtil.timeUnitExactConversionFactor(timeUnit, baseUnit);
		isExact = f>0L;
		if (timeUnit.equals(TimeUnits.UNSPECIFIED))
			grainsPerBaseUnit = nTimeUnits;
		else
			grainsPerBaseUnit = nTimeUnits * f;
		if (timeModel.properties().hasProperty(P_TIMEMODEL_OFFSET.key())) {
			double os = (double) timeModel.properties().getPropertyValue(P_TIMEMODEL_OFFSET.key());
			offset = twTime(os);
		}
	}

	@Override
	public long dt(long time) {
		if (!isExact) {
			// Create the calendar for this time
			LocalDateTime currentDate = TimeUtil.longToDate(time, baseUnit);
			// Create next calendar.
			LocalDateTime nextDate = TimeUtil.getIncrementedDate(currentDate, timeUnit, nTimeUnits);
			// use calendar functions to calculate the difference.
			long result = TimeUtil.timeBetween(currentDate, nextDate, baseUnit);
			return result;
		} 
		else 
			return dt*grainsPerBaseUnit; // in time line units
	}

	@Override
	public void advanceTime(long newTime) {
		lastTime = newTime;
	}

	@Override
	public void preProcess() {
		lastTime = timeOrigin + offset;
	}

	@Override
	public double userTime(long t) {
		if (isExact)
			return (1.0 * t) / grainsPerBaseUnit;
		else
			return TimeUtil.convertTime(t, baseUnit,timeUnit, startDateTime);
	}

	@Override
	public TimeUnits timeUnit() {
		return timeUnit;
	}

	@Override
	public long twTime(double t) {
		if (isExact)
			return Math.round(t*grainsPerBaseUnit);
		else {
			double convF = TimeUtil.convertTime(t, timeUnit, baseUnit, startDateTime);
			return Math.round(convF);
		}
			
	}

}
