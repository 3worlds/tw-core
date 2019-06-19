package au.edu.anu.twcore.ecosystem.runtime.timer;

import au.edu.anu.twcore.ecosystem.dynamics.TimeModel;
import au.edu.anu.twcore.ecosystem.runtime.Timer;

public abstract class AbstractTimer implements Timer {

	/** The last time at which this time model was activated */
	protected long lastTime = 0L;
	protected TimeModel timeModel;

	public AbstractTimer(TimeModel timeModel) {
		super();
		this.timeModel = timeModel;
	}

	/**
	 * Computes the next time at which this time model will require activation
	 *
	 * @param time
	 *            the current time
	 * @return the next time for this time model
	 */
	public final long nextTime(long time) {
		long adt = dt(time);
		if (adt != Long.MAX_VALUE)
			return lastTime + adt;
		else
			return Long.MAX_VALUE;
	}

	@Override
	public void reset() {
		lastTime = 0L;
	}

}
