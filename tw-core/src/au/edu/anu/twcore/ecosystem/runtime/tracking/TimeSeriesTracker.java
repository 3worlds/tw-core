package au.edu.anu.twcore.ecosystem.runtime.tracking;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.Collection;
import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.IndexedDataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeSeriesData;
import au.edu.anu.twcore.data.runtime.TimeSeriesMetadata;
import au.edu.anu.twcore.data.runtime.TwData;
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
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(DataMessageTypes.TIME_SERIES);
		this.fieldMetadata = fieldMetadata;
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(),selection);
		metaprops.setProperty(P_DATATRACKER_GROUPBY.key(),grouping);
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(),statistics);
		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(),tableStatistics);
//		metaprops.setProperty(P_DATATRACKER_TRACK.key(),track);
		metadata = new TimeSeriesMetadata();
		for (String s:track) {
			Class<?> c = (Class<?>) fieldMetadata.getPropertyValue(s+"."+P_FIELD_TYPE.key());
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s+"."+P_FIELD_LABEL.key());
			addMetadataVariable(c,l);
		}
		metaprops.setProperty(TimeSeriesMetadata.TSMETA,metadata);
	}
	
	private void addMetadataVariable(Class<?> c, DataLabel lab) {
		if (c.equals(String.class))
			metadata.addStringVariable(lab);
		else if (c.equals(Double.class) | c.equals(Float.class))
			metadata.addDoubleVariable(lab);
		else 
			metadata.addIntVariable(lab);
	}
	
	public void recordTime(long time) {
		currentTime = time;
	}
	
	public void recordItem(String...labels) {
		currentItem = new DataLabel(labels);
	}
		
	// cross-recursive with below
	private void getTableValue(int depth, Table table, int[] index, DataLabel lab, TimeSeriesData tsd) {
		if (table instanceof ObjectTable<?>) {
			ObjectTable<?> t = (ObjectTable<?>) table;
			TwData next = (TwData) t.getByInt(index);
			getRecValue(depth,next,lab,tsd);
		}
		else { // this is a table of primitive types and we are at the end of the label
			if (table instanceof DoubleTable) 
				tsd.setValue(lab,((DoubleTable)table).getByInt(index));
			else if (table instanceof FloatTable) 
				tsd.setValue(lab,((FloatTable)table).getByInt(index));
			else if (table instanceof IntTable) 
				tsd.setValue(lab,((IntTable)table).getByInt(index));
			else if (table instanceof LongTable) 
				tsd.setValue(lab,((LongTable)table).getByInt(index));
			else if (table instanceof BooleanTable) 
				tsd.setValue(lab,((BooleanTable)table).getByInt(index));
			else if (table instanceof ShortTable) 
				tsd.setValue(lab,((ShortTable)table).getByInt(index));
			else if (table instanceof ByteTable) 
				tsd.setValue(lab,((ByteTable)table).getByInt(index));
			else if (table instanceof StringTable) 
				tsd.setValue(lab,((StringTable)table).getByInt(index));
		}
	}
	
	// cross-recursive with above
	private void getRecValue(int depth, TwData root, DataLabel lab, TimeSeriesData tsd) {
		String key = lab.get(depth);
		if (key.contains("["))
			key = key.substring(0,key.indexOf("["));
		if (root.hasProperty(key)) {
			Object next = root.getPropertyValue(key);
			if (next instanceof Table) {
				getTableValue(depth+1,(Table)next,((IndexedDataLabel)lab).getIndex(depth),lab,tsd);
			}
			else { // this is a primitive type and we should be at the end of the label
				if (next instanceof Double)
					tsd.setValue(lab,(double)next);
				else if (next instanceof Float)
					tsd.setValue(lab,(float)next);							
				else if (next instanceof Integer)
					tsd.setValue(lab,(int)next);
				else if (next instanceof Long)
					tsd.setValue(lab,(long)next);							
				else if (next instanceof Boolean)
					tsd.setValue(lab,(boolean)next);							
				else if (next instanceof Short)
					tsd.setValue(lab,(short)next);							
				else if (next instanceof Byte)
					tsd.setValue(lab,(byte)next);
				else if (next instanceof String)
					tsd.setValue(lab,(String)next);
			}
		}
	}
	
	// use this for SystemComponent TwData variables
	public void record(SimulatorStatus status, TwData props) {
		if (hasObservers()) {
			TimeSeriesData tsd = new TimeSeriesData(status,senderId,metadataType,metadata);
			tsd.setTime(currentTime);
			tsd.setItemLabel(currentItem);
			for (DataLabel lab:metadata.intNames())
				getRecValue(0,props,lab,tsd);
			for (DataLabel lab:metadata.doubleNames()) 
				getRecValue(0,props,lab,tsd);
			for (DataLabel lab:metadata.stringNames()) 
				getRecValue(0,props,lab,tsd);
			sendData(tsd);
		}
	}
	
	// use this for simple property lists, eg Population data
	// assumes label = property name
	// TODO: untested!
	// always crashes for me: lab.getEnd is not a key to props - some kind of mix up here
	public void record(SimulatorStatus status, ReadOnlyPropertyList props) {
		if (hasObservers()) {
			TimeSeriesData tsd = new TimeSeriesData(status,senderId,metadataType,metadata);
			tsd.setTime(currentTime);
			tsd.setItemLabel(currentItem);
			for (DataLabel lab:metadata.intNames())
				tsd.setValue(lab,((Number)props.getPropertyValue(lab.getEnd())).longValue());
			for (DataLabel lab:metadata.doubleNames()) 
				tsd.setValue(lab,((Number)props.getPropertyValue(lab.getEnd())).doubleValue());
			for (DataLabel lab:metadata.stringNames()) 
				tsd.setValue(lab,(String)props.getPropertyValue(lab.getEnd()));
			sendData(tsd);
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
