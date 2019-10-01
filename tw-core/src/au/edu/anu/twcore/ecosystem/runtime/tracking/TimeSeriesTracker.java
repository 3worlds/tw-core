package au.edu.anu.twcore.ecosystem.runtime.tracking;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeSeriesData;
import au.edu.anu.twcore.data.runtime.TimeSeriesMetadata;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.twcore.constants.DataTrackerStatus;
import fr.cnrs.iees.twcore.constants.Grouping;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;

/**
 * A data tracker for time series.
 * 
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class TimeSeriesTracker extends AbstractDataTracker<TimeSeriesData, Metadata> {
	
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
	
	public Metadata metadata(DataTrackerStatus status, int messageType) {
		return new Metadata(status,messageType,metaprops);
	}

}
