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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.OutputXYData;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * A data tracker for simple XY plots.
 * Always tracks only 1 single component
 *
 * @author J. Gignoux - 15 mai 2020
 *
 */
public class DataTrackerXY extends SamplerDataTracker<CategorizedComponent,OutputXYData, Metadata> {

	private long currentTime = 0L;
	private SimulatorStatus currentStatus = null;
	private DataLabel currentItem = null;
	private Metadata metadata = null;
	// TODO: replace these with data labels for diving into the TwData tree
	// but this is already done in the DataTracker0D...
	private String xPropName = null;
	private String yPropName = null;
	// JG: new
	private DataLabel xprop = null;
	private DataLabel yprop = null;

	public DataTrackerXY(int simulatorId,
			SamplingMode selection,
			Collection<CategorizedComponent> trackedGroup,
			List<CategorizedComponent> trackedComponents,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(DataMessageTypes.XY,simulatorId,selection,1,trackedGroup,trackedComponents);
		senderId = simulatorId;
		// Assuming here that fieldMetadata only contains 2 properties
		metadata = new Metadata(senderId,fieldMetadata);
		// the properties are sorted in alphabetical order: first is x, second is y
		SortedSet<String> names = new TreeSet<>();
		names.addAll(metadata.properties().getKeysAsSet());
		int i=0;
		for (String name:names)
			if (name.contains(".hlabel")){
			if (i==0)
				xPropName = name.substring(0,name.indexOf('.'));
			else if (i==1)
				yPropName = name.substring(0,name.indexOf('.'));
			else
				break;
			i++;
		}
		xprop = new DataLabel(xPropName);
		yprop = new DataLabel(yPropName);
	}

	@Override
	public void openTimeRecord(SimulatorStatus status, long time) {
		currentTime = time;
		currentStatus = status;
	}

	@Override
	public void recordItem(String... labels) {
		if (currentItem==null)
			currentItem = new DataLabel(labels);
	}

//	// AT the moment this only works with fields, no tables.
//	private void getRecValue(int depth, TwData root, OutputXYData xyd) {
//		if (root.hasProperty(xPropName)) {
//			Object next = root.getPropertyValue(xPropName);
//			if (next instanceof Table) {
//				// TODO !!
//			}
//			else {
//				if (next instanceof Double)
//					xyd.setX((double) next);
//				else if (next instanceof Float)
//					xyd.setX((Float)next);
//				else if (next instanceof Integer)
//					xyd.setX((int)next);
//				else if (next instanceof Long)
//					xyd.setX((long)next);
//				else if (next instanceof Boolean)
//					xyd.setX((boolean)next);
//				else if (next instanceof Short)
//					xyd.setX((short)next);
//				else if (next instanceof Byte)
//					xyd.setX((byte)next);
//			}
//		}
//		if (root.hasProperty(yPropName)) {
//			Object next = root.getPropertyValue(yPropName);
//			if (next instanceof Table) {
//				// TODO !!
//			}
//			else {
//				if (next instanceof Double)
//					xyd.setY((double) next);
//				else if (next instanceof Float)
//					xyd.setY((Float)next);
//				else if (next instanceof Integer)
//					xyd.setY((int)next);
//				else if (next instanceof Long)
//					xyd.setY((long)next);
//				else if (next instanceof Boolean)
//					xyd.setY((boolean)next);
//				else if (next instanceof Short)
//					xyd.setY((short)next);
//				else if (next instanceof Byte)
//					xyd.setY((byte)next);
//			}
//		}
//	}

	@Override
	public void record(TwData... props) {
		if (hasObservers()) {
			OutputXYData xyd = new OutputXYData(currentStatus,senderId,metadata.type(),xprop,yprop);
			xyd.setTime(currentTime);
			xyd.setItemLabel(currentItem);
			for (TwData data:props)
				if (data!=null) {
//					getRecValue(0,data,xyd);
					TwDataReader.getValue(data,xprop,xyd);
					TwDataReader.getValue(data,yprop,xyd);
			}
			sendData(xyd);
		}
	}

	@Override
	public Metadata getInstance() {
		return metadata;
	}

	@Override
	public void closeTimeRecord() {
		// DO NOTHING as messages are sent at every call to record.
	}


}
