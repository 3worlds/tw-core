package au.edu.anu.twcore.ui.runtime;

import fr.cnrs.iees.rvgrid.statemachine.StateMachineController;

/**
 * An ancestor for widgets that can control a state machine. The 'controller' field has methods
 * onStatusMessage(State) which tells the Widget in which state the state machine is, and a
 * sendEvent(Event) method to send events to the state machine. This widget is supposed to
 * send events to the controller in response to user clicks on buttons.
 * 
 * @author Jacques Gignoux - 2 sept. 2019
 *
 */
@Deprecated
public abstract class ControlWidget implements StatusProcessor {
	
	protected StateMachineController controller;
	
	public ControlWidget(StateMachineController controller) {
		super();
		this.controller = controller;
	}
	
}
