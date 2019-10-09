package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * The class to send metadata from DataTrackers to DataReceivers
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class Metadata extends OutputData {
// If this had a time field (inherited from OutputData) it would be the startTime of the TimeLine (consistent but redundant)
	private static int N_INSTANCES = 0;

	private SimplePropertyList properties = null;

	/**
	 * Constructor for a metadata record. The receiver is supposed to know what to
	 * do with the metadata sent. The constructor works out a unique integer id
	 * linked to this instance of Metadata, which enables to match future output
	 * data records to this metadata instance. This id is accessible through the
	 * type() method.
	 * 
	 * @param status     the status of the DataTracker
	 * @param senderId   the id of the DataTracker
	 * @param properties all the metadata as a property list
	 */
	public Metadata(SimulatorStatus status, int senderId, ReadOnlyPropertyList properties) {
		super(status, senderId, N_INSTANCES++);
		// make a copy of properties
		this.properties = new SimplePropertyListImpl(properties.getKeysAsSet());
		for (String key : properties.getKeysAsSet())
			this.properties.setProperty(key, properties.getPropertyValue(key));
	}

	public SimplePropertyList properties() {
		return properties;
	}
}
