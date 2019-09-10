package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.DataTrackerStatus;

/**
 * An ancestor class for data being sent from DataTrackers to DataReceivers
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public abstract class OutputData {
	
	private DataTrackerStatus status = null;
	private int senderId = -1;
	private int metadataType = -1;

	public OutputData(DataTrackerStatus status,int senderId,int metadataType) {
		super();
		this.senderId = senderId;
		this.status = status;
		this.metadataType = metadataType;
	}
	
	public DataTrackerStatus status() {
		return status;
	}

	public int sender() {
		return senderId;
	}

	/**
	 * 
	 * @return the metadata type matching this data record
	 */
	public int type() {
		return metadataType;
	}

}
