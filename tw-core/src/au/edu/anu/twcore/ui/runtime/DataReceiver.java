package au.edu.anu.twcore.ui.runtime;

import fr.cnrs.iees.rvgrid.observer.Observer;

/**
 * An interface for objects able to receive data from a DataTracker.
 * Descendants of this class must call addRendezvous with the proper  message type
 * in their constructor
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public interface DataReceiver<T> extends Observer {

	/** process received data, whatever this means */
	public void onDataMessage(T data);
	
}
