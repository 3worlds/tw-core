package au.edu.anu.twcore.ecosystem.runtime.timer;

import au.edu.anu.twcore.ecosystem.dynamics.EventQueue;

/**
 * Implementation of Timer with event-driven scheduling
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class EventTimer extends AbstractTimer {

	public EventTimer(EventQueue eq) {
		super();
		// caution: eventQueue is uninitialised when getting in here
		// TODO Auto-generated constructor stub
	}

	@Override
	public long dt(long time) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void advanceTime(long newTime) {
		// TODO Auto-generated method stub

	}

}
