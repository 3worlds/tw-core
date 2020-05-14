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
package au.edu.anu.twcore.ecosystem.runtime;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import au.edu.anu.twcore.ecosystem.dynamics.EventQueueReadable;
import au.edu.anu.twcore.ecosystem.dynamics.EventQueueWriteable;
import au.edu.anu.twcore.ecosystem.runtime.timer.TimeEvent;
import au.edu.anu.twcore.ecosystem.runtime.timer.TimeUtil;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.ens.biologie.generic.Resettable;

/**
 * @author Ian Davies
 *
 * @date 14 May 2020
 */
public class TimerEventQueue implements Resettable, EventQueueWriteable, EventQueueReadable<TimeEvent> {
	private static final int INITIAL_QUEUE_SIZE = 100;
	private Queue<TimeEvent> queue = new PriorityQueue<TimeEvent>(INITIAL_QUEUE_SIZE, new Comparator<TimeEvent>() {

		@Override
		public int compare(TimeEvent e1, TimeEvent e2) {
			if (e1.getTime() < e2.getTime())
				return -1;
			if (e1.getTime() > e2.getTime())
				return +1;
			return 0;
		}
	});
	private TimeUnits to;
	private LocalDateTime startDateTime;

	public TimerEventQueue(TimeUnits tu, LocalDateTime startDateTime) {
		this.to = tu;
		this.startDateTime = startDateTime;
	}

	@Override
	public int postEvent(double time, TimeUnits tu) {
		long eventTime = Math.round(TimeUtil.convertTime(time, tu, to, startDateTime));
		if (queue.isEmpty()) {
			queue.add(new TimeEvent(eventTime));// ?? WRONG not necessarily an advance.
			return 1;
		} else {
			long hoq = queue.peek().getTime();
			if (hoq < eventTime) {
				queue.add(new TimeEvent(eventTime));
				return 1;
			} else
				return 0;
		}
	}

	//  Bug in waiting - Empty queue 
	@Override
	public TimeEvent peek() {
		return queue.peek();
	}

	@Override
	public TimeEvent poll() {
		return queue.poll();
	}

	@Override
	public void reset() {
		queue.clear();

	}

}
