package au.edu.anu.twcore.ecosystem.runtime.tracking;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
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
	private long currentTime = Long.MIN_VALUE;
	private DataLabel currentItem = null;
	private Metadata singletonMD = null;
	// metadata for numeric fields, ie min max units etc.
	private ReadOnlyPropertyList fieldMetadata = null;

	public TimeSeriesTracker(Grouping grouping,
			StatisticalAggregatesSet statistics,
			StatisticalAggregatesSet tableStatistics,
			SamplingMode selection,
			boolean viewOthers,
			StringTable track,
			ObjectTable<Class<?>> trackTypes,
			ReadOnlyPropertyList fieldMetadata) {
		super(DataMessageTypes.TIME_SERIES);
		this.fieldMetadata = fieldMetadata;
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(),selection);
		metaprops.setProperty(P_DATATRACKER_GROUPBY.key(),grouping);
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(),statistics);
		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(),tableStatistics);
		metaprops.setProperty(P_DATATRACKER_TRACK.key(),track);
		metadata = new TimeSeriesMetadata();
		DataLabel[] labels = buildLabels(track);
		for (int i=0; i<track.size(); i++) {
			Class<?> c = trackTypes.getWithFlatIndex(i);
			if (c.equals(String.class))
				metadata.addStringVariable(labels[i]);
			else if (c.equals(Double.class) | c.equals(Float.class))
				metadata.addDoubleVariable(labels[i]);
			else 
				metadata.addIntVariable(labels[i]);
		}
		// TODO: fill with appropriate information
		metaprops.setProperty(TimeSeriesMetadata.TSMETA,metadata);
	}
	
	private DataLabel[] buildLabels(StringTable track) {
		DataLabel[] result = new DataLabel[track.size()];
		for (int i=0; i<track.size(); i++)
			result[i] = new DataLabel(track.getWithFlatIndex(i));
		return result;
	}
	
	public void recordTime(long time) {
		currentTime = time;
	}
	
	public void recordItem(String...labels) {
		currentItem = new DataLabel(labels);
	}
	
	public void record(SimulatorStatus status, ReadOnlyPropertyList props) {
		if (hasObservers()) {
			TimeSeriesData tsd = new TimeSeriesData(status,senderId,metadataType,metadata);
			tsd.setTime(currentTime);
			tsd.setItemLabel(currentItem);
			boolean foundOne = false;
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
						foundOne = true;
				}
				for (DataLabel lab:metadata.doubleNames()) 
					if (key.equals(lab.getEnd())) {
						Object o = props.getPropertyValue(key);
						if (o instanceof Double)
							tsd.setValue(lab,(double)o);
						else if (o instanceof Float)
							tsd.setValue(lab,(float)o);							
						foundOne = true;
				}
				for (DataLabel lab:metadata.stringNames()) 
					if (key.equals(lab.getEnd())) {
						tsd.setValue(lab,(String)props.getPropertyValue(key));
						foundOne = true;
				}
			}
			if (foundOne) sendData(tsd);
		}
	}

	@Override
	public Metadata getInstance() {
		if (singletonMD==null) {
			singletonMD = new Metadata(senderId,metaprops); 
			metadataType = singletonMD.type();
			if (fieldMetadata!=null)
				singletonMD.addProperties(fieldMetadata);
		}
		return singletonMD;
	}


}
