package au.edu.anu.twcore.ui.runtime;

import au.edu.anu.twcore.data.runtime.DataMessageTypes;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.rendezvous.RendezvousProcess;

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

	protected AbstractDisplayWidget(int statusType,int dataType) {
		super(statusType);
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
