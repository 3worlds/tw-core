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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.IndexedDataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.Output0DData;
import au.edu.anu.twcore.data.runtime.Output0DMetadata;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;
import fr.ens.biologie.generic.utils.Statistics;

/**
 * A data tracker for time series.
 *
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class DataTracker0D extends SamplerDataTracker<CategorizedComponent,Output0DData, Metadata> {

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
	// true if all tracked components are permanent, false if at least one is ephemeral
	private boolean permanentComponents = true;
	
	// statistical aggregators - one per variable
	private Map<String,Statistics> aggregators = new HashMap<>();
	
	public DataTracker0D(int simulatorId,
			StatisticalAggregatesSet statistics,
			StatisticalAggregatesSet tableStatistics,
			SamplingMode selection,
			int sampleSize,
			Collection<CategorizedComponent> samplingPool,
			List<CategorizedComponent> trackedComponents,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(DataMessageTypes.DIM0,simulatorId,selection,sampleSize,samplingPool,trackedComponents);
		this.fieldMetadata = fieldMetadata;
		metaprops = new SimplePropertyListImpl(propertyKeys);
		metaprops.setProperty(P_DATATRACKER_SELECT.key(), selection);
		metaprops.setProperty(P_DATATRACKER_STATISTICS.key(), statistics);
		metaprops.setProperty(P_DATATRACKER_TABLESTATS.key(), tableStatistics);
		metaprops.setProperty(P_DATATRACKER_SAMPLESIZE.key(), sampleSize);
		metadata = new Output0DMetadata();
		for (String s : track) {
			Class<?> c = (Class<?>) fieldMetadata.getPropertyValue(s + "." + P_FIELD_TYPE.key());
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s + "." + P_FIELD_LABEL.key());
			addMetadataVariable(c, l);
			aggregators.put(s,new Statistics());
		}
		metaprops.setProperty(Output0DMetadata.TSMETA, metadata);
		if (!trackedComponents.isEmpty()) {
			for (CategorizedComponent cp: sample)
				if (!cp.isPermanent()) {
					permanentComponents = false;
					break;
			}
		}
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
		for (Statistics stat:aggregators.values())
			stat.reset();
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
	@Override
	public boolean isTracked(CategorizedComponent sc) {
		boolean result = false;
		result = sample.contains(sc);
		if ((!result)&&(sc instanceof SystemComponent)) {
			CategorizedComponent isc = ((SystemComponent)sc).container().initialForItem(sc.id());;
			if (isc != null)
				result = sample.contains(isc);
		}
		return result;
	}

	// use this to select new SystemComponents if some are missing
	// only needed if components are not permanent
	@Override
	public void updateSample() {
		if (!permanentComponents) 
			super.updateSample();
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
