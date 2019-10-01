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
package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.LabelValuePairTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.MapTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.TimeSeriesTracker;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/process/dataTracker" node label in the 
 * 3Worlds configuration tree. Had many properties but needs refactoring.
 *  
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class DataTrackerNode 
		extends InitialisableNode 
		implements Singleton<DataTracker<?,?>>, Sealable {

	private DataTracker<?,?> dataTracker = null;
	private boolean sealed = false;
	
	public DataTrackerNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DataTrackerNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			Object dataTrackerClass = properties().getPropertyValue(P_DATATRACKER_SUBCLASS.key());
			if (dataTrackerClass.equals(TimeSeriesTracker.class.getName())) {	
				dataTracker = new TimeSeriesTracker();
			}		
			else if (dataTrackerClass.equals(MapTracker.class.getName())) {	
				dataTracker = new MapTracker();
			}		
			else if (dataTrackerClass.equals(LabelValuePairTracker.class.getName())) {	
				dataTracker = new LabelValuePairTracker();
			}		
			// optional properties - if absent take default value
			properties().getPropertyValue(P_DATATRACKER_SELECT.key());
			properties().getPropertyValue(P_DATATRACKER_GROUPBY.key());
			properties().getPropertyValue(P_DATATRACKER_STATISTICS.key());
			properties().getPropertyValue(P_DATATRACKER_VIEWOTHERS.key());
			// the only required property.
			properties().getPropertyValue(P_DATATRACKER_TRACK.key());
			// TODO - implement behaviour...
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_DATATRACKER.initRank();
	}

	@Override
	public DataTracker<?,?> getInstance() {
		if (!sealed)
			initialise();
		return dataTracker;
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

}
