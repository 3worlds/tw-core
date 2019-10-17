package au.edu.anu.twcore.ecosystem.runtime.tracking;

import au.edu.anu.twcore.ecosystem.runtime.DataTracker;

/**
 * Interface for objects that carry data trackers sharing the same Metadata type
 * 
 * @author Jacques Gignoux - 17 oct. 2019
 *
 * @param <M> the Metadata type
 */
public interface DataTrackerHolder<M> {
	
	public Iterable<DataTracker<?,M>> dataTrackers();

}
