package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.TimeUnits;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.time.LocalDateTime;
import java.util.BitSet;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import au.edu.anu.twcore.ecosystem.runtime.timer.TimeEvent;
import au.edu.anu.twcore.ecosystem.runtime.timer.TimeUtil;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.InitialisableNode;

/**
 * Class needed by event-driven timer 
 * 
 * @author Ian Davies Jun 4, 2012
 * 
 *         refurbished by J. Gignoux 22/11/2016 - cf comments with 'JG'
 *         redecorated by I. Davies 5/7/2018 - updated for new
 *         calendar-compatible time system
 *
 */
public class EventQueue extends InitialisableNode {

	private class TimeEventComparator implements Comparator<TimeEvent> {
		@Override
		public int compare(TimeEvent e1, TimeEvent e2) {
			if (e1.getTime() < e2.getTime())
				return -1;
			if (e1.getTime() > e2.getTime())
				return +1;
			return 0;
		}
	}
	// Times in queue are in Units x grain of the associated TimeModel
	private static final int INITIAL_QUEUE_SIZE = 100;
	private Queue<TimeEvent> queue = new PriorityQueue<TimeEvent>(INITIAL_QUEUE_SIZE, new TimeEventComparator());
	private TimeUnits to;
	private int nTimeUnits;
	private LocalDateTime startDateTime;

	
	// default constructor
	public EventQueue(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public EventQueue(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		TimeModel timeModel = (TimeModel) getParent();
		startDateTime = timeModel.timeLine.getTimeOrigin();
		to = (TimeUnits) timeModel.properties().getPropertyValue("timeUnit");
		nTimeUnits = (Integer) timeModel.properties().getPropertyValue("nTimeUnits");
	}

	@Override
	public int initRank() {
		return N_EVENTQUEUE.initRank();
	}
	
	public void clearQueue() {
		queue.clear();
	}

	public long peekTime() {
		if (queue.isEmpty())
			return Long.MAX_VALUE;
		else
			return queue.peek().getTime();
	}

	public TimeEvent peek() {
		return queue.peek();
	}

	public TimeEvent poll() {
		return queue.poll();
	}

	/**
	 * These add(...) methods convert a time in the future (delta>0) from the given
	 * TimeUnits and grain to the units of the associated model.
	 */
	private static void check(double delta) {
		if (delta<0.0d)
			throw new TwcoreException("add event in the past: "+delta);
	}
	private long getTimeInQueueUnits(double eventTime, TimeUnits units) {
		long time = Math.round(TimeUtil.convertTime(eventTime, units, to, startDateTime));
		long result = Math.round((double) time / (double) nTimeUnits);
		return result;
	}

	public void add(double time, double delta, TimeUnits units, int nUnits) {
		check(delta);
		TimeEvent te = new TimeEvent(getTimeInQueueUnits(nUnits * (time + delta), units));
		queue.add(te);
	}

	public void add(double time, double delta, TimeUnits units, int nUnits, Object obj) {
		check(delta);
		TimeEvent te = new TimeEvent(getTimeInQueueUnits(nUnits * (time + delta), units), obj);
		queue.add(te);
	}

	public void add(double time, double delta, TimeUnits units, int nUnits, Object obj, BitSet flags) {
		check(delta);
		TimeEvent te = new TimeEvent(getTimeInQueueUnits(nUnits * (time + delta), units), obj, flags);
		queue.add(te);
	}

}
