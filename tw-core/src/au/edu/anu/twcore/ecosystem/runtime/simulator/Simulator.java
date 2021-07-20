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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_TIMELINE_TIMEORIGIN;
import static fr.cnrs.iees.twcore.constants.SimulatorStatus.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.graph.property.Property;
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
import au.edu.anu.twcore.ecosystem.runtime.process.SearchProcess;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.ObserverDynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import au.edu.anu.twcore.ecosystem.runtime.space.SpaceOrganiser;
import au.edu.anu.twcore.ecosystem.runtime.system.EcosystemGraph;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentData;
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
import fr.cnrs.iees.twcore.constants.DateTimeType;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
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
		@Override
		public Metadata getInstance() {
			return null;
		}
		// returns quickly if there are no observers - no point building a TimeData
		@Override
		public void openTimeRecord(SimulatorStatus status, long time) {
			if (hasObservers()) {
				TimeData output = new TimeData(status, id, metadata.type());
				output.setTime(time);
				sendData(output);
			}
		}
		@Override
		public void closeTimeRecord() {}
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
	private SpaceOrganiser mainSpace=null;
	
	private List<Property> expProperties;

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
		this.id = id;
		log.info(()->"START Simulator " + this.id + " instantiated");
		this.stoppingCondition = stoppingCondition;
