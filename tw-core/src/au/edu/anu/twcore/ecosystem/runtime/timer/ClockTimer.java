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
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.ecosystem.dynamics.TimeLine;
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

	public ClockTimer(TimerNode timeModel) {
		super(timeModel);
		this.timeModel = timeModel;
		dt = (long) timeModel.properties().getPropertyValue(P_TIMEMODEL_DT.key());
		timeUnit = (TimeUnits) timeModel.properties().getPropertyValue(P_TIMEMODEL_TU.key());
		nTimeUnits = (Integer) timeModel.properties().getPropertyValue(P_TIMEMODEL_NTU.key());
		baseUnit = ((TimeLine)timeModel.getParent()).shortestTimeUnit();
		isExact = TimeUtil.timeUnitExactConversionFactor(timeUnit, baseUnit)>0L;
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

	@Override
	protected Timer clone() {
		ClockTimer ct = new ClockTimer(timeModel);
		ct.dt = dt;
		ct.baseUnit = baseUnit;
		return ct;
	}

	@Override
	public long modelTime(double t) {
		throw new TwcoreException("Not implemented");
	}

	@Override
	public double userTime(long t) {
		throw new TwcoreException("Not implemented");
	}


}
