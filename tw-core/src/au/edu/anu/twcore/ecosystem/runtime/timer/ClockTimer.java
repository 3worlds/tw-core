package au.edu.anu.twcore.ecosystem.runtime.timer;

import java.time.LocalDateTime;

import au.edu.anu.twcore.ecosystem.dynamics.TimeModel;
import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * Implementation of Timer with constant time steps
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class ClockTimer extends AbstractTimer {

	private long dt;
// TODO: get rid of this - looks like a flaw
//	private boolean runAtTimeZero;
	// for calendar-based timers
	private TimeUnits baseUnit  = TimeUnits.UNSPECIFIED;
	private long startTime = 0L;

	public ClockTimer(TimeModel timeModel) {
		super(timeModel);
		this.timeModel = timeModel;
		dt = (long) timeModel.properties().getPropertyValue("dt");
//		runAtTimeZero = (boolean) timeModel.properties().getPropertyValue("runAtTimeZero");
		if (!timeModel.isExact()) {
			baseUnit = timeModel.timeLine().shortestTimeUnit();
// TODO - fix this. currently starttime is set in TimePeriod
//			startTime = timeModel.timeLine().getStartTime();
		}
	}

	@Override
	public long dt(long time) {
		if (!timeModel.isExact()) {
			// Create the calendar for this time
			LocalDateTime currentDate = TimeUtil.longToDate(time + startTime, baseUnit);
			// Create next calendar.
			LocalDateTime nextDate = TimeUtil.getIncrementedDate(currentDate, timeModel.timeUnit(), timeModel.nTimeUnits());
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

//	public boolean runAtTimeZero() {
//		return runAtTimeZero;
//	}

}
