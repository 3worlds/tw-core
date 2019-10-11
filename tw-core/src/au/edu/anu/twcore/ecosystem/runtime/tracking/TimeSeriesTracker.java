package au.edu.anu.twcore.ecosystem.runtime.tracking;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeSeriesData;
import au.edu.anu.twcore.data.runtime.TimeSeriesMetadata;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.twcore.constants.Grouping;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;

/**
 * A data tracker for time series.
 * 
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class TimeSeriesTracker extends AbstractDataTracker<TimeSeriesData,Metadata> {
	
	private static String[] propertyKeys = {
		P_DATATRACKER_SELECT.key(),
		P_DATATRACKER_GROUPBY.key(),
		P_DATATRACKER_STATISTICS.key(),
		P_DATATRACKER_TABLESTATS.key(),
		P_DATATRACKER_VIEWOTHERS.key(),
		P_DATATRACKER_TRACK.key(),
		TimeSeriesMetadata.TSMETA};
	private SimplePropertyList metaprops;
	private TimeSeriesMetadata metadata;
	private int metadataType = -1;
	private int senderId = -1;

	public TimeSeriesTracker(Grouping grouping,
			StatisticalAggregatesSet statistics,
			StatisticalAggregatesSet tableStatistics,
			SamplingMode selection,
			boolean viewOthers) {
		super(DataMessageTypes.TIME_SERIES);
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(),selection);
		metaprops.setProperty(P_DATATRACKER_GROUPBY.key(),grouping);
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(),statistics);
		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(),tableStatistics);
		metaprops.setProperty(P_DATATRACKER_TRACK.key(),null);
		metadata = new TimeSeriesMetadata();
		// TODO: fill with appropriate information
		metaprops.setProperty(TimeSeriesMetadata.TSMETA,metadata);
	}
	
	public Metadata metadata(SimulatorStatus status) {
		Metadata result = new Metadata(status,senderId,metaprops); 
		metadataType = result.type();
		return result;
	}
	
	@Override
	public void setSender(int id) {
		senderId = id;
	}
	
	public void record(SimulatorStatus status, ReadOnlyPropertyList props) {
		if (hasObservers()) {
			TimeSeriesData tsd = new TimeSeriesData(status,senderId,metadataType,metadata);
			for (String key:props.getKeysAsSet()) {
				for (DataLabel lab:metadata.intNames()) 
					if (key.equals(lab.getEnd())) {
						Object o = props.getPropertyValue(key);
						if (o instanceof Integer)
							tsd.setValue(lab,(int)o);
						else if (o instanceof Long)
							tsd.setValue(lab,(long)o);							
						else if (o instanceof Boolean)
							tsd.setValue(lab,(boolean)o);							
						else if (o instanceof Short)
							tsd.setValue(lab,(short)o);							
						else if (o instanceof Byte)
							tsd.setValue(lab,(byte)o);							
				}
				for (DataLabel lab:metadata.doubleNames()) 
					if (key.equals(lab.getEnd())) {
						Object o = props.getPropertyValue(key);
						if (o instanceof Double)
							tsd.setValue(lab,(double)o);
						else if (o instanceof Float)
							tsd.setValue(lab,(float)o);							
				}
				for (DataLabel lab:metadata.stringNames()) 
					if (key.equals(lab.getEnd())) {
						tsd.setValue(lab,(String)props.getPropertyValue(key));
				}

			}
		}
	}

}
