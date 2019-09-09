package au.edu.anu.twcore.experiment.runtime;

import java.util.logging.Logger;

import au.edu.anu.twcore.ui.runtime.StatusProcessor;
import fr.cnrs.iees.rvgrid.statemachine.State;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineController;
import fr.ens.biologie.generic.utils.Logging;

import static au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorEvents.*;

/**
 * The class which communicates with the Deployers (sends commands, receives status)
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
@Deprecated
public class ExperimentController extends StateMachineController {

	private static Logger log = Logging.getLogger(ExperimentController.class);
	
	private StatusProcessor statusProcessor = null;

	public ExperimentController(Deployer observed) {
		super(observed);
		observed.addObserver(this);
	}
	
	/**
	 * deploys an experiment and runs it. This should be called to run simulators
	 * Maybe this method is not needed (sendEvent can be called directly)
	 */
	public void deploy() {
		sendEvent(run.event());
	}
	
	@Override
	public void onStatusMessage(State newState) {
		log.info("Oh! simulators now in state "+newState.getName());
		if (statusProcessor!=null)
			statusProcessor.processStatus(newState);
	}
	
	public void setStatusProcessor(StatusProcessor sp) {
		statusProcessor = sp;
	}


}
