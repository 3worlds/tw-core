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

import java.util.*;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.ecosystem.runtime.Spatialized;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import au.edu.anu.twcore.ecosystem.runtime.tracking.MultipleDataTrackerHolder;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SamplerDataTracker;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.cnrs.iees.omhtk.Sealable;

/**
 * An ancestor class for 3Worlds "Processes". The descendant Processes implement different
 * ways of looping on SystemComponents or SystemRelations to apply user-defined functions.
 * @author gignoux - 10 mars 2017
 *
 */
public abstract class AbstractProcess
		implements TwProcess, Sealable, MultipleDataTrackerHolder<Metadata>,
			Spatialized<DynamicSpace<SystemComponent>> {

	private boolean sealed = false;
	protected SimulatorStatus currentStatus = SimulatorStatus.Initial;
    private ArenaComponent ecosystem = null;
	protected Timer timer = null;
	protected DynamicSpace<SystemComponent> space = null;
	protected double searchRadius = 0.0;
	protected int searchNeighbours = 0;
	protected double currentTime = 0.0;
    // dataTrackers - common to all process types
    // NB: space data trackers are contained into spaces
	protected List<SamplerDataTracker<CategorizedComponent,?,Metadata>> trackers = new ArrayList<>();

	public AbstractProcess(ArenaComponent world, Timer timer,
			DynamicSpace<SystemComponent> space,
    		double searchR,
    		int searchN) {
    	super();
    	ecosystem = world;
    	this.timer = timer;
    	this.space = space;
    	searchRadius = searchR;
    	searchNeighbours = searchN;
    }

	@Override
	public final Sealable seal() {
		sealed = true;
 		return this;
	}

	@Override
	public final boolean isSealed() {
		return sealed;
	}

	@Override
	public DynamicSpace<SystemComponent> space() {
		return space;
	}

	public final TimeUnits timeUnit() {
		return timer.timeUnit();
	}

	public final ArenaComponent ecosystem() {
		return ecosystem;
	}

	public void addDataTracker(SamplerDataTracker<CategorizedComponent,?,Metadata> tracker) {
		if (!isSealed())
			trackers.add(tracker);
	}

	public final double time() {
		return currentTime;
	}
	
	public double searchRadius() {
		return searchRadius;
	}

	@Override
	public Collection<SamplerDataTracker<CategorizedComponent,?,Metadata>> dataTrackers() {
		return Collections.unmodifiableCollection(trackers);
	}

	@Override
	public final void execute(SimulatorStatus status, long t, long dt) {
		currentStatus = status;
		currentTime = timer.userTime(t);
//		for (SamplerDataTracker<CategorizedComponent,?,Metadata> tracker:trackers)
//			tracker.recordTime(t);
//		
//		if (space!=null)
//			if (space.dataTracker()!=null)
//				space.dataTracker().openTimeRecord(status,t);
//
//		GraphDataTracker gdt = ecosystem.getDataTracker();
//		if (gdt!=null)
//			gdt.recordTime(t);

		loop(currentTime,timer.userTime(dt),ecosystem());
//		if (space != null)
//			if (space.dataTracker() != null)
//				space.dataTracker().closeTimeRecord(); // this sends the message to the widget
	}

//	/**
//	 * For use in descendant Process classes. This method fixes the user-computed coordinates
//	 * according to space edge effect corrections and relocates a formerly located point
//	 * to its new location
//	 *
//	 * @param sc the SystemComponent to relocate
//	 */
//	protected void relocate(SystemComponent sc) {
//		if (sc.mobile()) { // doesnt make sense for fixed items
//			double[] oldLoc = sc.locationData().coordinates(); 	// always non null
//			double[] newLoc = sc.nextLocationData().coordinates();
//			newLoc = space.fixLocation(newLoc); 		// may be null
//			// 1 the component jumped out of space - it must go - well, unsure.
//			if (newLoc==null) {
//				// new location is outside the space - sc should be deleted:
//				// huh? maybe not if it's present in other spaces ???
//				// Possible flaw here!
//				unlocate(sc);
//				sc.container().removeItem(sc);
//			}
//			// 2 the component didnt move - nothing to do
//			else if (space.equalLocation(oldLoc,newLoc)) {
//				// DO NOTHING
//			}
//			// the component did move
//			else {
//				sc.nextLocationData().setCoordinates(newLoc);
//				space.moveItem(sc);
//				if (space.dataTracker()!=null)
//					space.dataTracker().movePoint(newLoc,sc.container().itemId(sc.id()));
//			}
//		}
//	}

//	/**
//	 * For use in descendant Process classes. This method the user-computed coordinates
//	 * according to space edge effect corrections. This method is called at creation of
//	 * a SystemComponent only, so coordinates may be constants or driver values.
//	 *
//	 * @param sc the SystemComponent to locate
//	 * @param cont the component container, to compute its label
//	 */
//	protected void locate(SystemComponent sc, ComponentContainer cont) {
//		double[] oldLoc, newLoc;
//		if (sc.mobile())
//			oldLoc = sc.nextLocationData().coordinates(); 	// always non null
//		else
//			oldLoc = sc.locationData().coordinates(); 		// always non null
//		newLoc = space.fixLocation(oldLoc);					// may be null
//		// 1 the component jumped out of space - it must go before it's even born
//		if (newLoc==null)
//			cont.removeItem(sc);
//		// 2 the component is created and placed into space
//		else {
//			if (sc.mobile())
//				sc.nextLocationData().setCoordinates(newLoc);
//			else
//				sc.locationData().setCoordinates(newLoc);
//			space.addItem(sc);
//			if (space.dataTracker()!=null)
//				space.dataTracker().createPoint(newLoc, cont.itemId(sc.id()));
//		}
//	}

	// for descendants
	// NB: removes a SC from ALL spaces, not only from this one
//	protected void unlocate(SystemComponent sc) {
//		for (DynamicSpace<SystemComponent> space:
//			((ComponentFactory)sc.membership()).spaces()) {
//			space.removeItem(sc);
//			if (space.dataTracker()!=null) {
//				String[] sclab = sc.container().itemId(sc.id());
//				// lines must be cleared before points
//				for (Edge r:sc.edges(Direction.OUT)) {
//					SystemRelation sr = (SystemRelation) r;
//					SystemComponent end = (SystemComponent)r.endNode();
//					String[] endlab = end.container().itemId(end.id());
//					space.dataTracker().deleteLine(sclab,endlab,sr.type());
//				}
//				for (Edge r:sc.edges(Direction.IN)) {
//					SystemRelation sr = (SystemRelation) r;
//					SystemComponent start = (SystemComponent)r.startNode();
//					String[] startlab = start.container().itemId(start.id());
//					space.dataTracker().deleteLine(startlab,sclab,sr.type());
//				}
//				space.dataTracker().deletePoint(sclab);
//			}
//		}
//	}

	public abstract void addFunction(TwFunction function);

	protected abstract void loop(double t, double dt, HierarchicalComponent container);

}
