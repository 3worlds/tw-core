package au.edu.anu.twcore.ui.runtime;

import au.edu.anu.twcore.ecosystem.runtime.tracking.DataMessageTypes;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.rendezvous.RendezvousProcess;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;

/**
 * An ancestor class for widgets displaying data
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 * @param <T> The type of data sent in messages
 */
public abstract class AbstractDisplayWidget<T,M> 
		extends StatusWidget 
		implements DataReceiver<T,M> {

	protected AbstractDisplayWidget(StateMachineEngine<StatusWidget> statusSender,int dataType) {
		super(statusSender);
		// RV for data messages
		addRendezvous(new RendezvousProcess() {
			@SuppressWarnings("unchecked")
			@Override
			public void execute(RVMessage message) {
				if (message.getMessageHeader().type()==dataType) {
					T data = (T) message.payload();
					onDataMessage(data);
				}
			}
		},dataType);
		// RV for metadata messages
		addRendezvous(new RendezvousProcess() {
			@SuppressWarnings("unchecked")
			@Override
			public void execute(RVMessage message) {
				if (message.getMessageHeader().type()==DataMessageTypes.METADATA) {
					M meta = (M) message.payload();
					onMetaDataMessage(meta);
				}
			}
		},DataMessageTypes.METADATA);

	}

}
