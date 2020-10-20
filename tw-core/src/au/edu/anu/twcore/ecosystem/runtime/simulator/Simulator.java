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
package au.edu.anu.twcore.ecosystem.runtime.simulator;

import static fr.cnrs.iees.twcore.constants.SimulatorStatus.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeData;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.dynamics.Timeline;
import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.Sampler;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.space.Location;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import au.edu.anu.twcore.ecosystem.runtime.space.SpaceOrganiser;
import au.edu.anu.twcore.ecosystem.runtime.system.EcosystemGraph;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentData;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.tracking.AbstractDataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataMessageTypes;
import au.edu.anu.twcore.ecosystem.runtime.tracking.GraphDataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.MultipleDataTrackerHolder;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SingleDataTrackerHolder;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.utils.Logging;

/**
 * The class which runs a single simulation on a single parameter set
 *
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public class Simulator implements Resettable {

	// CLASSES

	/** a data tracker to send time data */
	class TimeTracker extends AbstractDataTracker<TimeData, Metadata> {
		private TimeTracker() {
			super(DataMessageTypes.TIME,id);
		}

		// returns quickly if there are no observers - no point building a TimeData
		void sendData(long time) {
			if (hasObservers()) {
				TimeData output = new TimeData(status, id, metadata.type());
//				output.setTime(lastTime);
				output.setTime(time);// used the passed in parameter to avoid side effects.
//				output.setCommunity(ecosystem.community());
//				if (mainSpace!=null)
//					output.setSpaces(mainSpace.spaces());
//				else
//					output.setSpaces(null);
				sendData(output);
			}
		}
		@Override
		public Metadata getInstance() {
			return null;
		}
	}

	// FIELDS

	private static Logger log = Logging.getLogger(Simulator.class);
	/** this simulator's unique id */
	private int id = -1;
	/** helper local field to build up and send metadata to data observers */
	private Metadata metadata;
	/** the list of timers (timeModels) in use in this simulator */
	List<Timer> timerList = null;
	/** the current time of each timer */
	private long[] currentTimes;
	/**
	 * a bit pattern uniquely identifying each timer (NB: this is probably useless
	 * optimisation)
	 */
	private int[] timeModelMasks; // bit pattern for every timeModel
	/** the time origin */
	long startTime = 0L;
	/**
	 * the last time for which a computation with done, ie the one just before
	 * current time
	 */
	protected long lastTime = 0L;
	/**
	 * the stopping condition. There is always exactly one stopping condition. When
	 * there are many, they are organized as a tree
	 */
	protected StoppingCondition stoppingCondition;
	/**
	 * the calling order of processes depending on the combination of simultaneous
	 * time models
	 */
	private Map<Integer, List<List<TwProcess>>> processCallingOrder;
	/** the timeTracker, sending time information to whoever is listening */
	TimeTracker timetracker;
	/** container for all SystemComponents */
	EcosystemGraph ecosystem;
	/** simulator status */
	private SimulatorStatus status = SimulatorStatus.Initial;
	/** all data trackers used in this simulator, together with their metadata */
	private Map<DataTracker<?, Metadata>, Metadata> trackers = new HashMap<>();
	/** all spaces used in this simulation */
