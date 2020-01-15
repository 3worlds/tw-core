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
package au.edu.anu.twcore.ecosystem.runtime.simulator;

import fr.cnrs.iees.rvgrid.statemachine.Event;

/**
 * Description of events that can be sent to simulators
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public enum SimulatorEvents {
	
	run  		(new Event(1,"run")),
	step 		(new Event(2,"step")),
	reset 		(new Event(3,"reset")),
	goOn 		(new Event(4,"continue")),
	pause 		(new Event(5,"pause")),
	finalise 	(new Event(6,"finalise",true)),
	quit 		(new Event(7,"quit")),
	initialise 	(new Event(8,"initialise",true))
	;
	
	private final Event event;
	
	private SimulatorEvents(Event event) {
		this.event = event;
	}

	public Event event() {
		return event;
	}
	
	public int messageType() {
		return event.getMessageType();
	}
}
