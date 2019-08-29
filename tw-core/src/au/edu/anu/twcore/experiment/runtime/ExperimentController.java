package au.edu.anu.twcore.experiment.runtime;

import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineObserver;

/**
 * The class which communicates with the Deployers (sends commands, receives status)
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public class ExperimentController extends StateMachineObserver {

	public ExperimentController(StateMachineEngine<? extends GridNode> observed) {
		super(observed);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * deploys an experiment and runs it
	 */
	public void deploy() {
		
	}

}
