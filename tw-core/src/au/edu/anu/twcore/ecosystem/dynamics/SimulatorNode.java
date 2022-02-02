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

import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeData;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.ecosystem.runtime.space.SpaceOrganiser;
import au.edu.anu.twcore.ecosystem.runtime.stop.MultipleOrStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.SimpleStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.system.EcosystemGraph;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.ecosystem.structure.Structure;
import au.edu.anu.twcore.experiment.runtime.DataIdentifier;
import au.edu.anu.twcore.root.World;
import au.edu.anu.twcore.ui.runtime.DataReceiver;

/**
 * Class matching the "ecosystem/dynamics" node label in the 3Worlds
 * configuration tree. Has no properties. This <em>is</em> the simulator.
 *
 * NB: possible flaw here - Simulator is a factory while processNode is a
 * singleton - does it mean the same process instance will be used in many
 * simulators ? if yes, that's wrong...
 *
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class SimulatorNode extends InitialisableNode implements LimitedEdition<Simulator>, Sealable {

	private boolean sealed = false;
	private Timeline timeLine = null;
	private Map<Integer, Simulator> simulators = new HashMap<>();
	private int[] timeModelMasks; // bit pattern for every timeModel
	private Map<Integer, List<List<ProcessNode>>> processCallingOrder;


	public SimulatorNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public SimulatorNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	// used by doc generator
	public Map<Integer, List<List<ProcessNode>>> getProcessCallingOrder() {
		initialise();
		return processCallingOrder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			timeLine = (Timeline) get(getChildren(), selectOne(hasTheLabel(N_TIMELINE.label())));
			List<TimerNode> timeModels = (List<TimerNode>) get(timeLine.getChildren(),
					selectOneOrMany(hasTheLabel(N_TIMER.label())));
			// processes
			hierarchiseProcesses(timeModels);
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_DYNAMICS.initRank();
	}

	@SuppressWarnings("unchecked")
	private Simulator makeSimulator(int index) {
		
		// *** TimeModel --> Timer
		List<TimerNode> timeModels = (List<TimerNode>) get(timeLine.getChildren(),
				selectOneOrMany(hasTheLabel(N_TIMER.label())));
		List<Timer> timers = new ArrayList<>();
		for (TimerNode tm : timeModels)
			timers.add(tm.getInstance(index));
		
		// *** StoppingConditionNode --> StoppingCondition
		List<StoppingConditionNode> scnodes = (List<StoppingConditionNode>) get(getChildren(),
				selectZeroOrMany(hasTheLabel(N_STOPPINGCONDITION.label())));
		StoppingCondition rootStop = null;
		// when there is no stopping condition, the default one is used (runs to
		// infinite time)
		if (scnodes.isEmpty())
			rootStop = SimpleStoppingCondition.defaultStoppingCondition();
		// when there are many stopping conditions, any of them can stop the simulation
		else if (scnodes.size() > 1) {
			List<StoppingCondition> lsc = new ArrayList<>();
			for (StoppingConditionNode scn : scnodes)
				lsc.add(scn.getInstance(index));
			rootStop = new MultipleOrStoppingCondition(lsc);
		}
		// when there is only one stopping condition, then it is used
		else
			rootStop = scnodes.get(0).getInstance(index);
		
		// *** ProcessNode --> Process
		Map<Integer, List<List<TwProcess>>> pco = new HashMap<>();
		for (Map.Entry<Integer, List<List<ProcessNode>>> e : processCallingOrder.entrySet()) {
			List<List<TwProcess>> nllp = new ArrayList<>();
			for (List<ProcessNode> lp : e.getValue()) {
				List<TwProcess> nlp = new ArrayList<>();
				for (ProcessNode pn : lp)
					nlp.add(pn.getInstance(index));
				nllp.add(nlp);
			}
			pco.put(e.getKey(), nllp);
		}
		
		// *** Initial community
		// arena is always present
		ArenaComponent arena = ((ArenaType) getParent()).getInstance(index).getInstance();
		// everything else is optional
		Structure str = (Structure) get(getParent(), 
			children(), 
			selectZeroOrOne(hasTheLabel(N_STRUCTURE.label())));
		if (str!=null) {
			// for each of these, the problem is to find the container
			// hence we start at the top, with lifecycles, then groups, etc.
			// pb: if no initial data has been loaded, there will be no instantation here
			// although lower levels might require one.
			List<LifeCycleType> lctl = (List<LifeCycleType>) get(str.subTree(),
				selectZeroOrMany(hasTheLabel(N_LIFECYCLETYPE.label()))); 
			for (LifeCycleType lct:lctl) {
				LifeCycleFactory lcf = lct.getInstance(index);
				for (DataIdentifier itemId:lct.initialItems().keySet()) {
					ComponentContainer parentContainer = (ComponentContainer)arena.content();
					lcf.setName(itemId.lifeCycleId());
					LifeCycleComponent lcc = lcf.newInstance(parentContainer);
					initComponentData(lcc,lct.initialItems().get(itemId));
				}
			}
			List<GroupType> gtl = (List<GroupType>) get(str.subTree(),
				selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
			for (GroupType gt:gtl) {
				GroupFactory gf = gt.getInstance(index);
				for (DataIdentifier itemId:gt.initialItems().keySet()) {
					ComponentContainer parentContainer = (ComponentContainer)arena.content();
					if ((itemId.lifeCycleId()!=null)&&(!itemId.lifeCycleId().isBlank())) {
						String lcId = itemId.lifeCycleId();						
						parentContainer = (ComponentContainer) arena.content().findContainer(lcId);
						if (parentContainer==null)
							// create the lifecycle container with no data (otherwise it was found before)
							
							;
					}
					gf.setName(itemId.groupId());
					GroupComponent gc = gf.newInstance(parentContainer);
					initComponentData(gc,gt.initialItems().get(itemId));
				}
			}
			List<ComponentType> ctl = (List<ComponentType>) get(str.subTree(),
				selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
			for (ComponentType ct:ctl) {
				ComponentFactory cf = ct.getInstance(index);
				for (DataIdentifier itemId:ct.initialItems().keySet()) {
					ComponentContainer parentContainer = (ComponentContainer)arena.content();
					ComponentContainer grandParentContainer = (ComponentContainer)arena.content();
					if ((itemId.lifeCycleId()!=null)&&(!itemId.lifeCycleId().isBlank())) {
						String lcId = itemId.lifeCycleId();						
						grandParentContainer = (ComponentContainer) arena.content().findContainer(lcId);
						if (parentContainer==null)
							;
					}
					if ((itemId.groupId()!=null)&&(!itemId.groupId().isBlank())) {
						String gId = itemId.groupId();						
						parentContainer = (ComponentContainer) arena.content().findContainer(gId);
						if (parentContainer==null) {
							if (ct.getParent() instanceof GroupType) {
								GroupType gt = (GroupType) ct.getParent();
								GroupFactory gf = gt.getInstance(index);
								gf.setName(gId);
								GroupComponent gc = gf.newInstance(grandParentContainer);
								parentContainer = (ComponentContainer)gc.content();
							}
							// else ? no groupType --> must be arena ? or an arbitrary group if >1 componentType ?
						}
					}
					SystemComponent sc = cf.newInstance(parentContainer);
					initComponentData(sc,ct.initialItems().get(itemId));
					parentContainer.addInitialItem(sc);
					sc.setContainer((ComponentContainer)parentContainer); // is this really needed?

				}
			}
		}
		
		// *** ecosystem graph
		EcosystemGraph ecosystem = null;
		SpaceOrganiser spo = null;// presume can be null for non-spatial models?
		Map<String,RelationContainer> relconts = new HashMap<>();
		// add predefined relation containers
		Collection<RelationType> predefrels = (Collection<RelationType>) get(World.getRoot(this),
			children(),
			selectOne(hasTheLabel(N_PREDEFINED.label())),
			children(),
			selectZeroOrMany(hasTheLabel(N_RELATIONTYPE.label())));
		for (RelationType reltype:predefrels)
			relconts.put(reltype.id(),reltype.getInstance(index));
		// add user defined relation containers
		if (str != null) {
			relconts.putAll(str.relationContainers.getInstance(index));
			ecosystem = new EcosystemGraph(arena,relconts);
			// *** spaceOrganiser
			spo = str.spaceOrganiser.getInstance(index);
		} else {
			ecosystem = new EcosystemGraph(arena);
		}
		
		// *** finally, instantiate simulator
		Simulator sim = new Simulator(index, rootStop, timeLine, timeModels, timers, timeModelMasks.clone(), pco, spo,
			ecosystem,scnodes.isEmpty());
		rootStop.attachSimulator(sim);
		return sim;
	}
	
	// helper for previous method
	private void initComponentData(DataHolder dh, ReadOnlyPropertyList ropl) {
		for (String pkey:dh.properties().getKeysAsSet())
			if (ropl.hasProperty(pkey))
				dh.properties().setProperty(pkey,ropl.getPropertyValue(pkey));
	}

	@Override
	public Simulator getInstance(int index) {
		if (!sealed)
			initialise();
		if (!simulators.containsKey(index))
			simulators.put(index, makeSimulator(index));
		return simulators.get(index);
	}

	public void addObserver(DataReceiver<TimeData, Metadata> observer) {
		for (Simulator sim : simulators.values())
			sim.addTimeTracker(observer);
		// simulators.clear(); IDD: what's going on here -no longer needed?
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

	/**
	 * recursive method to build up the list of all possible simultaneous timeModel
	 * combinations. NOTE that this must be called with a non-empty list, otherwise
	 * the recursion will never start. works fine (3 timeModels generate 7 sets as
	 * expected)
	 *
	 * @param combinationList - the list of timeModel combinations
	 */
	private void computeTMCombinations(Set<HashSet<TimerNode>> combinationList, List<TimerNode> timerList) {
		int initSize = combinationList.size();
		Set<HashSet<TimerNode>> addList = new HashSet<HashSet<TimerNode>>();
		for (Set<TimerNode> stm : combinationList) {
			for (TimerNode tm : timerList) {
				HashSet<TimerNode> set = new HashSet<TimerNode>();
				set.addAll(stm);
				set.add(tm);
				addList.add(set);
			}
		}
		combinationList.addAll(addList);
		if (combinationList.size() != initSize)
			computeTMCombinations(combinationList, timerList);
	}

	/**
	 * compute the dependency rank of a Process - recursive
	 *
	 * @return
	 */
	private int dependencyRank(int rank, ProcessNode p, Map<ProcessNode, List<ProcessNode>> deps) {
		int result = rank;
		for (ProcessNode dp : deps.get(p))
			result = Math.max(result, dependencyRank(rank + 1, dp, deps));
		return result;
	}

	/**
	 * computes the order of process calls for any combination of time models
	 * possibly occurring simultaneously. Results are stored in processCallingOrder,
	 * which contains a map of lists of (simultaneous) processes index by execution
	 * rank. Process lists are executed by order of execution rank. Within a list,
	 * order doesnt matter (and process execution could in theory be parallelized
	 * here).
	 *
	 * Code checked & tested with procesRankingTest.dsl.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public void hierarchiseProcesses(List<TimerNode> timerList) {
		// compute bit pattern to identify timeModels
		timeModelMasks = new int[timerList.size()];
		int i = 0;
		int mask = 0x40000000;
		for (TimerNode tm : timerList) {
			timeModelMasks[i] = mask >> i;
			i++;
		}
		// builds up all the possible combinations of simultaneously occurring
		// timeModels
		Set<HashSet<TimerNode>> allTMCombinations = new HashSet<HashSet<TimerNode>>();
		for (TimerNode tm : timerList) {
			HashSet<TimerNode> set = new HashSet<TimerNode>();
			set.add(tm);
			allTMCombinations.add(set);
		}
		computeTMCombinations(allTMCombinations, timerList);
		Map<HashSet<TimerNode>, Integer> allTMMasks = new Hashtable<HashSet<TimerNode>, Integer>();
		for (HashSet<TimerNode> stm : allTMCombinations) {
			mask = 0x00000000;
			i = 0;
			// this assumes timeModels are always iterated in the same order in
			// timeModels
			for (TimerNode tm : timerList) {
				if (stm.contains(tm))
					mask = mask | timeModelMasks[i];
				i++;
			}
			allTMMasks.put(stm, mask);
		}
		// for each time model combination, sort out the calling order of
		// dependent processes
		processCallingOrder = new Hashtable<Integer, List<List<ProcessNode>>>();
		for (HashSet<TimerNode> stm : allTMCombinations) {
			LinkedList<TimerNode> simultaneousTM = new LinkedList<TimerNode>();
			simultaneousTM.addAll(stm);
			// get all processes activated by this list of time models
			LinkedList<ProcessNode> simultaneousProcesses = new LinkedList<ProcessNode>();
			for (TimerNode tm : simultaneousTM) {
				List<ProcessNode> simP = (List<ProcessNode>) get(tm.getChildren(),
						selectOneOrMany(hasTheLabel(N_PROCESS.label())));
				simultaneousProcesses.addAll(simP);
			}
			// find their dependencies
			List<ProcessNode> spl = new ArrayList<ProcessNode>(simultaneousProcesses.size());
			Map<ProcessNode, List<ProcessNode>> dependencies = new Hashtable<ProcessNode, List<ProcessNode>>(
					simultaneousProcesses.size());
			for (ProcessNode p : simultaneousProcesses)
				spl.add(p);
			for (ProcessNode p : simultaneousProcesses) {
				List<ProcessNode> deps = (List<ProcessNode>) get(p.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_DEPENDSON.label())), edgeListEndNodes());
				List<ProcessNode> dep = new LinkedList<ProcessNode>();
				// only store those dependencies that are in the current list of
				// activated processes
				for (ProcessNode dp : deps)
					if (spl.contains(dp))
						dep.add(dp);
				dependencies.put(p, dep);
			}
			// compute dependency ranks for this set of timeModels
			Map<ProcessNode, Integer> ranks = new Hashtable<ProcessNode, Integer>();
			int maxRank = 0;
			for (ProcessNode p : simultaneousProcesses) {
				Integer rank = 0;
				rank = dependencyRank(rank, p, dependencies);
				maxRank = Math.max(maxRank, rank);
				ranks.put(p, rank);
			}
			// build an array of process lists, indexed by execution rank
			List<List<ProcessNode>> processesByRank = new ArrayList<List<ProcessNode>>(maxRank + 1);
			for (int ii = 0; ii < maxRank + 1; ii++)
				processesByRank.add(new LinkedList<ProcessNode>());
			for (ProcessNode p : simultaneousProcesses)
				processesByRank.get(ranks.get(p)).add(p);
			// add the list into the proper timeModel combination map
			processCallingOrder.put(allTMMasks.get(stm), processesByRank);
		}
	}

}