//	private Set<DynamicSpace<SystemComponent, LocatedSystemComponent>> spaces = new HashSet<>();
	private SpaceOrganiser mainSpace=null;

	// CONSTRUCTORS

	/**
	 * Every call to this constructor increments N_INSTANCES. Every new instance has
	 * a unique id.
	 *
	 * @param stoppingCondition
	 * @param refTimer
	 * @param timers
	 * @param timeModelMasks
	 * @param processCallingOrder
	 * @param ecosystem
	 * @param haveStoppingConditions 
	 */
	@SuppressWarnings("unchecked")
	public Simulator(int id,
			StoppingCondition stoppingCondition,
			Timeline refTimer,
			List<TimerNode> timeModels,
			List<Timer> timers,
			int[] timeModelMasks,
			Map<Integer, List<List<TwProcess>>> processCallingOrder,
			SpaceOrganiser space,
			EcosystemGraph ecosystem, boolean noStoppingConditions) {
		super();
		log.info("START Simulator " + id + " instantiated");
		this.id = id;
		this.stoppingCondition = stoppingCondition;
//		this.refTimer = refTimer;
		this.timerList = timers;
		this.timeModelMasks = timeModelMasks;
		this.processCallingOrder = processCallingOrder;
		this.ecosystem = ecosystem;
		this.mainSpace = space;
		// looping aids
		currentTimes = new long[timerList.size()];
		// data tracking - record all data trackers and make their metadata
		timetracker = new TimeTracker();
//		timetracker.setSender(id);
		metadata = new Metadata(id, refTimer.properties());
		
		String scDesc = stoppingCondition.toString();
		if (noStoppingConditions)//i.e. not the default stopping condition
			scDesc = "(never)";
		
		// Add the description of the stopping condition for display by widgets if required
		metadata.addProperty("StoppingDesc", scDesc);
		
		trackers.put(timetracker, metadata);
		for (List<List<TwProcess>> llp : processCallingOrder.values())
			for (List<TwProcess> lp : llp)
				for (TwProcess p : lp) {
			if (p instanceof MultipleDataTrackerHolder)
				for (DataTracker<?, Metadata> dt : ((MultipleDataTrackerHolder<Metadata>) p).dataTrackers()) {
					// make metadata
					Metadata meta = dt.getInstance();
					meta.addProperties(refTimer.properties());
					ReadOnlyPropertyList timerProps = findTimerProps(timeModels, p);
					if (timerProps != null)
						meta.addProperties(timerProps);
					trackers.put(dt, meta);
			}
//					if (p instanceof Spatialized<?>) {
//						DynamicSpace<SystemComponent, LocatedSystemComponent> sp = ((Spatialized<DynamicSpace<SystemComponent, LocatedSystemComponent>>) p)
//								.space();
//						if (sp != null)
//							spaces.add(sp);
//					}
		}
		// add system (arena) GraphDataTracker
		GraphDataTracker gdt = ecosystem.arena().getDataTracker();		
		if (gdt!=null)
			trackers.put(gdt,gdt.getInstance());
		
		// add space data trackers to datatracker list
		if (mainSpace!=null)
			for (Space<SystemComponent> sp : mainSpace.spaces())
				if (sp instanceof SingleDataTrackerHolder) {
			SpaceDataTracker dts = (SpaceDataTracker) ((SingleDataTrackerHolder<Metadata>) sp).dataTracker();
			if (dts != null)
				trackers.put(dts, dts.getInstance());
		}
		log.info("END Simulator " + id + " instantiated");
	}

	public int id() {
		return id;
	}

	private ReadOnlyPropertyList findTimerProps(List<TimerNode> timeModels, TwProcess p) {
		for (TimerNode tm : timeModels)
			for (TreeNode tn : tm.getChildren())
				if (tn instanceof ProcessNode)
					if (p == ((ProcessNode) tn).getInstance(id))
						return tm.properties();
		return null;
	}

	// METHODS

	public void addObserver(DataReceiver<TimeData, Metadata> observer) {
		timetracker.addObserver(observer);
		timetracker.sendMetadataTo((GridNode) observer, metadata);
	}

	// run one simulation step
	@SuppressWarnings("unused")
	public void step() {
		status = SimulatorStatus.Active;
		log.info("Time = " + lastTime);
//		timetracker.sendData(lastTime);
		// 1 find next time step by querying timeModels
		long nexttime = Long.MAX_VALUE;
		int i = 0;
		for (Timer tm : timerList) {
			currentTimes[i] = tm.nextTime(lastTime);
			nexttime = Math.min(nexttime, currentTimes[i]);
			i++;
		}
		// advance main timer clock
		if (nexttime == Long.MAX_VALUE)
			status = SimulatorStatus.Final;
		else {
			long step = nexttime - lastTime;
			// send the time as supplied to the processes in this step
			timetracker.sendData(nexttime);
			lastTime = nexttime;
			// 2 find all timeModels which must execute now - using bitmasks for
			// searches
			i = 0;
			int ctmask = 0;
			for (Timer tm : timerList) {
				if (currentTimes[i] == nexttime) {
					ctmask = ctmask | timeModelMasks[i];
				}
				i++;
			}
			// 3 execute all the processes depending on these time models
			// NB sending data to datatrackers is performed in this loop
			List<List<TwProcess>> currentProcesses = processCallingOrder.get(ctmask);
			// loop on dependency rank
			for (int j = 0; j < currentProcesses.size(); j++) {
				List<TwProcess> torun = currentProcesses.get(j);
				// execute all processes at the same dependency level
				for (TwProcess p : torun) {
					p.execute(status, nexttime, step);
				}
			}
			// 3b resetting decorators and population counters to zero for next step
			// only for those processes that were run just before
			if (ecosystem.community()!=null) // TODO improve this treatment
				ecosystem.community().prepareStepAll();
			// 4 advance time ONLY for those time models that were processed
			i = 0;
			for (Timer tm : timerList) {
				if ((timeModelMasks[i] & ctmask) != 0)
					tm.advanceTime(lastTime);
				i++;
			}
			// 5 advance age of ALL SystemComponents, including the not update ones.
			if (ecosystem.community()!=null) // TODO improve this treatment
				for (SystemComponent sc : ecosystem.community().allItems())
				if (sc.autoVar()!=null)
					if (sc.autoVar() instanceof ComponentData){
						ComponentData au = (ComponentData) sc.autoVar();
						au.writeEnable();
						au.age(nexttime - au.birthDate());
						au.writeDisable();
			}
			// apply all changes to community
			ecosystem.effectChanges();
			if (mainSpace!=null)
				for (DynamicSpace<SystemComponent, LocatedSystemComponent> space : mainSpace.spaces())
					space.effectChanges();
			for (DataTracker<?, Metadata> tracker : trackers.keySet())
				if (tracker instanceof Sampler)
					((Sampler<?>)tracker).updateSample();
		}
	}

	/**
	 * recomputes the coordinates of systemComponents after copied from initial
	 * systems recursive.
	 */
	private void computeInitialCoordinates(CategorizedContainer<SystemComponent> container) {
		for (SystemComponent sc : container.items()) {
			Iterable<DynamicSpace<SystemComponent, LocatedSystemComponent>> spaces =
				((ComponentFactory) sc.membership()).spaces();
			for (DynamicSpace<SystemComponent, LocatedSystemComponent> space : spaces) {
				// get the initial item matching this
				SystemComponent isc = container.initialForItem(sc.id());
//				if (isc!=null) // must always be non null, normally
				// get the location of this initial item
				if (space.dataTracker() != null) {
					space.dataTracker().setInitialTime();
					space.dataTracker().recordTime(status,startTime);
				}
				for (LocatedSystemComponent lisc : space.getInitialItems())
					if (lisc.item() == isc) {
						// locate the initial item clone at the location of the initial item
						Location initLoc;
						if (!space.boundingBox().contains(lisc.location().asPoint())) {
							initLoc = space.locate(sc, Point.newPoint(space.defaultLocation()));
						}
						else
							initLoc = space.locate(sc, lisc.location());
						// send coordinates to data tracker if needed
						if (space.dataTracker() != null) {
							double x[] = new double[initLoc.asPoint().dim()];
							for (int i = 0; i < initLoc.asPoint().dim(); i++)
								x[i] = initLoc.asPoint().coordinate(i);
							space.dataTracker().createPoint(x, container.itemId(sc.id()));
						}
				}
				if (space.dataTracker() != null)
					space.dataTracker().closeTimeStep();
			}
		}
		for (CategorizedContainer<SystemComponent> cc : container.subContainers())
			computeInitialCoordinates(cc);
	}

	// postProcess() + preProcess() = reset a simulation at its initial state
	@Override
	public void preProcess() {
		status = Initial;
		stoppingCondition.preProcess();
		for (Timer t : timerList)
			t.preProcess();
		timetracker.sendData(startTime);
		// clones initial items to ecosystem objects
		ecosystem.preProcess();
		// computes coordinates of items just added before
		if (ecosystem.community()!=null)
			computeInitialCoordinates(ecosystem.community());
	}

	@Override
	public void postProcess() {
		status = Final;
		lastTime = startTime;
		stoppingCondition.postProcess();
		for (Timer t : timerList)
			t.postProcess();
		// remove all items from containers
		ecosystem.postProcess();
		// remove all items from spaces
		if (mainSpace!=null)
			for (DynamicSpace<SystemComponent, LocatedSystemComponent> space : mainSpace.spaces())
				space.postProcess();
	}

	// returns true if stopping condition is met
	public boolean stop() {
		boolean finished = false;
		if (status==Active)
			finished = stoppingCondition.stop();
		if (finished)
			status = Final;
		return finished;
	}

	public boolean isStarted() {
		return (status != Initial);
	}

	public boolean isFinished() {
		return (status == Final);
	}

	public long currentTime() {
		return lastTime;
	}

	public ComponentContainer community() {
		return ecosystem.community();
	}
}
