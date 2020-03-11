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

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.twcore.experiment.runtime.deployment.FinishProcedure;
import au.edu.anu.twcore.experiment.runtime.deployment.PauseProcedure;
import au.edu.anu.twcore.experiment.runtime.deployment.QuitProcedure;
import au.edu.anu.twcore.experiment.runtime.deployment.ResetProcedure;
import au.edu.anu.twcore.experiment.runtime.deployment.RunProcedure;
import au.edu.anu.twcore.experiment.runtime.deployment.StepProcedure;
import au.edu.anu.twcore.experiment.runtime.deployment.WaitProcedure;
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
	
	waiting 	(new State("waiting",new WaitProcedure())),
	stepping 	(new State("stepping",new StepProcedure())),
	pausing  	(new State("pausing",new PauseProcedure())),
	running  	(new State("running",new RunProcedure())),
	quitting  	(new State("quitting",new QuitProcedure())),
	finished  	(new State("finished",new FinishProcedure())),
	;
	
	static {
		waiting.state.addTransition(new Transition(running.state,run.event()));
		waiting.state.addTransition(new Transition(stepping.state,step.event()));
		waiting.state.addTransition(new Transition(quitting.state,quit.event()));
		running.state.addTransition(new Transition(pausing.state,pause.event()));
		running.state.addTransition(new Transition(finished.state,finalise.event()));
		stepping.state.addTransition(new Transition(running.state,goOn.event()));
		stepping.state.addTransition(new Transition(waiting.state,reset.event(),new ResetProcedure()));
		stepping.state.addTransition(new Transition(finished.state,finalise.event()));
		stepping.state.addTransition(new Transition(quitting.state,quit.event()));
		stepping.state.addTransition(new Transition(stepping.state,step.event()));
		pausing.state.addTransition(new Transition(running.state,goOn.event()));
		pausing.state.addTransition(new Transition(stepping.state,step.event()));
		pausing.state.addTransition(new Transition(waiting.state,reset.event(),new ResetProcedure()));
		pausing.state.addTransition(new Transition(quitting.state,quit.event()));
		finished.state.addTransition(new Transition(quitting.state,quit.event()));
		finished.state.addTransition(new Transition(waiting.state,reset.event(),new ResetProcedure()));
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
