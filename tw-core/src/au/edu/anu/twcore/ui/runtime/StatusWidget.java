package au.edu.anu.twcore.ui.runtime;

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

}
