package au.edu.anu.twcore.ui.runtime;

import au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorStates;
import fr.cnrs.iees.rvgrid.rendezvous.AbstractGridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.rendezvous.RendezvousProcess;
import fr.cnrs.iees.rvgrid.statemachine.State;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineObserver;

/**
 * Ancestor class for widgets able to process status messages
 * 
 * @author Jacques Gignoux - 9 sept. 2019
 *
 */
public abstract class StatusWidget 
		extends AbstractGridNode 
		implements StateMachineObserver {

	public StatusWidget(StateMachineEngine<StatusWidget> statusSender) {
		super();
		statusSender.addObserver(this);
		addRendezvous(new RendezvousProcess() {
			@Override
			public void execute(RVMessage message) {
				if (message.getMessageHeader().type()==statusSender.statusMessageCode()	) {
					State state = (State) message.payload();
					onStatusMessage(state);
				}
			}
		},statusSender.statusMessageCode());
	}
	
	// helper method for this slightly confusing classes (one from rvgrid, the other from twcore
	// Maybe SimulatorStates should be called DeployerStates?
	public static boolean isSimulatorState(State state, SimulatorStates simState) {
		return state.getName().equals(simState.name());
	}
 

}
