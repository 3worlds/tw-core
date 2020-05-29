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

import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * @author Ian Davies
 *
 * @date 14 May 2020
 */
// The interface seen by user code. All they can do is post and event.
// actually not exactly - more interfacting to user code is needed. cf EventQueue interface

public interface EventQueueWriteable {

	/**
	 * Post an TimeEvent to an class (EventTimer). The time will be converted to the
	 * classe's time units (aka the Shortest time unit of the timeline). An
	 * exception is thrown if, after conversion, the currentTime < time.
	 * We need to test the case of currentTime==time. Maybe this can be allowed
	 *
	 * @param cTime Current time of the calling function
	 * @param time Time in units of the calling function
	 * @param tu   TimeUnits of the calling function
	 */
	public void postEvent(double cTime, double time, TimeUnits tu);
}
