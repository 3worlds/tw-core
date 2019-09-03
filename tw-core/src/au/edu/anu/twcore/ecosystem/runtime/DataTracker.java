package au.edu.anu.twcore.ecosystem.runtime;

import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.rvgrid.observer.Observable;

/**
 * An interface for any object that sends data through messages to other objects
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public interface DataTracker<T> extends Observable<DataReceiver<T>> { 
	
	public void removeObserver(DataReceiver<T> observer);
	
	public void sendData (T data);

}
