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

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.ArrayList;
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
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.ecosystem.runtime.stop.MultipleOrStoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.stop.SimpleStoppingCondition;
import au.edu.anu.twcore.ui.runtime.DataReceiver;

/**
 * Class matching the "ecosystem/dynamics" node label in the 3Worlds configuration tree.
 * Has no properties. This <em>is</em> the simulator.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class SimulatorNode 
		extends InitialisableNode 
		implements Factory<Simulator>, Sealable {
	
	private boolean sealed = false;
	private StoppingCondition rootStop = null;
	private List<Timer> timers = new ArrayList<>();
	private TimeLine timeLine = null;
	private List<Simulator> instances = new LinkedList<>();
	private int[] timeModelMasks; // bit pattern for every timeModel
	private Map<Integer, List<List<ProcessNode>>> processCallingOrder;

	public SimulatorNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public SimulatorNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if(!sealed) {
			super.initialise();
			timeLine = (TimeLine) get(getChildren(),
				selectOne(hasTheLabel(N_TIMELINE.label())));
			List<TimeModel> timeModels = (List<TimeModel>)get(timeLine.getChildren(),
				selectOneOrMany(hasTheLabel(N_TIMEMODEL.label())));
			for (TimeModel tm:timeModels)
				timers.add(tm.getInstance());
			List<StoppingConditionNode> scnodes = (List<StoppingConditionNode>) get(getChildren(),
				selectZeroOrMany(hasTheLabel(N_STOPPINGCONDITION.label())));
			// when there is no stopping condition, the default one is used (runs to infinite time)
			if (scnodes.isEmpty())
				rootStop = SimpleStoppingCondition.defaultStoppingCondition();
			// when there are many stopping conditions, any of them can stop the simulation
			else if (scnodes.size()>1) {
				List<StoppingCondition> lsc = new ArrayList<>();
				for (StoppingConditionNode scn:scnodes)
					lsc.add(scn.getInstance());
				rootStop = new MultipleOrStoppingCondition(lsc);
			} 
			// when there is only one stopping condition, then it is used
			else
				rootStop = scnodes.get(0).getInstance();
			// processes
			hierarchiseProcesses(timeModels);
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_DYNAMICS.initRank();
	}

	@Override
	public Simulator newInstance() {
		if (!sealed)
			initialise();
		Simulator sim = new Simulator(rootStop,timeLine,timers,timeModelMasks,
			processCallingOrder,
			(Ecosystem) getParent());
		rootStop.attachSimulator(sim);
		instances.add(sim);
		return sim;
	}
	
//	public void addObserver(DataReceiver<LabelValuePairData,Metadata> observer) {
//		for (Simulator sim:instances)
//			sim.addObserver(observer);
//		instances.clear();
//	}
	public void addObserver(DataReceiver<TimeData,Metadata> observer) {
		for (Simulator sim:instances)
			sim.addObserver(observer);
		instances.clear();
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
	 * recursive method to build up the list of all possible simultaneous
	 * timeModel combinations. NOTE that this must be called with a non-empty
	 * list, otherwise the recursion will never start. works fine (3 timeModels
	 * generate 7 sets as expected)
	 * 
	 * @param combinationList
	 *            - the list of timeModel combinations
	 */
	private void computeTMCombinations(Set<HashSet<TimeModel>> 
		combinationList,List<TimeModel> timerList) {
		int initSize = combinationList.size();
		Set<HashSet<TimeModel>> addList = new HashSet<HashSet<TimeModel>>();
		for (Set<TimeModel> stm : combinationList) {
			for (TimeModel tm : timerList) {
				HashSet<TimeModel> set = new HashSet<TimeModel>();
				set.addAll(stm);
				set.add(tm);
				addList.add(set);
			}
		}
		combinationList.addAll(addList);
		if (combinationList.size() != initSize)
			computeTMCombinations(combinationList,timerList);
	}

	
	/**
	 * compute the dependency rank of a Process - recursive
	 * 
	 * @return
	 */
	private int dependencyRank(int rank, ProcessNode p,
			Map<ProcessNode, List<ProcessNode>> deps) {
		int result = rank;
		for (ProcessNode dp : deps.get(p))
			result = Math.max(result, dependencyRank(rank + 1, dp, deps));
		return result;
	}

	
	/**
	 * computes the order of process calls for any combination of time models
	 * possibly occurring simultaneously. Results are stored in
	 * processCallingOrder, which contains a map of lists of (simultaneous)
	 * processes index by execution rank. Process lists are executed by order of
	 * execution rank. Within a list, order doesnt matter (and process execution
	 * could in theory be parallelized here).
	 * 
	 * Code checked & tested with procesRankingTest.dsl.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void hierarchiseProcesses(List<TimeModel> timerList) {
		// compute bit pattern to identify timeModels
		timeModelMasks = new int[timerList.size()];
		int i = 0;
		int mask = 0x40000000;
		for (TimeModel tm : timerList) {
			timeModelMasks[i] = mask >> i;
			i++;
		}
		// builds up all the possible combinations of simultaneously occurring
		// timeModels
		Set<HashSet<TimeModel>> allTMCombinations = new HashSet<HashSet<TimeModel>>();
		for (TimeModel tm : timerList) {
			HashSet<TimeModel> set = new HashSet<TimeModel>();
			set.add(tm);
			allTMCombinations.add(set);
		}
		computeTMCombinations(allTMCombinations,timerList);
		Map<HashSet<TimeModel>, Integer> allTMMasks = new Hashtable<HashSet<TimeModel>, Integer>();
		for (HashSet<TimeModel> stm : allTMCombinations) {
			mask = 0x00000000;
			i = 0;
			// this assumes timeModels are always iterated in the same order in
			// timeModels
			for (TimeModel tm : timerList) {
				if (stm.contains(tm))
					mask = mask | timeModelMasks[i];
				i++;
			}
			allTMMasks.put(stm, mask);
		}
		// for each time model combination, sort out the calling order of
		// dependent processes
		processCallingOrder = new Hashtable<Integer, List<List<ProcessNode>>>();
		for (HashSet<TimeModel> stm : allTMCombinations) {
			LinkedList<TimeModel> simultaneousTM = new LinkedList<TimeModel>();
			simultaneousTM.addAll(stm);
			// get all processes activated by this list of time models
			LinkedList<ProcessNode> simultaneousProcesses = new LinkedList<ProcessNode>();
			for (TimeModel tm : simultaneousTM) {
				List<ProcessNode> simP = (List<ProcessNode>) get(tm.getChildren(),
					selectOneOrMany(hasTheLabel(N_PROCESS.label())));
				simultaneousProcesses.addAll(simP);
			}
			// find their dependencies
			List<ProcessNode> spl = new ArrayList<ProcessNode>(
				simultaneousProcesses.size());
			Map<ProcessNode, List<ProcessNode>> dependencies = new Hashtable<ProcessNode, List<ProcessNode>>(
				simultaneousProcesses.size());
			for (ProcessNode p : simultaneousProcesses)
				spl.add(p);
			for (ProcessNode p : simultaneousProcesses) {
				List<ProcessNode> deps = (List<ProcessNode>) get(p.edges(Direction.OUT), 
					selectZeroOrMany(hasTheLabel(E_DEPENDSON.label())),
					edgeListEndNodes());
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
			List<List<ProcessNode>> processesByRank = new ArrayList<List<ProcessNode>>(
				maxRank + 1);
			for (int ii = 0; ii < maxRank + 1; ii++)
				processesByRank.add(new LinkedList<ProcessNode>());
			for (ProcessNode p : simultaneousProcesses)
				processesByRank.get(ranks.get(p)).add(p);
			// add the list into the proper timeModel combination map
			processCallingOrder.put(allTMMasks.get(stm), processesByRank);
		}
	}

	
}
