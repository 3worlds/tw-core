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
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTrackerHolder;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTracker2D;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTracker0D;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
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
	protected SimulatorStatus currentStatus = SimulatorStatus.Initial;
    private ComponentContainer ecosystem = null;
    // dataTrackers - common to all process types
	protected List<DataTracker0D> tsTrackers = new LinkedList<DataTracker0D>();
	protected List<DataTracker2D> mapTrackers = new LinkedList<DataTracker2D>();
	protected Timer timer = null;

	private List<DataTracker<?,Metadata>> trackers = new ArrayList<>();
    
    public AbstractProcess(ComponentContainer world, Timer timer) {
    	super();
    	ecosystem = world;
    	this.timer = timer;
    }

	@Override
	public final Sealable seal() {
		sealed = true;
    	trackers.addAll(tsTrackers);
    	trackers.addAll(mapTrackers);
 		return this;
	}

	@Override
	public final boolean isSealed() {
		return sealed;
	}
	
	public final ComponentContainer ecosystem() {
		return ecosystem;
	}
	
	public void setSender(int id) {
		for (DataTracker0D tracker:tsTrackers)
			tracker.setSender(id);
		for (DataTracker2D tracker:mapTrackers)
			tracker.setSender(id);
	}
	
	public void addDataTracker(DataTracker<?,?> tracker) {
		if (!isSealed()) {
			if (tracker instanceof DataTracker0D)
				tsTrackers.add((DataTracker0D) tracker);
			else if (tracker instanceof DataTracker2D)
				mapTrackers.add((DataTracker2D) tracker);
		}
	}
	
	@Override
	public Iterable<DataTracker<?,Metadata>> dataTrackers() {
		return trackers;
	}

	@Override
	public final void execute(SimulatorStatus status, long t, long dt) {
		currentStatus = status;
		for (DataTracker0D tracker:tsTrackers)
			tracker.recordTime(t);		
		loop(ecosystem(),timer.userTime(t),timer.userTime(dt));
	}

	/**
	 * Utility for descendants
	 * @param context
	 * @param container
	 */
	protected void setContext(HierarchicalContext context,
			CategorizedContainer<SystemComponent> container) {
		if (container.categoryInfo() instanceof Ecosystem) {
			context.ecosystemParameters = container.parameters();
			context.ecosystemVariables = container.variables();
			context.ecosystemPopulationData = container.populationData();
			context.ecosystemName = container.id();
		}
		else if (container.categoryInfo() instanceof LifeCycle) {
			context.lifeCycleParameters = container.parameters();
			context.lifeCycleVariables = container.variables();
			context.lifeCyclePopulationData = container.populationData();
			context.lifeCycleName = container.id();
		}
		else if (container.categoryInfo() instanceof SystemFactory)  {
			context.groupParameters = container.parameters();
			context.groupVariables = container.variables();
			context.groupPopulationData = container.populationData();
			context.groupName = container.id();
		}
	}
	
	public abstract void addFunction(TwFunction function);
	
	protected abstract void loop(CategorizedContainer<SystemComponent> container,
			double t, double dt);
	


}
