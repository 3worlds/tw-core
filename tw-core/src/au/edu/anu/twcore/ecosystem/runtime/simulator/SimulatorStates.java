package au.edu.anu.twcore.ecosystem.runtime.simulator;

import java.util.ArrayList;
import java.util.List;

import fr.cnrs.iees.rvgrid.statemachine.State;
import fr.cnrs.iees.rvgrid.statemachine.Transition;
import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.*;

/**
 * Description of states and transitions of a simulator. This class actually stores the whole 
 * state list and transitions for the simulator
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public enum SimulatorStates {
	
	waiting 	(new State("waiting")),
	stepping 	(new State("stepping",Simulator.stepProc())),
	pausing  	(new State("pausing")),
	running  	(new State("running")),
	quitting  	(new State("quitting")),
	finished  	(new State("finished")),
	;
	
	static {
		waiting.state.addTransition(new Transition(running.state,run.event()));
		waiting.state.addTransition(new Transition(stepping.state,step.event()));
		waiting.state.addTransition(new Transition(quitting.state,quit.event()));
		running.state.addTransition(new Transition(pausing.state,pause.event()));
		running.state.addTransition(new Transition(finished.state,finalise.event()));
		stepping.state.addTransition(new Transition(running.state,goOn.event()));
		stepping.state.addTransition(new Transition(waiting.state,reset.event()));
		stepping.state.addTransition(new Transition(finished.state,finalise.event()));
		stepping.state.addTransition(new Transition(quitting.state,quit.event()));
		stepping.state.addTransition(new Transition(stepping.state,step.event()));
		pausing.state.addTransition(new Transition(running.state,goOn.event()));
		pausing.state.addTransition(new Transition(stepping.state,step.event()));
		pausing.state.addTransition(new Transition(waiting.state,reset.event()));
		pausing.state.addTransition(new Transition(quitting.state,quit.event()));
		finished.state.addTransition(new Transition(quitting.state,quit.event()));
		finished.state.addTransition(new Transition(waiting.state,reset.event()));
	}

	private final State state;
	
	private SimulatorStates(State state) {
		this.state = state;
	}
	
	public State state() {
		return state;
	}
	
	public static Iterable<State> stateList() {
		List<State> states = new ArrayList<>(values().length);
		for (SimulatorStates value: values())
			states.add(value.state());
		return states;
	}
	
}
