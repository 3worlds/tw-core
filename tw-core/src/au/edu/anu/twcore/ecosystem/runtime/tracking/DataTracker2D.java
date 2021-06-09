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

import au.edu.anu.twcore.data.runtime.Output2DData;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;


import java.util.Collection;
import java.util.List;

import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;

/**
 * A data tracker for map data
 *
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class DataTracker2D extends SamplerDataTracker<CategorizedComponent,Output2DData, Metadata> {

	private final Metadata metadata;
	private DataLabel currentItem;
	private long currentTime;
	private SimulatorStatus currentStatus;
	protected int metadataType;
	private int nx;
	private int ny;
	public DataTracker2D(int simulatorId, 
			SamplingMode selection, 
			int sampleSize,
			Collection<CategorizedComponent> samplingPool, 
			List<CategorizedComponent> trackedComponents,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(DataMessageTypes.DIM2, simulatorId, selection, sampleSize, samplingPool, trackedComponents);
		senderId = simulatorId;
		metadata = new Metadata(senderId,fieldMetadata);
		// Looks like Output2DData currently assumes 1 table only. Would be better it is followed the Output0DData - IDD
//		for (String s : track) {
//			Class<?> c = (Class<?>) fieldMetadata.getPropertyValue(s + "." + P_FIELD_TYPE.key());
//			DataLabel l = (DataLabel) fieldMetadata.getPropertyValue(s + "." + P_FIELD_LABEL.key());
////			addMetadataVariable(metadata,c, l);
//		}

	}

	@Override
	public void recordItem(String... labels) {
		if (currentItem==null)
		currentItem = new DataLabel(labels);
	}

	@Override
	public Metadata getInstance() {
		return metadata;
	}

	@Override
	public void record(TwData... props) {
		if (hasObservers()) {
			Output2DData tsd = new Output2DData(currentStatus,senderId,metadata.type(),nx,ny);
		}
		
	}

	@Override
	public void openTimeRecord(SimulatorStatus status, long time) {
		currentTime = time;
		currentStatus = status;
		
	}

	@Override
	public void closeTimeRecord() {
		// DO NOTHING as messages are sent at every call to record.
		
	}


}
