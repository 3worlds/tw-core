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

	private long dt;
	private TimeUnits timeUnit;
	private TimeUnits baseUnit;
	private int nTimeUnits;
	private boolean isExact;
	/** if isExact is false grainsPerBaseUnit will be zero */
	protected long grainsPerBaseUnit;

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

	}

	@Override
	public long dt(long time) {
		if (!isExact) {
			// Create the calendar for this time
//			LocalDateTime currentDate = TimeUtil.longToDate(time + startTime, baseUnit);
			LocalDateTime currentDate = TimeUtil.longToDate(time, baseUnit);
			// Create next calendar.
			LocalDateTime nextDate = TimeUtil.getIncrementedDate(currentDate, timeUnit, nTimeUnits);
			// use calendar functions to calculate the difference.
			long result = TimeUtil.timeBetween(currentDate, nextDate, baseUnit);
			return result;
		} else
			return dt;
	}

	@Override
	public void advanceTime(long newTime) {
		lastTime = newTime;
	}

	// Still used???
//	@Override
//	protected Timer clone() {
//		ClockTimer ct = new ClockTimer(timeModel);
//		ct.dt = dt;
//		ct.baseUnit = baseUnit;
//		return ct;
//	}

//	// No longer used
//	@Override
//	public long modelTime(double t) {
//		// convert model time to simulator baseTime
//		if (isExact)
//			return Math.round(t * grainsPerBaseUnit);
//		else {
//			double result = TimeUtil.convertTime(t, timeUnit, baseUnit, startDateTime);
//			result = result * nTimeUnits;
//			return Math.round(result);
//		}
//
//	}

	@Override
	public double userTime(long t) {
		// convert simulator baseTime to model time. Needs checking with Gregorian Timeline
		return (1.0 * t) / grainsPerBaseUnit;
		// if (!exact)
		//TimeUtil.convertTime(t, baseUnit,timeUnit, startDateTime); x some bloody thing?
	}

	@Override
	public TimeUnits timeUnit() {
		return timeUnit;
	}

	@Override
	public long twTime(double t) {
		// cf caution above in userTime
		return Math.round(t*grainsPerBaseUnit);
	}

}
