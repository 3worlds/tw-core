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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import au.edu.anu.twcore.ecosystem.dynamics.EventQueueWriteable;
import au.edu.anu.twcore.ecosystem.dynamics.TimeLine;
import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import au.edu.anu.twcore.exceptions.TwcoreException;
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
	private LocalDateTime startDateTime;

	public EventTimer(TimerNode timeModel) {
		super(timeModel);
		timeUnit = ((TimeLine) timeModel.getParent()).shortestTimeUnit();
		startDateTime = ((TimeLine) timeModel.getParent()).getTimeOrigin();
		queue = new PriorityQueue<TimeEvent>(INITIAL_QUEUE_SIZE, new Comparator<TimeEvent>() {

			@Override
			public int compare(TimeEvent e1, TimeEvent e2) {
				if (e1.getTime() < e2.getTime())
					return -1;
				if (e1.getTime() > e2.getTime())
					return +1;
				return 0;
			}
		});
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
		if (eventTime <= currentTime)
			throw new TwcoreException("Posted event must be in advance of current time. [ currentTime: " + currentTime
					+ ", time: " + eventTime + "]");
		queue.add(new TimeEvent(eventTime));
	}

	@Override
	public long modelTime(double t) {
		throw new TwcoreException("Not implemented");
	}

	@Override
	public double userTime(long t) {
		throw new TwcoreException("Not implemented");
	}

}
