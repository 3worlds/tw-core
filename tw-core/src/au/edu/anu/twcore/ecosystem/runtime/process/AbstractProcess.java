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
import java.util.List;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.Spatialized;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import au.edu.anu.twcore.ecosystem.runtime.tracking.GraphDataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.MultipleDataTrackerHolder;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.utils.Logging;

/**
 * An ancestor class for 3Worlds "Processes". The descendant Processes implement different
 * ways of looping on SystemComponents or SystemRelations to apply user-defined functions.
 * @author gignoux - 10 mars 2017
 *
 */
public abstract class AbstractProcess
		implements TwProcess, Sealable, MultipleDataTrackerHolder<Metadata>,
			Spatialized<DynamicSpace<SystemComponent,LocatedSystemComponent>> {

	private static Logger log = Logging.getLogger(AbstractProcess.class);
	
	private boolean sealed = false;
	protected SimulatorStatus currentStatus = SimulatorStatus.Initial;
    private ArenaComponent ecosystem = null;
    // dataTrackers - common to all process types
    // NB: space data trackers are contained into spaces
//	protected List<DataTracker0D> tsTrackers = new LinkedList<DataTracker0D>();
//	protected List<DataTracker2D> mapTrackers = new LinkedList<DataTracker2D>();
	protected Timer timer = null;
	protected DynamicSpace<SystemComponent,LocatedSystemComponent> space = null;
	protected double searchRadius = 0.0;
	protected double currentTime = 0.0;

	protected List<DataTracker<?,Metadata>> trackers = new ArrayList<>();

	public AbstractProcess(ArenaComponent world, Timer timer,
			DynamicSpace<SystemComponent,LocatedSystemComponent> space,
    		double searchR) {
    	super();
    	ecosystem = world;
    	this.timer = timer;
    	this.space = space;
    	searchRadius = searchR;
    }

	@Override
	public final Sealable seal() {
		sealed = true;
//    	trackers.addAll(tsTrackers);
//    	trackers.addAll(mapTrackers);
 		return this;
	}

	@Override
	public final boolean isSealed() {
		return sealed;
	}

	@Override
	public DynamicSpace<SystemComponent,LocatedSystemComponent> space() {
		return space;
	}

	public final TimeUnits timeUnit() {
		return timer.timeUnit();
	}

	public final ArenaComponent ecosystem() {
		return ecosystem;
	}

//	public void setSender(int id) {
//		for (DataTracker<?,Metadata> tracker:trackers)
//			if (tracker instanceof AbstractDataTracker)
//				((AbstractDataTracker<?, Metadata>)tracker).setSender(id);
////		for (DataTracker0D tracker:tsTrackers)
////			tracker.setSender(id);
////		for (DataTracker2D tracker:mapTrackers)
////			tracker.setSender(id);
//	}

	public void addDataTracker(DataTracker<?,Metadata> tracker) {
		if (!isSealed()) {
			trackers.add(tracker);
//			if (tracker instanceof DataTracker0D)
//				tsTrackers.add((DataTracker0D) tracker);
//			else if (tracker instanceof DataTracker2D)
//				mapTrackers.add((DataTracker2D) tracker);
		}
	}

	public final double time() {
		return currentTime;
	}

	@Override
	public Iterable<DataTracker<?,Metadata>> dataTrackers() {
		return trackers;
	}

	@Override
	public final void execute(SimulatorStatus status, long t, long dt) {
		currentStatus = status;
		currentTime = timer.userTime(t);
//		for (DataTracker0D tracker:tsTrackers)
		for (DataTracker<?,Metadata> tracker:trackers)
			tracker.recordTime(t);
		if (space!=null)
			if (space.dataTracker()!=null)
				space.dataTracker().recordTime(status,t);
		
		GraphDataTracker gdt = ecosystem.getDataTracker();
		if (gdt!=null)
			gdt.recordTime(t);
		
		loop(currentTime,timer.userTime(dt),ecosystem());
		if (space!=null)
			if (space.dataTracker()!=null)
				space.dataTracker().closeTimeStep(); // this sends the message to the widget
	}
	
	private double fixCoordinate(double coord, double lower, double upper, double side) {
		while (coord<lower)
			coord += side;
		while (coord>upper)
			coord -= side;
		return coord;
	}

	private double[] applyEdgeEffectCorrection(double[] newLoc) {
		if (newLoc.length!=space.ndim()) {
			log.warning("Wrong number of dimensions: default location generated");
			newLoc = space.defaultLocation();
		}
		switch (space.edgeEffectCorrection()) {
		case bufferAndWrap:
			if (!space.boundingBox().contains(Point.newPoint(newLoc)))
				return null;
			break;
		case bufferZone:
			// TODO
			if (!space.boundingBox().contains(Point.newPoint(newLoc)))
				return null;
			break;
		case noCorrection:
			// TODO
			if (!space.boundingBox().contains(Point.newPoint(newLoc)))
				return null;
			break;
		case wrapAround1D:	
			// crude: assumes the wrapped dimension is first one
			newLoc[0] = fixCoordinate(newLoc[0],
				space.boundingBox().lowerBound(0),
				space.boundingBox().upperBound(0),
				space.boundingBox().sideLength(0));
			break;
		case wrapAround2D:
			// crude: assumes the wrapped dimension are the two first ones
			for (int i=0; i<2; i++)
				newLoc[i] = fixCoordinate(newLoc[i],
					space.boundingBox().lowerBound(i),
					space.boundingBox().upperBound(i),
					space.boundingBox().sideLength(i));
			break;
		case wrapAroundAllD:
			for (int i=0; i<newLoc.length; i++)
				newLoc[i] = fixCoordinate(newLoc[i],
					space.boundingBox().lowerBound(i),
					space.boundingBox().upperBound(i),
					space.boundingBox().sideLength(i));
			break;
		default:
			break;
		}
		return newLoc;
	}
	
	// for descendants
	protected void relocate(SystemComponent sc, double[] newLoc) {
		newLoc = applyEdgeEffectCorrection(newLoc);
		if (newLoc==null) {
			// new location is outside the space - other should be deleted:
			sc.container().removeItem(sc);
		}
		else {
			LocatedSystemComponent newLocSc = new LocatedSystemComponent(sc,space.makeLocation(newLoc));
			space.removeItem(new LocatedSystemComponent(sc,space.locationOf(sc)));
			space.addItem(newLocSc);
			if (space.dataTracker()!=null)
				space.dataTracker().movePoint(newLoc,sc.container().itemId(sc.id()));
		}
	}
	
	// for descendants
	protected void locate(SystemComponent sc, ComponentContainer cont, double[] newLoc) {
		newLoc = applyEdgeEffectCorrection(newLoc);
		if (newLoc==null)
			cont.removeItem(sc);
		else {
			LocatedSystemComponent newLocSc = new LocatedSystemComponent(sc,space.makeLocation(newLoc));
			space.addItem(newLocSc);
			if (space.dataTracker()!=null)
				space.dataTracker().createPoint(newLoc, cont.itemId(sc.id()));
		}	
	}
	
	// for descendants
	// NB: removes a SC from ALL spaces, not only from this one
	protected void unlocate(SystemComponent sc) {
		for (DynamicSpace<SystemComponent,LocatedSystemComponent> space:
			((ComponentFactory)sc.membership()).spaces()) {
			space.removeItem(new LocatedSystemComponent((SystemComponent)sc));
			if (space.dataTracker()!=null)
				space.dataTracker().deletePoint(sc.container().itemId(sc.id()));
		}
	}
	
	/**
	 * Utility for descendants. Fills a hierarchical context from container information
	 *
	 * @param context the context to fill
	 * @param container the container which information is to add to the context
	 */
	protected void setContext(HierarchicalContext context,
			CategorizedContainer<SystemComponent> container) {
//		if (container.containerCategorized() instanceof Ecosystem) {
//			context.ecosystemParameters = container.parameters();
////			context.ecosystemVariables = container.variables();
////			context.ecosystemPopulationData = container.populationData();
//			context.ecosystemName = container.id();
//		}
//		else if (container.containerCategorized() instanceof LifeCycle) {
//			context.lifeCycleParameters = container.parameters();
////			context.lifeCycleVariables = container.variables();
////			context.lifeCyclePopulationData = container.populationData();
//			context.lifeCycleName = container.id();
//		}
//		else if (container.containerCategorized() instanceof SystemFactory)  {
//			context.groupParameters = container.parameters();
////			context.groupVariables = container.variables();
////			context.groupPopulationData = container.populationData();
//			context.groupName = container.id();
//		}
	}

	/**
	 * Utility for descendants. Instantiates and fills a hierarchical context from
	 * component information.
	 *
	 * @param component the component to extract container information from
	 * @return the new instance of the context
	 */
	protected HierarchicalContext getContext(SystemComponent component) {
		HierarchicalContext context = new HierarchicalContext();
		// group or ecosystem
		setContext(context,component.container());
		// lifecycle or ecosystem
		if (component.container().parentContainer()!=null) {
			setContext(context,component.container());
			// ecosystem
			if (component.container().parentContainer().parentContainer()!=null)
				setContext(context,component.container().parentContainer().parentContainer());
		}
		return context;
	}

	public abstract void addFunction(TwFunction function);

	protected abstract void loop(double t, double dt, HierarchicalComponent container);

}
