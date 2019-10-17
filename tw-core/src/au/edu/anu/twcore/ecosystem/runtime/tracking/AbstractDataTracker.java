package au.edu.anu.twcore.ecosystem.runtime.tracking;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.rvgrid.rendezvous.AbstractGridNode;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.ens.biologie.generic.utils.Logging;

/**
 * An ancestor for all kinds of DataTrackers - implements the messaging capacity. All its methods
 * inherited from {@link DataTracker} are implemented as {@code final} to prevent erroneous behaviour.
 * Descendants should just set the {@code T} and {@code M} types, and possibly helper methods for 
 * constructing these objects from the raw data.
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 * @param <T>
 */
public abstract class AbstractDataTracker<T,M> 
		extends AbstractGridNode 
		implements DataTracker<T,M> {
	
	private static Logger log = Logging.getLogger(AbstractDataTracker.class);
	
	private Set<DataReceiver<T,M>> observers = new HashSet<>();
	private int messageType;
	protected int senderId = -1;

	protected AbstractDataTracker(int messageType) {
		super();
		this.messageType = messageType;
	}

	@Override
	public final void addObserver(DataReceiver<T,M> listener) {
		observers.add(listener);
	}

	@Override
	public final void sendMessage(int msgType, Object payload) {
		for (DataReceiver<T,M> dr:observers) 
			if (dr instanceof GridNode)	{
				log.info("Sending data to receiver "+dr.toString());
				GridNode gn = (GridNode) dr;
				RVMessage dataMessage = new RVMessage(msgType,payload,this,gn);
				gn.callRendezvous(dataMessage);
		}
	}

	@Override
	public final void removeObserver(DataReceiver<T,M> observer) {
		observers.remove(observer);
	}

	@Override
	public final void sendData(T data) {
		sendMessage(messageType,data);
	}
	
	@Override
	public final void sendMetadata(M meta) {
		sendMessage(DataMessageTypes.METADATA,meta);
	}

	@Override
	public boolean hasObservers() {
		return !observers.isEmpty();
	}
	
	public void setSender(int id) {
		senderId = id;
	}

	@Override
	public M getInstance() {
		// dummy - for descendants
		return null;
	}

	
}
