package au.edu.anu.twcore.experiment.runtime;

import static java.util.logging.Level.*;

import java.util.logging.Logger;

import fr.cnrs.iees.rvgrid.statemachine.State;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineObserver;

/**
 * The class which communicates with the Deployers (sends commands, receives status)
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public class ExperimentController extends StateMachineObserver {

	private static Logger log = Logger.getLogger(ExperimentController.class.getName());
	// set level to WARNING to stop getting debug information
	static { log.setLevel(INFO); } // debugging info

	public ExperimentController(Deployer observed) {
		super(observed);
		observed.addObserver(this);
	}
	
	/**
	 * deploys an experiment and runs it
	 */
	public void deploy() {
		
	}
	
	@Override
	public void onStatusMessage(State newState) {
		log.info("Oh! simulators now in state "+newState.getName());
	}


}
