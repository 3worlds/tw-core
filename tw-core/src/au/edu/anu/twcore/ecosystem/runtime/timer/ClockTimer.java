package au.edu.anu.twcore.ecosystem.runtime.timer;

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
	private boolean runAtTimeZero;
	private TimeModel timeModel;

	public ClockTimer(TimeModel timeModel) {
		super();
		this.timeModel = timeModel;
		dt = (long) timeModel.properties().getPropertyValue("dt");
		runAtTimeZero = (boolean) timeModel.properties().getPropertyValue("runAtTimeZero");

	}

	@Override
	public long dt(long time) {
		if (!timeModel.isExact) {
			TimeUnits baseUnit = timeLine.shortestTimeUnit();
			// Create the calendar for this time
			LocalDateTime currentDate = TimeUtil.longToDate(time + timeLine.getStartTime(), baseUnit);
			// Create next calendar.
			LocalDateTime nextDate = TimeUtil.getIncrementedDate(currentDate, getTimeUnit(), getNTimeUnits());
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

	public boolean runAtTimeZero() {
		return runAtTimeZero;
	}

}
