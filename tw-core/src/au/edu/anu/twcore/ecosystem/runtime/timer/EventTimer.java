package au.edu.anu.twcore.ecosystem.runtime.timer;

import au.edu.anu.twcore.ecosystem.dynamics.EventQueue;
import au.edu.anu.twcore.ecosystem.dynamics.TimeModel;

/**
 * Implementation of Timer with event-driven scheduling
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class EventTimer extends AbstractTimer {
	
	private EventQueue eq = null;
	private TimeEvent queueHead;

	public EventTimer(EventQueue eq, TimeModel timeModel) {
		super(timeModel);
		// caution: eventQueue is uninitialised when getting in here
		// TODO Auto-generated constructor stub
	}
	
	private long getEventTime() {
		long qTime = eq.peekTime();
		queueHead = null;
		if (qTime != Long.MAX_VALUE) {
			queueHead=eq.peek();
			return timeModel.modelToBaseTime(qTime);
		}
		else
			return Long.MAX_VALUE;
	}

	@Override
	public long dt(long time) {
		long adt = getEventTime();
		if (adt == Long.MAX_VALUE)
			return adt;
		else
			return adt - lastTime;
	}

	@Override
	public void advanceTime(long newTime) {
		lastTime = newTime;
		eq.poll();
	}
	
	@Override
	public void reset() {
		super.reset();
		eq.clearQueue();
		queueHead = null;
	}
	public TimeEvent getLastEvent() {
		return queueHead;
	}

}
