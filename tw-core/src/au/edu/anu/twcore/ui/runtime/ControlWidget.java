package au.edu.anu.twcore.ui.runtime;

import fr.cnrs.iees.rvgrid.statemachine.StateMachineObserver;

/**
 * An ancestor for widgets that can control a state machine. The 'controller' field has methods
 * onStatusMessage(State) which tells the Widget in which state the state machine is, and a
 * sendEvent(Event) method to send events to the state machine. This widget is supposed to
 * send events to the controller in response to user clicks on buttons.
 * 
 * @author Jacques Gignoux - 2 sept. 2019
 *
 */
public abstract class ControlWidget extends AbstractWidget implements StatusProcessor {
	
	protected StateMachineObserver controller;
	
	public ControlWidget(StateMachineObserver controller) {
		super();
		this.controller = controller;
	}
	
}
