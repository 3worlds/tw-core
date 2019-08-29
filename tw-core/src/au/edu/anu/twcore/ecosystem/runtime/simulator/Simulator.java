package au.edu.anu.twcore.ecosystem.runtime.simulator;

import au.edu.anu.twcore.experiment.runtime.Deployer;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.statemachine.Procedure;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.cnrs.iees.rvgrid.statemachine.Transition;

import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorStates.*;
import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.*;

/**
 * The class which runs a single simulation on a single parameter set
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public class Simulator extends StateMachineEngine<Deployer> {
	
	private static class StepProcedure extends Procedure {

		@Override
		public void run(GridNode node, RVMessage message) {
			// TODO Auto-generated method stub
			super.run(node, message);
		}
		
	}
	
	static Procedure stepProc() {
		return new StepProcedure();
	}

	public Simulator(Deployer deployer) {
		super(new Transition(waiting.state(),initialise.event()),stateList());
		
	}

}
