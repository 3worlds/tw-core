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
package au.edu.anu.twcore.ecosystem.runtime.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTrackerHolder;
import au.edu.anu.twcore.ecosystem.runtime.tracking.LabelValuePairTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.MapTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.TimeSeriesTracker;
import fr.ens.biologie.generic.Sealable;

/**
 * An ancestor class for 3Worlds "Processes". The descendant Processes implement different
 * ways of looping on SystemComponents or SystemRelations to apply user-defined functions.
 * @author gignoux - 10 mars 2017
 *
 */
public abstract class AbstractProcess 
		implements TwProcess, Sealable, DataTrackerHolder<Metadata> {

	private boolean sealed = false;
    private SystemContainer ecosystem = null;
    // dataTrackers - common to all process types
	protected List<TimeSeriesTracker> tsTrackers = new LinkedList<TimeSeriesTracker>();
	protected List<MapTracker> mapTrackers = new LinkedList<MapTracker>();
	protected List<LabelValuePairTracker> simpleTrackers = new LinkedList<LabelValuePairTracker>();
	protected Timer timer = null;

	private List<DataTracker<?,Metadata>> trackers = new ArrayList<>();
    
    public AbstractProcess(SystemContainer world, Timer timer) {
    	super();
    	ecosystem = world;
    	this.timer = timer;
    }

	@Override
	public final Sealable seal() {
		sealed = true;
    	trackers.addAll(tsTrackers);
    	trackers.addAll(mapTrackers);
    	trackers.addAll(simpleTrackers);
		return this;
	}

	@Override
	public final boolean isSealed() {
		return sealed;
	}
	
	public final SystemContainer ecosystem() {
		return ecosystem;
	}
	
	public void setSender(int id) {
		for (TimeSeriesTracker tracker:tsTrackers)
			tracker.setSender(id);
		for (MapTracker tracker:mapTrackers)
			tracker.setSender(id);
		for (LabelValuePairTracker tracker:simpleTrackers)
			tracker.setSender(id);
	}
	
	public abstract void addFunction(TwFunction function);
	
	public void addDataTracker(DataTracker<?,?> tracker) {
		if (!isSealed()) {
			if (tracker instanceof TimeSeriesTracker)
				tsTrackers.add((TimeSeriesTracker) tracker);
			else if (tracker instanceof MapTracker)
				mapTrackers.add((MapTracker) tracker);
			else if (tracker instanceof LabelValuePairTracker)
				simpleTrackers.add((LabelValuePairTracker) tracker);
		}
	}
	
	@Override
	public Iterable<DataTracker<?,Metadata>> dataTrackers() {
		return trackers;
	}

}
