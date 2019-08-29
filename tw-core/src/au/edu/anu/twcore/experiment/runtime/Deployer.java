package au.edu.anu.twcore.experiment.runtime;

import fr.cnrs.iees.rvgrid.statemachine.State;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.cnrs.iees.rvgrid.statemachine.Transition;

/**
 * The class which manages Simulators according to experiment size and constraints
 * <ul>
 * <li>receives commands from ExperimentController</li>
 * <li>sends status to ExperimentController</li>
 * <li>transmits commands to Simulator(s)</li>
 * <li>gets status from simulator(s)</li>
 * </ul>
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public class Deployer extends StateMachineEngine<ExperimentController> {

	public Deployer(Iterable<Transition> initialPseudoStates, Iterable<State> states) {
		super(initialPseudoStates, states);
		// TODO Auto-generated constructor stub
	}


}
