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

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import au.edu.anu.twcore.ecosystem.dynamics.Timeline;
import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * Implementation of Timer with event-driven scheduling
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class EventTimer extends AbstractTimer implements EventQueueWriteable {

	private static final int INITIAL_QUEUE_SIZE = 100;
	private Queue<TimeEvent> queue;
	private TimeUnits timeUnit;

	public EventTimer(TimerNode timeModel) {
		super(timeModel);
		timeUnit = ((Timeline) timeModel.getParent()).shortestTimeUnit();
		queue = new PriorityQueue<TimeEvent>(INITIAL_QUEUE_SIZE,
			new Comparator<TimeEvent>() {
				@Override
				public int compare(TimeEvent e1, TimeEvent e2) {
					if (e1.getTime() < e2.getTime())
						return -1;
					if (e1.getTime() > e2.getTime())
						return +1;
					return 0;
				}
			}
		);
	}

	private TimeEvent queueHead;

	// TODO CHECK!! may be crap
	private long getEventTime() {
		long qTime = Long.MAX_VALUE;
		TimeEvent e = queue.peek();
		if (e != null)
			qTime = e.getTime();
		queueHead = null;
		if (qTime != Long.MAX_VALUE) {
			queueHead = queue.peek();
			return qTime;
		} else
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
		queue.poll();
		// warning here because this may be a sign of serious trouble
		if (queue.isEmpty()) {
			log.warning(()->"Event queue is now empty");
		}
	}

	// start the timer by adding an initial event in its own queue
	@Override
	public void preProcess() {
		lastTime = timeOrigin;
//		queue.add(new TimeEvent(timeOrigin));
	}


	// TODO check this!
	@Override
	public void postProcess() {
		super.postProcess();
		queue.clear();
		queueHead = null;
	}

	public TimeEvent getLastEvent() {
		return queueHead;
	}

	@Override
	public void postEvent(double cTime, double time, TimeUnits tu) {
		long currentTime = Math.round(TimeUtil.convertTime(cTime, tu, timeUnit, startDateTime));
		long eventTime = Math.round(TimeUtil.convertTime(time, tu, timeUnit, startDateTime));
		if (eventTime <= currentTime) {
			// issue an error message to end user saying his code is crap.
			log.severe("Next time event (t="+eventTime+") occuring earlier than current time (t="+currentTime+")");
			// fix the problem by settingeventTime to the minimal acceptable difference of 1 time grain
			eventTime = currentTime+1;
		}
		queue.add(new TimeEvent(eventTime));
	}

	@Override
	public void postInitialEvent(double time, TimeUnits tu) {
		long eventTime = timeOrigin + Math.round(TimeUtil.convertTime(time, tu, timeUnit, startDateTime));
		queue.add(new TimeEvent(eventTime));
	}
	
	@Override
	public double userTime(long t) {
		// Since EventTimer.timeUnit = Timeline.shortestTimeUnit, the conversion factor is 1
		return t;
	}
	@Override
	public long twTime(double t) {
		// Since EventTimer.timeUnit = Timeline.shortestTimeUnit, the conversion factor is 1
		return Math.round(t);
	}

	@Override
	public TimeUnits timeUnit() {
		return timeUnit;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		if (!queue.isEmpty()) {
			sb.append(" {t =");
			for (TimeEvent te:queue) {
				sb.append(" ");
				sb.append(te.getTime());
				if (te.getObject()!=null)
					sb.append('*');
			}
			sb.append('}');
		}
		else
			sb.append(" Ø");
		return sb.toString();
	}



}
