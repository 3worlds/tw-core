package au.edu.anu.twcore.ecosystem.runtime.tracking;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.IndexedDataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeSeriesData;
import au.edu.anu.twcore.data.runtime.TimeSeriesMetadata;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.rngFactory.RngFactory;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;

/**
 * A data tracker for time series.
 * 
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class TimeSeriesTracker extends AbstractDataTracker<TimeSeriesData,Metadata> {
	
	private static final String rngName = "DataTracker RNG";
	
	private static String[] propertyKeys = {
		P_DATATRACKER_SELECT.key(),
		P_DATATRACKER_STATISTICS.key(),
		P_DATATRACKER_TABLESTATS.key(),
		P_DATATRACKER_TRACK.key(),
		P_DATATRACKER_SAMPLESIZE.key(),
		TimeSeriesMetadata.TSMETA};
	private SimplePropertyList metaprops;
	private TimeSeriesMetadata metadata;
	private int metadataType = -1;
	private long currentTime = Long.MIN_VALUE;
	private DataLabel currentItem = null;
	private Metadata singletonMD = null;
	// metadata for numeric fields, ie min max units etc.
	private ReadOnlyPropertyList fieldMetadata = null;
	private List<SystemContainer> trackedGroups = null;
	private List<SystemComponent> trackedComponents = null;
	private int trackSampleSize = 0;
	private SamplingMode trackMode;
	// true if tracking a group, false if tracking components
	private boolean popTracker; 

	public TimeSeriesTracker(StatisticalAggregatesSet statistics,
			StatisticalAggregatesSet tableStatistics,
			SamplingMode selection,
			int sampleSize,
			List<SystemContainer> trackedGroups,
			List<SystemComponent> trackedComponents,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata,
			boolean trackGroup) {
		super(DataMessageTypes.TIME_SERIES);
		popTracker = trackGroup;
		this.fieldMetadata = fieldMetadata;
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(),selection);
		trackMode = selection;
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(),statistics);
		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(),tableStatistics);
		metaprops.setProperty(P_DATATRACKER_SAMPLESIZE.key(), sampleSize);
		trackSampleSize = sampleSize;
		metadata = new TimeSeriesMetadata();
		for (String s:track) {
			Class<?> c = (Class<?>) fieldMetadata.getPropertyValue(s+"."+P_FIELD_TYPE.key());
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s+"."+P_FIELD_LABEL.key());
			addMetadataVariable(c,l);
		}
		metaprops.setProperty(TimeSeriesMetadata.TSMETA,metadata);
		this.trackedGroups = trackedGroups;
		this.trackedComponents = trackedComponents;
		// TODO: check this is ok for a RNG - do we want other settings?
		if (!RngFactory.exists(rngName))
			RngFactory.makeRandom(rngName,0,RngResetType.never,RngSeedSourceType.secure,RngAlgType.Pcg32);
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
	
	public boolean isTracked(CategorizedContainer<SystemComponent> cc) {
		return (popTracker && trackedGroups.contains(cc));
	}
	
	// There may be a time bottleneck here
	public boolean isTracked(SystemComponent sc) {
		boolean result = false;
		if (!popTracker) {
			result = trackedComponents.contains(sc);
			if (!result) {
				for (CategorizedContainer<SystemComponent> cc : trackedGroups) {
					SystemComponent isc = cc.initialForItem(sc.id());
					if (isc!=null)
						result = trackedComponents.contains(isc);
					if (result)
						break;
				}
			}
		}
		return result;
	}
	
	// use this to select new SystemComponents if some are missing
	@Override
	public void updateTrackList() {
		if (!popTracker) {
			// if we track system components, then trackedGroups only contains one group
			CategorizedContainer<SystemComponent> container = trackedGroups.get(0);
			// no more components to track
			if (container.count()==0) 
				trackedComponents.clear();
			// all components must be tracked
			else if ((trackSampleSize == -1) || // means the whole container is tracked 
				(container.count()<=trackSampleSize)) { // means there are not enough components to select them
				trackedComponents.clear();
				for (SystemComponent sc:container.items())
					trackedComponents.add(sc);
			}
			// only a selected subset is tracked
			else if (trackedComponents.size()<trackSampleSize) {
				// first cleanup the tracked list from components which are gone from the container list
				Iterator<SystemComponent> isc = trackedComponents.iterator();
				while (isc.hasNext()) {
					if (container.item(isc.next().id())==null)
						isc.remove();
				}
				// then proceed to selection
				boolean goOn = true;
				switch (trackMode) {
				case FIRST:	
					while (goOn) {
						Iterator<SystemComponent> list = container.items().iterator();
						SystemComponent next = list.next();
						while (trackedComponents.contains(next))
							next = list.next();
						if (next==null)
							goOn=false;
						else {
							trackedComponents.add(next);
							if (trackedComponents.size() == trackSampleSize)
								goOn = false;
						}
					}
					break;
				case RANDOM:
					goOn = true;
					while (goOn) {
						ArrayList<SystemComponent> l = new ArrayList<>(container.count());
						for (SystemComponent sc:container.items())
							l.add(sc);
						int i =	0;
						SystemComponent next = null;
						while (trackedComponents.contains(next)) {
							i = RngFactory.getRandom(rngName).nextInt(container.count());
							next = l.get(i);
						}
						if (next==null) // this should never happen actually
							goOn=false;
						else {
							trackedComponents.add(next);
							if (trackedComponents.size() == trackSampleSize)
								goOn = false;
						}
					}
					break;
				case LAST:
					goOn = true;
					while (goOn) {
						// reverse the list order
						LinkedList<SystemComponent> l = new LinkedList<>();
						for (SystemComponent sc:container.items())
							l.addFirst(sc);
						// as before
						Iterator<SystemComponent> list = l.iterator();
						SystemComponent next = list.next();
						while (trackedComponents.contains(next))
							next = list.next();
						if (next==null)
							goOn=false;
						else {
							trackedComponents.add(next);
							if (trackedComponents.size() == trackSampleSize)
								goOn = false;
						}
					}
					break;
				}
			}
		}
	}
	
	// use this for simple property lists, eg Population data
	// assumes label = property name
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
