package au.edu.anu.twcore.ui.runtime;

import fr.cnrs.iees.rvgrid.rendezvous.AbstractGridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.rendezvous.RendezvousProcess;

/**
 * An ancestor class for widgets displaying data
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 * @param <T> The type of data sent in messages
 */
public abstract class AbstractDisplayWidget<T> 
		extends AbstractGridNode 
		implements DataReceiver<T>, Widget {

	protected AbstractDisplayWidget(int messageType) {
		super();
		addRendezvous(new RendezvousProcess() {
			@SuppressWarnings("unchecked")
			@Override
			public void execute(RVMessage message) {
				if (message.getMessageHeader().type()==messageType) {
					T data = (T) message.payload();
					onDataMessage(data);
				}
			}
		},messageType);
	}

}
