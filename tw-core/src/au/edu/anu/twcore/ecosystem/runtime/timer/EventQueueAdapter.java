package au.edu.anu.twcore.ecosystem.runtime.timer;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;

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
		// this is for normal functions
		if (function.process()!=null)
			queue.postEvent(function.process().time(), nextTime, function.process().timeUnit());
		// this is only for SetInitialState function, which are called only at t=0
		else
			if (function instanceof SetInitialStateFunction)
				queue.postEvent(0, nextTime, ((SetInitialStateFunction)function).baseTimeUnit());
	}

}
