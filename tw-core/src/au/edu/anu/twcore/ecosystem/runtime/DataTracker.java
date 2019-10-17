package au.edu.anu.twcore.ecosystem.runtime;

import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.rvgrid.observer.Observable;
import fr.ens.biologie.generic.Singleton;

/**
 * <p>An interface for any object that sends data through messages to other objects of the
 * {@linkplain DataReceiver} class.
 * Inherits the {@code addObserver(...)} and {@code sendMessage(...)} methods from
 * {@linkplain Observable}.</p>
 * <p>It assumes a 2 step process: 
 * <ol>
 * <li>The data tracker sends <em>metadata</em> to its
 * observers. The observers use this metadata to perform internal setups preparing them to receive
 * future data messages.</li>
 * <li>The data tracker sends <em>data</em> messages to its observers. These data are processed
 * with the help of the metadata sent at step 1.</li>
 * </ol> 
 * <p>In this process, the implicit assumption is that there is usually <em>one</em> metadata message sent
 * prior to <em>many</em> data messages. The exact meaning of metadata and data is left to
 * implementing classes.</p>
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public interface DataTracker<T,M> extends Observable<DataReceiver<T,M>>, Singleton<M> { 
	
	/**
	 * Removes an observer from this data tracker's list
	 * 
	 * @param observer the observer to remove.
	 */
	public void removeObserver(DataReceiver<T,M> observer);
	
	/**
	 * Sends a data record of class {@code T} to all its observers
	 * 
	 * @param data the data record to send
	 */
	public void sendData (T data);
	
	/**
	 * Sends a metadata record of class {@code M} to all its observers. This should usually be done
	 * before any call to {@code sendData(...)}
	 * 
	 * @param meta the metadata record to send
	 */
	public void sendMetadata(M meta);
	
	/**
	 * Returns true if this data tracker has observers. Use this to optimise data sending
	 * (eg check there is someone observing before building up a costly data record)
	 * 
	 * @return true if this data tracker has observers, false otherwise.
	 */
	public boolean hasObservers();
	
}
