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
