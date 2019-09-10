package au.edu.anu.twcore.data.runtime;

import java.util.HashMap;
import java.util.Map;

import fr.cnrs.iees.twcore.constants.DataTrackerStatus;

/**
 * Data for time series.
 * There may be optimisations later (ie not replicating labels all the time)
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class TimeSeriesData extends OutputData {

	private long time;
	private Map<DataLabel,Number> values = new HashMap<>();
	
	public TimeSeriesData(DataTrackerStatus status, 
			int senderId, 
			int metadataType) {
		super(status, senderId, metadataType);
	}
	
	public TimeSeriesData(DataTrackerStatus status, 
			int senderId, 
			int metadataType,
			long time) {
		super(status, senderId, metadataType);
		this.time = time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public void addValue(Number value,DataLabel label) {
		values.put(label,value);
	}

	public void addValue(Number value,String...labelParts) {
		DataLabel label = new DataLabel(labelParts);
		values.put(label,value);
	}

	public Map<DataLabel,Number> values() {
		return values;
	}
	
	public long time() {
		return time;
	}

}
