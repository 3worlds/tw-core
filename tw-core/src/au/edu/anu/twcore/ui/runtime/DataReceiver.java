package au.edu.anu.twcore.ui.runtime;

import fr.cnrs.iees.rvgrid.observer.Observer;

/**
 * An interface for objects able to receive data from a DataTracker.
 * Descendants of this class must call addRendezvous with the proper  message type
 * in their constructor
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 * 
 * T the type of data understood by this receiver
 * M the type of metadata understood by this receiver
 *
 */
public interface DataReceiver<T,M> extends Observer {

	/** process received data, whatever this means */
	public void onDataMessage(T data);
	
	public void onMetaDataMessage(M meta);
	
}
