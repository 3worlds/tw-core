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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.IndexedDataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.Output0DData;
import au.edu.anu.twcore.data.runtime.Output0DMetadata;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.rngFactory.RngFactory;
import au.edu.anu.twcore.rngFactory.RngFactory.Generator;
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
public class DataTracker0D extends AbstractDataTracker<Output0DData, Metadata> {

	private static final String rngName = "DataTracker RNG";

	private static String[] propertyKeys = { P_DATATRACKER_SELECT.key(), P_DATATRACKER_STATISTICS.key(),
			P_DATATRACKER_TABLESTATS.key(), P_DATATRACKER_TRACK.key(), P_DATATRACKER_SAMPLESIZE.key(),
			Output0DMetadata.TSMETA };
	private SimplePropertyList metaprops;
	private Output0DMetadata metadata;
	private int metadataType = -1;
	private long currentTime = Long.MIN_VALUE;
	private DataLabel currentItem = null;
	private Metadata singletonMD = null;
	// metadata for numeric fields, ie min max units etc.
	private ReadOnlyPropertyList fieldMetadata = null;
	private List<ComponentContainer> trackedGroups = null;
	private List<SystemComponent> trackedComponents = null;
	private int trackSampleSize = 0;
	private SamplingMode trackMode;
	// true if tracking a group, false if tracking components
	private boolean popTracker;

	private Random rng;

	public DataTracker0D(StatisticalAggregatesSet statistics, StatisticalAggregatesSet tableStatistics,
			SamplingMode selection, int sampleSize, List<ComponentContainer> trackedGroups,
			List<SystemComponent> trackedComponents, Collection<String> track, ReadOnlyPropertyList fieldMetadata,
			boolean trackGroup) {
		super(DataMessageTypes.TIME_SERIES);
		popTracker = trackGroup;
		this.fieldMetadata = fieldMetadata;
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(), selection);
		trackMode = selection;
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(), statistics);
		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(), tableStatistics);
		metaprops.setProperty(P_DATATRACKER_SAMPLESIZE.key(), sampleSize);
		trackSampleSize = sampleSize;
		metadata = new Output0DMetadata();
		for (String s : track) {
			Class<?> c = (Class<?>) fieldMetadata.getPropertyValue(s + "." + P_FIELD_TYPE.key());
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s + "." + P_FIELD_LABEL.key());
			addMetadataVariable(c, l);
		}
		metaprops.setProperty(Output0DMetadata.TSMETA, metadata);
		this.trackedGroups = trackedGroups;
		this.trackedComponents = trackedComponents;
		// TODO: check this is ok for a RNG - do we want other settings?
		Generator gen = RngFactory.find(rngName);
		if (gen == null) {
			gen = RngFactory.newInstance(rngName, 0, RngResetType.never, RngSeedSourceType.secure, RngAlgType.Pcg32);
			rng = gen.getRandom();
		} else
			rng = gen.getRandom();
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
	public void record(SimulatorStatus status, TwData props) {
		if (hasObservers()) {
			Output0DData tsd = new Output0DData(status, senderId, metadataType, metadata);
			tsd.setTime(currentTime);
			tsd.setItemLabel(currentItem);
			for (DataLabel lab : metadata.intNames())
				getRecValue(0, props, lab, tsd);
			for (DataLabel lab : metadata.doubleNames())
				getRecValue(0, props, lab, tsd);
			for (DataLabel lab : metadata.stringNames())
				getRecValue(0, props, lab, tsd);
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
					if (isc != null)
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
			// first cleanup the tracked list from components which are gone from the
			// container list
			Iterator<SystemComponent> isc = trackedComponents.iterator();
			while (isc.hasNext()) {
				if (!container.contains(isc.next()))
					isc.remove();
			}

			if (false) { // means there are not enough components to select them
//	TODO: CODE BROKEN HERE
//
//			if ((trackSampleSize == -1) || // means the whole container is tracked
//					(container.populationData().count() <= trackSampleSize)) { // means there are not enough components to select them
//				trackedComponents.clear();
//				for (SystemComponent sc : container.items())
//					trackedComponents.add(sc);
			}
			// only a selected subset is tracked
			else if (trackedComponents.size() < trackSampleSize) {
				// then proceed to selection
				boolean goOn = true;
				switch (trackMode) {
				case FIRST:
					while (goOn) {
						Iterator<SystemComponent> list = container.items().iterator();
						SystemComponent next = list.next();
						while (trackedComponents.contains(next))
							next = list.next();
						if (next == null)
							goOn = false;
						else {
							trackedComponents.add(next);
							if (trackedComponents.size() == trackSampleSize)
								goOn = false;
						}
					}
					break;
				case RANDOM:
					goOn = true;
					LinkedList<SystemComponent> ll = new LinkedList<>();
					for (SystemComponent sc : container.items())
						ll.add(sc);
					while (goOn) {
//						int i = RngFactory.find(rngName).getRandom().nextInt(ll.size());
						int i = rng.nextInt(ll.size());
						SystemComponent next = ll.get(i);
						while (trackedComponents.contains(next)) {
//							i = RngFactory.find(rngName).getRandom().nextInt(ll.size());
							i = rng.nextInt(ll.size());
							next = ll.get(i);
						}
						if (next == null) // this should never happen actually
							goOn = false;
						else {
							trackedComponents.add(next);
							if (trackedComponents.size() == trackSampleSize)
								goOn = false;
							else
								ll.remove(next); // remove from drawing list
						}
					}
					break;
				case LAST:
					goOn = true;
					while (goOn) {
						// reverse the list order
						LinkedList<SystemComponent> l = new LinkedList<>();
						for (SystemComponent sc : container.items())
							l.addFirst(sc);
						// as before
						Iterator<SystemComponent> list = l.iterator();
						SystemComponent next = list.next();
						while (trackedComponents.contains(next))
							next = list.next();
						if (next == null)
							goOn = false;
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
			Output0DData tsd = new Output0DData(status, senderId, metadataType, metadata);
			tsd.setTime(currentTime);
			tsd.setItemLabel(currentItem);
			for (DataLabel lab : metadata.intNames())
				tsd.setValue(lab, ((Number) props.getPropertyValue(lab.getEnd())).longValue());
			for (DataLabel lab : metadata.doubleNames())
				tsd.setValue(lab, ((Number) props.getPropertyValue(lab.getEnd())).doubleValue());
			for (DataLabel lab : metadata.stringNames())
				tsd.setValue(lab, (String) props.getPropertyValue(lab.getEnd()));
			sendData(tsd);
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
