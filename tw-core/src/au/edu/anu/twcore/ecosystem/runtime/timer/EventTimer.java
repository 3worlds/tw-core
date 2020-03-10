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
	
//	@Override
//	public void reset() {
//		super.reset();
//		eq.clearQueue();
//		queueHead = null;
//	}
	
	@Override
	public void postProcess() {
		super.postProcess();
		eq.clearQueue();
		queueHead = null;
	}

	public TimeEvent getLastEvent() {
		return queueHead;
	}

}