//		this.refTimer = refTimer;
		this.timerList = timers;
		this.timeModelMasks = timeModelMasks;
		this.processCallingOrder = processCallingOrder;
		this.ecosystem = ecosystem;
		this.mainSpace = space;
		// looping aids----------------------------------------------------------------
		currentTimes = new long[timerList.size()];
		if (refTimer.properties().hasProperty(P_TIMELINE_TIMEORIGIN.key()))
			startTime = ((DateTimeType)refTimer.properties()
				.getPropertyValue(P_TIMELINE_TIMEORIGIN.key())).getDateTime();
		// time line metadata for data trackers----------------------------------------
		metadata = new Metadata(id, refTimer.properties());
		// make sure a default value is there for optional properties
		if (!refTimer.properties().hasProperty(P_TIMELINE_TIMEORIGIN.key())) {
			DateTimeType dtt = new DateTimeType(0L);
			metadata.addProperty(P_TIMELINE_TIMEORIGIN.key(),dtt);
		}
		// stopping conditions metadata for data trackers
		String scDesc = stoppingCondition.toString();
		if (noStoppingConditions)//i.e. not the default stopping condition
			scDesc = "(never)";
		metadata.addProperty("StoppingDesc", scDesc);
		// data tracking - record all data trackers------------------------------------
		// time tracker
		timetracker = new TimeTracker();
		trackers.put(timetracker, metadata);
		// ComponentProcess data trackers
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
		}
		// system (arena) GraphDataTracker
		GraphDataTracker gdt = ecosystem.arena().getDataTracker();
		if (gdt!=null)
			trackers.put(gdt,gdt.getInstance());
		// space data trackers
		if (mainSpace!=null)
			for (Space<SystemComponent> sp : mainSpace.spaces())
				if (sp instanceof SingleDataTrackerHolder) {
			SpaceDataTracker dts = (SpaceDataTracker) ((SingleDataTrackerHolder<Metadata>) sp).dataTracker();
			if (dts != null)
				trackers.put(dts, dts.getInstance());
		}
		log.info(()->"END Simulator " + this.id + " instantiated");
	}
	
	public void setExpProperties(List<Property> expProperties) {
		this.expProperties=expProperties;
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
	public void addTimeTracker(DataReceiver<TimeData, Metadata> observer) {
		timetracker.addObserver(observer);
		timetracker.sendMetadataTo((GridNode) observer, metadata);
	}

	// TIME LOOP: run one simulation step
	@SuppressWarnings({ "unused" })
	public synchronized void step() {
		status = SimulatorStatus.Active;
		log.info(()->"START Simulator " + id +" stepping time = " + lastTime);
		// 1 //
		// find next time step by querying timeModels
		long nexttime = Long.MAX_VALUE;
		int i = 0;
		for (Timer tm : timerList) {
			currentTimes[i] = tm.nextTime(lastTime);
			nexttime = Math.min(nexttime, currentTimes[i]);
			i++;
		}
		// stop simulation if time reached infinity
		if (nexttime == Long.MAX_VALUE)
			status = SimulatorStatus.Final;
		// otherwise proceed with computations for this time step
		else {
			long step = nexttime - lastTime;
			// start recording data for this time step in all data trackers
			for (DataTracker<?, Metadata> tracker : trackers.keySet())
				tracker.openTimeRecord(status, nexttime);
			lastTime = nexttime;
			// 2 //
			// find all timeModels which must execute now - using bitmasks for searches
			i = 0;
			int ctmask = 0;
			for (Timer tm : timerList) {
				if (currentTimes[i] == nexttime) {
					ctmask = ctmask | timeModelMasks[i];
				}
				i++;
			}
			// 3 //
			// CAUSAL LOOP: loop on dependency rank within a time step and
			// execute all the processes depending on these time models
			// drivers and graph structure are updated at the end of each causal step
			// decorators are not; they are set to zero at the end of the time step
			//
			List<List<TwProcess>> currentProcesses = processCallingOrder.get(ctmask);
			for (int j = 0; j < currentProcesses.size(); j++) {
				List<TwProcess> torun = currentProcesses.get(j);
				// prepare data trackers for recording (important for space data trackers only)
				for (DataTracker<?, Metadata> tracker : trackers.keySet())
					tracker.openRecord();
				// execute all processes at the same dependency level
				for (TwProcess p : torun) {
					p.execute(status, nexttime, step);
				}
				// 5 apply all changes to community (structure and state)
				updateStateAndStructure(nexttime,step);
				// tell all data trackers to flush data and to readapt to changes in community
				for (DataTracker<?, Metadata> tracker : trackers.keySet()) {
					tracker.closeRecord();
					// resample community for data trackers who need it
					if (tracker instanceof Sampler)
						((Sampler<?>)tracker).updateSample();
				}
			}
			// 3b
			// resetting decorators and population counters to zero for next step
			setDecoratorsToZero();
			// 4
			// advance time ONLY for those time models that were processed
			i = 0;
			for (Timer tm : timerList) {
				if ((timeModelMasks[i] & ctmask) != 0)
					tm.advanceTime(lastTime);
				i++;
			}
			// 6 advance age of ALL SystemComponents, including the not update ones.
			if (ecosystem.community()!=null) // TODO improve this treatment
				for (SystemComponent sc : ecosystem.community().allItems())
					if (sc.autoVar()!=null)
						if (sc.autoVar() instanceof ComponentData) {
							ComponentData au = (ComponentData) sc.autoVar();
							au.writeEnable();
							au.age(nexttime - au.birthDate());
							au.writeDisable();
			}
			for (DataTracker<?, Metadata> tracker : trackers.keySet()) {
				// stop recording data in all data trackers
				tracker.closeTimeRecord();
			}
		}
		log.info(()->"END Simulator " + id +" stepping time = " + lastTime);
	} // step()

	// helper method for step()
	// resetting decorators and population counters to zero for next step
	// only for those processes that were run just before (as indicated by the changed() method in
	// ComponentContainer).
	private void setDecoratorsToZero() {
		if (ecosystem.community()!=null)
			ecosystem.community().prepareStepAll();
	}

	// helper method for step()
	// update state and structure at the end of every causal step
	@SuppressWarnings("unchecked")
	private void updateStateAndStructure(long nexttime, long step) {
		Collection<SystemComponent> newComp = ecosystem.effectChanges();
		// apply changes to spaces
		if (mainSpace!=null) {
			for (DynamicSpace<SystemComponent> space : mainSpace.spaces()) {
				space.effectChanges();
				// handle components that left the space (oblivion edge effect)
				for (SystemComponent sc:space.outOfSpaceItems()) {
					ComponentContainer c = (ComponentContainer) sc.container();
					c.removeItemNow(sc);
					sc.detachFromContainer(); // important: cannot be done inside removeItemNow() --> crash
				}
				space.outOfSpaceItems().clear();
			}
		}
		// set permanent relation for newly created (and located) systems
		setPermanentRelations(newComp,nexttime,step);
		for (RelationContainer rc:ecosystem.relations())
			if (rc.isPermanent())
				rc.effectChanges();
	}

	// helper method for step()
	// establish permanent relations at creation of SystemComponents
	private void setPermanentRelations(Collection<SystemComponent> comps, long time, long timeStep) {
		for (List<List<TwProcess>> llp:processCallingOrder.values())
			for (List<TwProcess> lp:llp)
				for (TwProcess p:lp)
					if (p instanceof SearchProcess) {
						SearchProcess proc = (SearchProcess) p;
						if (proc.isPermanent())
							proc.setPermanentRelations(comps,ecosystem.community(),time,timeStep);
					}
	}

	// postProcess() + preProcess() = reset a simulation at its initial state
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void preProcess() {
		status = Initial;
		log.info(()->"START Simulator " + id + " reset/pre");
		lastTime = startTime;
		stoppingCondition.preProcess();
		for (Timer t : timerList)
			t.preProcess();
		timetracker.openTimeRecord(status,startTime);
		timetracker.closeTimeRecord(); // does nothing but for code consistency
		// make spaces listen to changes in ecosystem
		// NB: only containers which components have coordinates can be observed
		if (mainSpace!=null)
			for (ObserverDynamicSpace space:mainSpace.spaces()) {
				ecosystem.addObserver(space);
				if (space.dataTracker() != null) {
					space.dataTracker().setInitialTime();
					space.dataTracker().openTimeRecord(status,startTime);
					space.dataTracker().openRecord();
				}
			}
		// clones initial items to ecosystem objects
		ecosystem.preProcess();
		// as a first attempt, we assume global (arena) constants only to don't make this apply to components
		if (expProperties!=null) {
			ecosystem.arena().applyExperimentProperties(expProperties);
		}
		// update spaces and send data for display
		if (mainSpace!=null)
			for (ObserverDynamicSpace space : mainSpace.spaces()) {
				space.effectChanges();
				if (space.dataTracker() != null) {
					space.dataTracker().closeRecord();
					space.dataTracker().closeTimeRecord();
				}
		}
		if (ecosystem.community()!=null)
			setPermanentRelations(ecosystem.community().allItems(),0L,0L);
		// new community
		// reset data tracker sample lists, ie replace initial items by runtime items
		for (DataTracker<?, Metadata> tracker:trackers.keySet())
			tracker.preProcess();
		log.info(()->"END Simulator " + id + " reset/pre");
	}

	@Override
	public synchronized void postProcess() {
		status = Final;
		log.info(()->"START Simulator " + id + " reset/post");
		lastTime = startTime;
		stoppingCondition.postProcess();
		for (Timer t : timerList)
			t.postProcess();
		// remove all items from containers
		ecosystem.postProcess();
		// remove all items from spaces and stop observing ecosystem
		if (mainSpace!=null)
			for (ObserverDynamicSpace space : mainSpace.spaces()) {
				space.postProcess();
				ecosystem.removeObserver(space);
		}
		log.info(()->"END Simulator " + id + " reset/post");
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
