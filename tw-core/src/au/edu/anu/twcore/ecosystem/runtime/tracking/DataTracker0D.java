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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_FIELD_LABEL;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_FIELD_TYPE;
import static au.edu.anu.twcore.ecosystem.runtime.tracking.TwDataReader.*;

import java.util.Collection;
import java.util.List;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Output0DData;
import au.edu.anu.twcore.data.runtime.Output0DMetadata;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import fr.cnrs.iees.omugi.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.twcore.constants.StatisticalAggregates;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.StatisticalAggregatesSet;

/**
 * A data tracker for time series.
 *
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class DataTracker0D extends AggregatorDataTracker<Output0DData> {

	// metadata for raw data
	private Output0DMetadata metadata;
	// metadata for aggregated data
	private Output0DMetadata aggregatedMetadata;
	// current properties  
	private long currentTime = Long.MIN_VALUE;
	private DataLabel currentItem = null;
	private SimulatorStatus currentStatus = null;
	
	public DataTracker0D(int simulatorId,
			StatisticalAggregatesSet statistics,
			StatisticalAggregatesSet tableStatistics,
			SamplingMode selection,
			int sampleSize,
			Collection<CategorizedComponent> samplingPool,
			String[] samplingPoolIds,
			boolean samplingPoolPermanent,
			List<CategorizedComponent> trackedComponents,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(AbstractDataTracker.DIM0,simulatorId,selection,sampleSize,samplingPool,samplingPoolIds,
			samplingPoolPermanent,trackedComponents,statistics,track,fieldMetadata);
		metadata = new Output0DMetadata();
		for (String s : track) {
			Class<?> c = (Class<?>) fieldMetadata.getPropertyValue(s + "." + P_FIELD_TYPE.key());
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s + "." + P_FIELD_LABEL.key());
			addMetadataVariable(metadata,c, l);
		}
		// for statistical aggregates of data, this is how they will be sent to widgets
		aggregatedMetadata = new Output0DMetadata();
		for (String s : track) {
			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s + "." + P_FIELD_LABEL.key());
			addMetadataVariable(aggregatedMetadata,Double.class, l);
		}
		if (isAggregating())
			metaprops.setProperty(Output0DMetadata.TSMETA, aggregatedMetadata);
		else
			metaprops.setProperty(Output0DMetadata.TSMETA, metadata);
	}

	@Override
	public void openTimeRecord(SimulatorStatus status, long time) {
		currentTime = time;
		currentStatus = status;
		resetStatistics();
	}

	@Override
	public void recordItem(String... labels) {
		currentItem = new DataLabel(labels);
	}

	private void addMetadataVariable(Output0DMetadata meta, Class<?> c, DataLabel lab) {
		if (c.equals(String.class))
			meta.addStringVariable(lab);
		else if (c.equals(Double.class) | c.equals(Float.class))
			meta.addDoubleVariable(lab);
		else
			meta.addIntVariable(lab);
	}
	
	// use this for SystemComponent TwData variables
	@Override
	public void record(TwData... props) {
		if (hasObservers()) {
			// this to handle statistics
			if (isAggregating()) {
				// read all data into a (dummy) message because finding the precise data in 
				// the TwData hierarchy is difficult otherwise
				Output0DData tmp = new Output0DData(currentStatus,senderId, metadataType, metadata);
				for (TwData data:props)
					if (data!=null) {
						for (DataLabel lab : metadata.intNames())
							getValue(data, lab, tmp);
						for (DataLabel lab : metadata.doubleNames())
							getValue(data, lab, tmp);
						for (DataLabel lab : metadata.stringNames())
							getValue(data, lab, tmp);
				}
				// aggregate the data into aggregators
				for (int i=0; i<tmp.getIntValues().length; i++)
					aggregateData(tmp.getIntValues()[i],metadata.intNames().get(i));
				for (int i=0; i<tmp.getDoubleValues().length; i++)
					aggregateData(tmp.getDoubleValues()[i],metadata.doubleNames().get(i));
				for (int i=0; i<tmp.getStringValues().length; i++)
					aggregateData(tmp.getStringValues()[i],metadata.stringNames().get(i));
				// if the item sample is the last for this sample, then send the aggregated message
				if (nAggregated()==sample.size()) { // CAUTION: wont work if trackAll ? yes because sample.size() and not trackSampleSize
//					Output0DData tsd = new Output0DData(status, senderId, metadataType, aggregatedMetadata);
					for (StatisticalAggregates sag:statisticsRequired()) {
						Output0DData tsd = new Output0DData(currentStatus, senderId, metadataType, aggregatedMetadata);
						for (DataLabel lab:variableChannels())
							tsd.setValue(lab, aggregatedValue(lab,sag));
						tsd.setTime(currentTime);
						tsd.setItemLabel(itemName(sag));
						// NB: must not alter msg contents after sending.
						sendData(tsd); // 1 message per statistical aggregate, ie mean, cv, etc.
						// tsd = null;
					}
				}
			}
			else {
				Output0DData tsd = new Output0DData(currentStatus,senderId, metadataType, metadata);
				tsd.setTime(currentTime);
				for (TwData data:props)
					if (data!=null) {
					for (DataLabel lab : metadata.intNames())
						getValue(data, lab, tsd);
					for (DataLabel lab : metadata.doubleNames())
						getValue(data, lab, tsd);
					for (DataLabel lab : metadata.stringNames())
						getValue(data, lab, tsd);
				}
				tsd.setItemLabel(currentItem);
				// NB: must not alter msg contents after sending.
				
				sendData(tsd);
				// tsd = null;
			}
		}
	}

	@Override
	public void closeTimeRecord() {
		// DO NOTHING as messages are sent at every call to record.
	}

}
