/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            *
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.ecosystem.runtime.tracking;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.IndexedDataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.Output0DData;
import au.edu.anu.twcore.data.runtime.Output0DMetadata;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.DescribedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;

/**
 * A data tracker for time series.
 *
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class DataTracker0D extends AbstractDataTracker<Output0DData, Metadata> {

	// metadata properties
	private static String[] propertyKeys = { P_DATATRACKER_SELECT.key(), 
		P_DATATRACKER_STATISTICS.key(),
		P_DATATRACKER_TABLESTATS.key(), 
		P_DATATRACKER_TRACK.key(), 
		P_DATATRACKER_SAMPLESIZE.key(),
		Output0DMetadata.TSMETA };
	private SimplePropertyList metaprops;
	private Output0DMetadata metadata;
	private int metadataType = -1;
	private Metadata singletonMD = null;
	// metadata for numeric fields, ie min max units etc.
	private ReadOnlyPropertyList fieldMetadata = null;
	
	// current properties  
	private long currentTime = Long.MIN_VALUE;
	private DataLabel currentItem = null;
	private int trackSampleSize = 0;
	private boolean trackAll = false;
	private SamplingMode trackMode;
	// true if all tracked components are permanent, false if at least one is ephemeral
	private boolean permanentComponents = true;
	
	// the list of containers in which to search for new components to track
	private DescribedContainer<CategorizedComponent> samplingPool = null;
//	private Iterable<CategorizedComponent> samplingPool = null;
	// the sampled components
	private Set<CategorizedComponent> sample = new HashSet<>();

	public DataTracker0D(int simulatorId,
			StatisticalAggregatesSet statistics,
			StatisticalAggregatesSet tableStatistics,
			SamplingMode selection,
			int sampleSize,
			DescribedContainer<CategorizedComponent> samplingPool,
			List<CategorizedComponent> trackedComponents,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(DataMessageTypes.DIM0,simulatorId);
		this.fieldMetadata = fieldMetadata;
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(), selection);
		trackMode = selection;
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(), statistics);
		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(), tableStatistics);
		metaprops.setProperty(P_DATATRACKER_SAMPLESIZE.key(), sampleSize);
		trackSampleSize = sampleSize;
		if (trackSampleSize==-1)
			trackAll = true;
		metadata = new Output0DMetadata();
		for (String s : track) {
			Class<?> c = (Class<?>) fieldMetadata.getPropertyValue(s + "." + P_FIELD_TYPE.key());
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s + "." + P_FIELD_LABEL.key());
			addMetadataVariable(c, l);
		}
		metaprops.setProperty(Output0DMetadata.TSMETA, metadata);
		this.samplingPool = samplingPool;
		this.sample.addAll(trackedComponents);
		if (!trackedComponents.isEmpty()) {
			for (CategorizedComponent cp: this.sample)
				if (!cp.isPermanent()) {
					permanentComponents = false;
					break;
			}
		}
		else if (samplingPool!=null) {
			// TODO: get the permanent value from the group characteristics.
			// huh? what does this mean?
		}
//		// TODO: check this is ok for a RNG - do we want other settings?
//		Generator gen = RngFactory.find(rngName);
//		if (gen == null) {
//			gen = RngFactory.newInstance(rngName, 0, RngResetType.never, RngSeedSourceType.secure, RngAlgType.Pcg32);
//			rng = gen.getRandom();
//		} else
//			rng = gen.getRandom();
	}

	private void addMetadataVariable(Class<?> c, DataLabel lab) {
		if (c.equals(String.class))
			metadata.addStringVariable(lab);
		else if (c.equals(Double.class) | c.equals(Float.class))
			metadata.addDoubleVariable(lab);
		else
			metadata.addIntVariable(lab);
	}

	@Override
	public void recordTime(long time) {
		currentTime = time;
	}

	@Override
	public void recordItem(String... labels) {
		currentItem = new DataLabel(labels);
	}

	// cross-recursive with below
	private void getTableValue(int depth, Table table, int[] index, DataLabel lab, Output0DData tsd) {
		if (table instanceof ObjectTable<?>) {
			ObjectTable<?> t = (ObjectTable<?>) table;
			TwData next = (TwData) t.getByInt(index);
			getRecValue(depth, next, lab, tsd);
		} else { // this is a table of primitive types and we are at the end of the label
			if (table instanceof DoubleTable)
				tsd.setValue(lab, ((DoubleTable) table).getByInt(index));
			else if (table instanceof FloatTable)
				tsd.setValue(lab, ((FloatTable) table).getByInt(index));
			else if (table instanceof IntTable)
				tsd.setValue(lab, ((IntTable) table).getByInt(index));
			else if (table instanceof LongTable)
				tsd.setValue(lab, ((LongTable) table).getByInt(index));
			else if (table instanceof BooleanTable)
				tsd.setValue(lab, ((BooleanTable) table).getByInt(index));
			else if (table instanceof ShortTable)
				tsd.setValue(lab, ((ShortTable) table).getByInt(index));
			else if (table instanceof ByteTable)
				tsd.setValue(lab, ((ByteTable) table).getByInt(index));
			else if (table instanceof StringTable)
				tsd.setValue(lab, ((StringTable) table).getByInt(index));
		}
	}

	// cross-recursive with above
	private void getRecValue(int depth, TwData root, DataLabel lab, Output0DData tsd) {
		String key = lab.get(depth);
		if (key.contains("["))
			key = key.substring(0, key.indexOf("["));
		if (root.hasProperty(key)) {
			Object next = root.getPropertyValue(key);
			if (next instanceof Table) {
				getTableValue(depth + 1, (Table) next, ((IndexedDataLabel) lab).getIndex(depth), lab, tsd);
			} else { // this is a primitive type and we should be at the end of the label
				if (next instanceof Double)
					tsd.setValue(lab, (double) next);
				else if (next instanceof Float)
					tsd.setValue(lab, (float) next);
				else if (next instanceof Integer)
					tsd.setValue(lab, (int) next);
				else if (next instanceof Long)
					tsd.setValue(lab, (long) next);
				else if (next instanceof Boolean)
					tsd.setValue(lab, (boolean) next);
				else if (next instanceof Short)
					tsd.setValue(lab, (short) next);
				else if (next instanceof Byte)
					tsd.setValue(lab, (byte) next);
				else if (next instanceof String)
					tsd.setValue(lab, (String) next);
			}
		}
	}

	// use this for SystemComponent TwData variables
	@Override
	public void record(SimulatorStatus status, TwData... props) {
		if (hasObservers()) {
			Output0DData tsd = new Output0DData(status, senderId, metadataType, metadata);
			tsd.setTime(currentTime);
			tsd.setItemLabel(currentItem);
			for (TwData data:props)
				if (data!=null) {
				for (DataLabel lab : metadata.intNames())
					getRecValue(0, data, lab, tsd);
				for (DataLabel lab : metadata.doubleNames())
					getRecValue(0, data, lab, tsd);
				for (DataLabel lab : metadata.stringNames())
					getRecValue(0, data, lab, tsd);
			}
			sendData(tsd);
		}
	}

	// There may be a time bottleneck here
	public boolean isTracked(CategorizedComponent sc) {
		boolean result = false;
		result = sample.contains(sc);
		if ((!result)&&(sc instanceof SystemComponent)) {
			CategorizedComponent isc = samplingPool.initialForItem(sc.id());
			if (isc != null)
				result = sample.contains(isc);
		}
		return result;
	}

	// use this to select new SystemComponents if some are missing
	// only needed if components are not permanent
	@Override
	public void updateTrackList() {
		if (!permanentComponents) {
			if (trackAll) {
				sample.clear();
				for (CategorizedComponent cc:samplingPool.items())
					sample.add(cc);
			}
			else {
				// if we track system components, then sample only contains one group
				// first cleanup the tracked list from components which are gone from the
				// container list
				Iterator<CategorizedComponent> isc = sample.iterator();
				while (isc.hasNext()) {
					if (!samplingPool.contains(isc.next()))
						isc.remove();
				}
				// only a selected subset is tracked
				if (sample.size() < trackSampleSize) {
					// then proceed to selection
					boolean goOn = true;
					switch (trackMode) {
					case FIRST:
						while (goOn) {
							Iterator<? extends CategorizedComponent> list = samplingPool.items().iterator();
							CategorizedComponent next = list.next();
							while (sample.contains(next))
								next = list.next();
							if (next == null)
								goOn = false; // to stop at end of pool when trackSampleSize is too large
							else {
								sample.add(next);
								if (sample.size() == trackSampleSize)
									goOn = false;
							}
						}
						break;
					case RANDOM:
						// calls to random will crash if argument is zero 
						goOn = true;
						LinkedList<CategorizedComponent> ll = new LinkedList<>();
						for (CategorizedComponent sc : samplingPool.items())
							ll.add(sc);
						while (goOn) {
							int i;
							CategorizedComponent next = null;
							if (ll.size()>0)
								do {
									i = rng.nextInt(ll.size());
									next = ll.get(i);
									ll.remove(i); // if already in sample, no need to draw it again
								} while ((sample.contains(next))&&(ll.size()>0));
							if (ll.size()==0)
								goOn = false;
							if (next!=null)
								sample.add(next);
							if (sample.size() == trackSampleSize)
								goOn = false;
						}
						break;
					case LAST:
						goOn = true;
						while (goOn) {
							// reverse the list order
							LinkedList<CategorizedComponent> l = new LinkedList<>();
							for (CategorizedComponent sc : samplingPool.items())
								l.addFirst(sc);
							// as before
							Iterator<CategorizedComponent> list = l.iterator();
							CategorizedComponent next = list.next();
							while (sample.contains(next))
								next = list.next();
							if (next == null)
								goOn = false; // to stop at end of pool when trackSampleSize is too large
							else {
								sample.add(next);
								if (sample.size() == trackSampleSize)
									goOn = false;
							}
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public Metadata getInstance() {
		if (singletonMD == null) {
			singletonMD = new Metadata(senderId, metaprops);
			metadataType = singletonMD.type();
			if (fieldMetadata != null)
				singletonMD.addProperties(fieldMetadata);
		}
		return singletonMD;
	}

}
