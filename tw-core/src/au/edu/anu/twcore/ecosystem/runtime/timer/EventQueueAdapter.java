package au.edu.anu.twcore.ecosystem.runtime.timer;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;

/**
 * Implementation of EventQueue for use with 3worlds user code
 *
 * @author J. Gignoux - 29 mai 2020
 *
 */
public class EventQueueAdapter implements EventQueue {

	private EventQueueWriteable queue;
	private AbstractProcess process;

	public EventQueueAdapter(EventQueueWriteable queue,TwFunction function) {
		super();
		this.queue = queue;
		process = function.process();
	}

	@Override
	public final void postTimeEvent(double nextTime) {
		queue.postEvent(process.time(), nextTime, process.timeUnit());
	}

}
