package au.edu.anu.twcore.experiment.runtime;

import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.*;
import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorStates.*;

import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineController;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.cnrs.iees.rvgrid.statemachine.Transition;

/**
 * The class which manages Simulators according to experiment size and constraints
 * <ul>
 * <li>receives commands from StateMachineController</li>
 * <li>sends status to StateMachineController</li>
 * <li>transmits commands to Simulator(s)</li>
 * <li>gets status from simulator(s)</li>
 * </ul>
 * understands the same commands as the simulator
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public abstract class Deployer 
		extends StateMachineEngine<StateMachineController>
		implements DeployerProcedures	{

	public Deployer() {
		super(new Transition(waiting.state(),initialise.event()),stateList());
	}

	public void attachSimulator(Simulator sim) {
	}

	public void detachSimulator(Simulator sim) {
	}


	
}
