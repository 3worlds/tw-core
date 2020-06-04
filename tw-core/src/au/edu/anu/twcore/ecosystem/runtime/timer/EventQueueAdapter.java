package au.edu.anu.twcore.ecosystem.runtime.timer;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;

/**
 * Implementation of EventQueue for use with 3worlds user code
 *
 * @author J. Gignoux - 29 mai 2020
 *
 */
public class EventQueueAdapter implements EventQueue {

	private EventQueueWriteable queue;
	private TwFunction function;

	public EventQueueAdapter(EventQueueWriteable queue,TwFunction function) {
		super();
		this.queue = queue;
		this.function = function;
	}

	@Override
	public final void postTimeEvent(double nextTime) {
		queue.postEvent(function.process().time(), nextTime, function.process().timeUnit());
	}

}
