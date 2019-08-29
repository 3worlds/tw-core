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
