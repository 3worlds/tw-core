package au.edu.anu.twcore.data.runtime;

import static java.util.logging.Level.*;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.rvgrid.rendezvous.AbstractGridNode;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;

/**
 * An ancestor for all kinds of DataTrackers - implements the messaging capacity
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 * @param <T>
 */
public abstract class AbstractDataTracker<T> 
		extends AbstractGridNode 
		implements DataTracker<T> {
	
	private static Logger log = Logger.getLogger(AbstractDataTracker.class.getName());
	// set level to WARNING to stop getting debug information
	static { log.setLevel(INFO); } // debugging info
	
	private Set<DataReceiver<T>> observers = new HashSet<>();
	private int messageType;

	protected AbstractDataTracker(int messageType) {
		super();
		this.messageType = messageType;
	}

	@Override
	public final void addObserver(DataReceiver<T> listener) {
		observers.add(listener);
	}

	@Override
	public final void sendMessage(int msgType, Object payload) {
		for (DataReceiver<T> dr:observers) 
			if (dr instanceof GridNode)	{
				log.info("Sending data to receiver "+dr.toString());
				GridNode gn = (GridNode) dr;
				RVMessage dataMessage = new RVMessage(msgType,payload,this,gn);
				gn.callRendezvous(dataMessage);
		}
	}

	@Override
	public final void removeObserver(DataReceiver<T> observer) {
		observers.remove(observer);
	}

	@Override
	public final void sendData(T data) {
		sendMessage(messageType,data);
	}

}