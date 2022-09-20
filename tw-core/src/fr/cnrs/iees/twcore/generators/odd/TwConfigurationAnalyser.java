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
package fr.cnrs.iees.twcore.generators.odd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.dynamics.DataTrackerNode;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.InitFunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import au.edu.anu.twcore.ecosystem.dynamics.TimerNode;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.ALGraph;
import fr.cnrs.iees.graph.impl.ALGraphFactory;
import fr.cnrs.iees.graph.impl.ALNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * A class to analyse the structure of a 3worlds configuration graph and return meaningful information.
 * 
 * @author Jacques Gignoux - 11 août 2021
 *
 */
public class TwConfigurationAnalyser {
		
	private TwConfigurationAnalyser() {}
	
	/** What kind of class is being looped on? */
	public enum ExecutionLevel {
		init,				// Initialisation methods --> called only once at start of a run
		timer, 				// Timers --> parallel
		dependencyRank,		// Dependency ranks of processes --> sequential
		process,			// Processes within dep. rank --> parallel
		functionType,		// Function type within processes --> sequential (but should not matter)
		function,			// Functions within function types --> parallel (random, actually)
		consequence,		// Function consequences within functions --> parallel?
		dataTracker			// dataTrackers within functionType --> sequential
	}
	
	/** Is the looping conceptually sequential or parallel (ie does order matter or not) */
	public enum LoopingMode {
		sequential,			// sequential looping, ie order does matter
		parallel,			// parallel looping, ie order does not matter (often implemented as random order looping)
		unique				// no looping because there is only one object to loop on - usually a permanent component
	}
	
	public class ExecutionStep {
		public TreeGraphDataNode node = null;
		public ExecutionLevel level = null;
		public LoopingMode looping = null;
		public TreeGraphDataNode applyTo = null;
		@Override
		public String toString() {
			String s = "";
			if (node!=null)
				s += node.classId() + "." +node.id();
			if (level!=null)
				s+= ":"+level;
			if (looping!=null)
				s+= ":"+looping;
			if (applyTo!=null)
				s+= ":"+applyTo.classId() + "." +node.id();
			return s;
		}
	}
	
	/** the execution flow as a flat list of execution steps */
	private List<ExecutionStep> executionFlow = new ArrayList<>();
	
	/**
	 * <p>Extract the execution flow as a linear list from the configuration tree structure.
	 * General looping structure is as follows (NB I use 'loop' for sequential loops and
	 * 'for each' for conceptually parallel loops:</p>
	 * 
	 * <pre>
	 *  loop on runs
	 *  	execute initFunction on arena
	 * 		for each lifeCycle
	 * 			execute initFunction on lifeCycle
	 * 			for each group
	 * 				execute initFunction on group
	 * 				for each component
	 * 					execute initFunction on component
	 * 		loop while stopping condition is false
	 * 			for each timer and ask its next time step
	 * 				for the closest step record all process that need to be executed
	 * 			for the current step
	 * 				for all timers activated at that step
	 * 					loop on all dependency ranks
	 * 						for each process at the same rank
	 * 							for each component / relation
	 * 								loop on all function types as per process inbuilt order
	 * 									for each functions of the same type
	 * 										execute function on component/relation for current step
	 * 										for each consequences of the function
	 * 											execute consequence on component/relation for current step
	 * 								for each data tracker
	 * 									record data
	 * </pre>
	 * @param system A system node - the root of a configuration model.
	 */
	public static List<ExecutionStep> getSystemExecutionFlow(TreeGraphDataNode system) {
		TwConfigurationAnalyser analyser = new TwConfigurationAnalyser();
		InitFunctionNode iFunc = (InitFunctionNode) get(system.getChildren(),
				selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
		if (iFunc != null)
			analyser.addNewStep(iFunc, ExecutionLevel.init, LoopingMode.unique, system, false);
		TreeGraphDataNode struc = (TreeGraphDataNode) get(system, children(),
				selectZeroOrOne(hasTheLabel(N_STRUCTURE.label())));
		if (struc!=null) {
			// life cycle types
			@SuppressWarnings("unchecked")
			Collection<TreeGraphDataNode> llct = (Collection<TreeGraphDataNode>) get(struc,
				children(),selectZeroOrMany(hasTheLabel(N_LIFECYCLETYPE.label())));
			for (TreeGraphDataNode lct:llct) {
				iFunc = (InitFunctionNode) get(lct.getChildren(),
					selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
				if (iFunc!=null)
					analyser.addNewStep(iFunc,ExecutionLevel.init,LoopingMode.parallel,
						lct,llct.size()==1);
				// group types under life cycle types
				@SuppressWarnings("unchecked")
				Collection<TreeGraphDataNode> lgt = (Collection<TreeGraphDataNode>) get(lct,
					children(),selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
				for (TreeGraphDataNode gt:lgt) {
					iFunc = (InitFunctionNode) get(gt.getChildren(),
						selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
					if (iFunc!=null)
						analyser.addNewStep(iFunc,ExecutionLevel.init,LoopingMode.parallel,
							gt,lgt.size()==1);
					// component types under group types
					Collection<TreeGraphDataNode> lcpt = (Collection<TreeGraphDataNode>) get(gt,
						children(),selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
					for (TreeGraphDataNode cpt:lcpt) {
						iFunc = (InitFunctionNode) get(cpt.getChildren(),
							selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
						if (iFunc!=null)
							analyser.addNewStep(iFunc,ExecutionLevel.init,LoopingMode.parallel,
								cpt,lcpt.size()==1);
					}
				}
			}
			// group types under arena type
			@SuppressWarnings("unchecked")
			Collection<TreeGraphDataNode> lgt = (Collection<TreeGraphDataNode>) get(struc,
				children(),selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
			for (TreeGraphDataNode gt:lgt) {
				iFunc = (InitFunctionNode) get(gt.getChildren(),
					selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
				if (iFunc!=null)
					analyser.addNewStep(iFunc,ExecutionLevel.init,LoopingMode.parallel,
						gt,lgt.size()==1);
				// component types under group types
				@SuppressWarnings("unchecked")
				Collection<TreeGraphDataNode> lcpt = (Collection<TreeGraphDataNode>) get(gt,
					children(),selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
				for (TreeGraphDataNode cpt:lcpt) {
					iFunc = (InitFunctionNode) get(cpt.getChildren(),
						selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
					if (iFunc!=null)
						analyser.addNewStep(iFunc,ExecutionLevel.init,LoopingMode.parallel,
							cpt,lcpt.size()==1);
				}
			}
			// component types under arena
			@SuppressWarnings("unchecked")
			Collection<TreeGraphDataNode> lcpt = (Collection<TreeGraphDataNode>) get(struc,
				children(),selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
			for (TreeGraphDataNode cpt:lcpt) {
				iFunc = (InitFunctionNode) get(cpt.getChildren(),
					selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
				if (iFunc!=null)
					analyser.addNewStep(iFunc,ExecutionLevel.init,LoopingMode.parallel,
						cpt,lcpt.size()==1);
			}
		}
		
		// get all timers for the loop on timers
		List<TimerNode> timers = (List<TimerNode>) get(system,
			childTree(),
			selectZeroOrMany(hasTheLabel(N_TIMER.label())));
		for (TreeGraphDataNode timer:timers)
			analyser.addNewStep(timer,
				ExecutionLevel.timer,
				LoopingMode.parallel,
				null,
				timers.size()==1);
		// get all processes
		SimulatorNode sim = (SimulatorNode) get(system,
			childTree(),
			selectZeroOrOne(hasTheLabel(N_DYNAMICS.label())));
		if (sim!=null) {
			sim.hierarchiseProcesses(timers);
			Map<Integer, List<List<ProcessNode>>> processCallingOrder = sim.getProcessCallingOrder();
			// NB for simplicity, we only present process ordering for the combination
			// of all timers, which means that the Integer index is maximal (it's a mask
			// with 1s when a timer is in, starting left, so the max value in the map
			// corresponds to the combination with the largest number of timers).
			int allTimers = Collections.max(processCallingOrder.keySet());
			// loop on process dependency rank in the case of maximal complexity (all 
			// timers simultaneously activated)
			for (List<ProcessNode> lproc:processCallingOrder.get(allTimers)) {
				analyser.addNewStep(null, 
					ExecutionLevel.dependencyRank, 
					LoopingMode.sequential, 
					null,
					processCallingOrder.get(allTimers).size()==1);
				// loop on all processes within a dependency rank
				for (ProcessNode proc:lproc) {
					analyser.addNewStep(proc, 
						ExecutionLevel.process, 
						LoopingMode.parallel, 
						null,
						lproc.size()==1);
					// get all functions of a process, per function type in proper order
					// component process
					Collection<FunctionNode> lfunc = (Collection<FunctionNode>) get(proc,children(),
						selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
						hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.ChangeState)) ));
					analyser.addFunctionSteps(lfunc);
					lfunc = (Collection<FunctionNode>) get(proc,children(),
						selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
						hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.DeleteDecision)) ));
					analyser.addFunctionSteps(lfunc);
					lfunc = (Collection<FunctionNode>) get(proc,children(),
						selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
						hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.CreateOtherDecision)) ));
					analyser.addFunctionSteps(lfunc);
					lfunc = (Collection<FunctionNode>) get(proc,children(),
						selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
						hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.ChangeCategoryDecision)) ));
					analyser.addFunctionSteps(lfunc);
					// relation process
					lfunc = (Collection<FunctionNode>) get(proc,children(),
						selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
						hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.ChangeOtherState)) ));
					analyser.addFunctionSteps(lfunc);
					lfunc = (Collection<FunctionNode>) get(proc,children(),
						selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
						hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.MaintainRelationDecision)) ));
					analyser.addFunctionSteps(lfunc);
					lfunc = (Collection<FunctionNode>) get(proc,children(),
						selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
						hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.ChangeRelationState)) ));
					analyser.addFunctionSteps(lfunc);
					// search process
					lfunc = (Collection<FunctionNode>) get(proc,children(),
						selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
						hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.RelateToDecision)) ));
					analyser.addFunctionSteps(lfunc);
					// get all data trackers of a process
					Collection<DataTrackerNode> ldt = (Collection<DataTrackerNode>) get(proc,
						children(),
						selectZeroOrMany(hasTheLabel(N_DATATRACKER.label())));
					for (DataTrackerNode dt:ldt)
						analyser.addNewStep(dt, 
							ExecutionLevel.dataTracker, 
							LoopingMode.parallel, 
							null,
							ldt.size()==1);
				}
			}
		}
		return analyser.executionFlow;		
	}

//	@SuppressWarnings("unchecked")
//	public static List<ExecutionStep> getExecutionFlow(TreeGraphDataNode configRoot, TreeGraphDataNode system) {
//		TwConfigurationAnalyser analyser = new TwConfigurationAnalyser();
//		// can't be null here
//		if (configRoot!=null) {
//			
//			// get all component types who have an initFunction - but only for this system!
//			ArenaType arena = (ArenaType) get(configRoot,
//				childTree(),
//				selectOne(hasTheLabel(N_SYSTEM.label())));
//			// arena
//			InitFunctionNode ifunc = null;
//			ifunc = (InitFunctionNode) get(arena.getChildren(),
//				selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
//			if (ifunc!=null)
//				analyser.addNewStep(ifunc,ExecutionLevel.init,LoopingMode.unique,arena,false);
//			// other types with init functions - down the structure tree, if any
//			TreeGraphDataNode struc = (TreeGraphDataNode) get(arena,
//				children(),
//				selectZeroOrOne(hasTheLabel(N_STRUCTURE.label())));
//			if (struc!=null) {
//				// life cycle types
//				Collection<TreeGraphDataNode> llct = (Collection<TreeGraphDataNode>) get(struc,
//					children(),selectZeroOrMany(hasTheLabel(N_LIFECYCLETYPE.label())));
//				for (TreeGraphDataNode lct:llct) {
//					ifunc = (InitFunctionNode) get(lct.getChildren(),
//						selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
//					if (ifunc!=null)
//						analyser.addNewStep(ifunc,ExecutionLevel.init,LoopingMode.parallel,
//							lct,llct.size()==1);
//					// group types under life cycle types
//					Collection<TreeGraphDataNode> lgt = (Collection<TreeGraphDataNode>) get(lct,
//						children(),selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
//					for (TreeGraphDataNode gt:lgt) {
//						ifunc = (InitFunctionNode) get(gt.getChildren(),
//							selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
//						if (ifunc!=null)
//							analyser.addNewStep(ifunc,ExecutionLevel.init,LoopingMode.parallel,
//								gt,lgt.size()==1);
//						// component types under group types
//						Collection<TreeGraphDataNode> lcpt = (Collection<TreeGraphDataNode>) get(gt,
//							children(),selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
//						for (TreeGraphDataNode cpt:lcpt) {
//							ifunc = (InitFunctionNode) get(cpt.getChildren(),
//								selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
//							if (ifunc!=null)
//								analyser.addNewStep(ifunc,ExecutionLevel.init,LoopingMode.parallel,
//									cpt,lcpt.size()==1);
//						}
//					}
//				}
//				// group types under arena type
//				Collection<TreeGraphDataNode> lgt = (Collection<TreeGraphDataNode>) get(struc,
//					children(),selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
//				for (TreeGraphDataNode gt:lgt) {
//					ifunc = (InitFunctionNode) get(gt.getChildren(),
//						selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
//					if (ifunc!=null)
//						analyser.addNewStep(ifunc,ExecutionLevel.init,LoopingMode.parallel,
//							gt,lgt.size()==1);
//					// component types under group types
//					Collection<TreeGraphDataNode> lcpt = (Collection<TreeGraphDataNode>) get(gt,
//						children(),selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
//					for (TreeGraphDataNode cpt:lcpt) {
//						ifunc = (InitFunctionNode) get(cpt.getChildren(),
//							selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
//						if (ifunc!=null)
//							analyser.addNewStep(ifunc,ExecutionLevel.init,LoopingMode.parallel,
//								cpt,lcpt.size()==1);
//					}
//				}
//				// component types under arena
//				Collection<TreeGraphDataNode> lcpt = (Collection<TreeGraphDataNode>) get(struc,
//					children(),selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
//				for (TreeGraphDataNode cpt:lcpt) {
//					ifunc = (InitFunctionNode) get(cpt.getChildren(),
//						selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
//					if (ifunc!=null)
//						analyser.addNewStep(ifunc,ExecutionLevel.init,LoopingMode.parallel,
//							cpt,lcpt.size()==1);
//				}
//			}
//			
//			// get all timers for the loop on timers
//			List<TimerNode> timers = (List<TimerNode>) get(configRoot,
//				childTree(),
//				selectZeroOrMany(hasTheLabel(N_TIMER.label())));
//			for (TreeGraphDataNode timer:timers)
//				analyser.addNewStep(timer,
//					ExecutionLevel.timer,
//					LoopingMode.parallel,
//					null,
//					timers.size()==1);
//			// get all processes
//			SimulatorNode sim = (SimulatorNode) get(configRoot,
//				childTree(),
//				selectZeroOrOne(hasTheLabel(N_DYNAMICS.label())));
//			if (sim!=null) {
//				sim.hierarchiseProcesses(timers);
//				Map<Integer, List<List<ProcessNode>>> processCallingOrder = sim.getProcessCallingOrder();
//				// NB for simplicity, we only present process ordering for the combination
//				// of all timers, which means that the Integer index is maximal (it's a mask
//				// with 1s when a timer is in, starting left, so the max value in the map
//				// corresponds to the combination with the largest number of timers).
//				int allTimers = Collections.max(processCallingOrder.keySet());
//				// loop on process dependency rank in the case of maximal complexity (all 
//				// timers simultaneously activated)
//				for (List<ProcessNode> lproc:processCallingOrder.get(allTimers)) {
//					analyser.addNewStep(null, 
//						ExecutionLevel.dependencyRank, 
//						LoopingMode.sequential, 
//						null,
//						processCallingOrder.get(allTimers).size()==1);
//					// loop on all processes within a dependency rank
//					for (ProcessNode proc:lproc) {
//						analyser.addNewStep(proc, 
//							ExecutionLevel.process, 
//							LoopingMode.parallel, 
//							null,
//							lproc.size()==1);
//						// get all functions of a process, per function type in proper order
//						// component process
//						Collection<FunctionNode> lfunc = (Collection<FunctionNode>) get(proc,children(),
//							selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
//							hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.ChangeState)) ));
//						analyser.addFunctionSteps(lfunc);
//						lfunc = (Collection<FunctionNode>) get(proc,children(),
//							selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
//							hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.DeleteDecision)) ));
//						analyser.addFunctionSteps(lfunc);
//						lfunc = (Collection<FunctionNode>) get(proc,children(),
//							selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
//							hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.CreateOtherDecision)) ));
//						analyser.addFunctionSteps(lfunc);
//						lfunc = (Collection<FunctionNode>) get(proc,children(),
//							selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
//							hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.ChangeCategoryDecision)) ));
//						analyser.addFunctionSteps(lfunc);
//						// relation process
//						lfunc = (Collection<FunctionNode>) get(proc,children(),
//							selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
//							hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.ChangeOtherState)) ));
//						analyser.addFunctionSteps(lfunc);
//						lfunc = (Collection<FunctionNode>) get(proc,children(),
//							selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
//							hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.MaintainRelationDecision)) ));
//						analyser.addFunctionSteps(lfunc);
//						lfunc = (Collection<FunctionNode>) get(proc,children(),
//							selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
//							hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.ChangeRelationState)) ));
//						analyser.addFunctionSteps(lfunc);
//						// search process
//						lfunc = (Collection<FunctionNode>) get(proc,children(),
//							selectZeroOrMany( andQuery(hasTheLabel(N_FUNCTION.label()),
//							hasProperty(P_FUNCTIONTYPE.key(),TwFunctionTypes.RelateToDecision)) ));
//						analyser.addFunctionSteps(lfunc);
//						// get all data trackers of a process
//						Collection<DataTrackerNode> ldt = (Collection<DataTrackerNode>) get(proc,
//							children(),
//							selectZeroOrMany(hasTheLabel(N_DATATRACKER.label())));
//						for (DataTrackerNode dt:ldt)
//							analyser.addNewStep(dt, 
//								ExecutionLevel.dataTracker, 
//								LoopingMode.parallel, 
//								null,
//								ldt.size()==1);
//					}
//				}
//			}
//		}
//		
//		return analyser.executionFlow;
//	}
	
	// helper for getExecutionFlow(...)
	private void addNewStep(TreeGraphDataNode node, 
			ExecutionLevel level, 
			LoopingMode mode, 
			TreeGraphDataNode apply,
			boolean unique) {
		ExecutionStep step = new ExecutionStep();
		step.node = node;
		step.level = level;
		if (unique)
			step.looping = LoopingMode.unique;
		else
			step.looping = mode;
		step.applyTo = apply;
		executionFlow.add(step);
	}
	
	// helper for getExecutionFlow(...)
	@SuppressWarnings("unchecked")
	private void addFunctionSteps(Collection<FunctionNode> lfunc) {
		if (lfunc!=null) {
			if (!lfunc.isEmpty()) {
				// function type step - all are unique within a process
				addNewStep(null, 
					ExecutionLevel.functionType, 
					LoopingMode.unique, 
					null,
					false);
				// functions within a function type
				for (FunctionNode func:lfunc) {
					addNewStep(func, 
						ExecutionLevel.function, 
						LoopingMode.parallel,
						null,
						lfunc.size()==1);
					// function consequences
					Collection<FunctionNode> lcsq = (Collection<FunctionNode>) get(func,
						children(),
						selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
					for (FunctionNode csq:lcsq) {
						addNewStep(csq, 
							ExecutionLevel.consequence, 
							LoopingMode.parallel,
							null,
							lcsq.size()==1);
					}
				}
			}
		}
	}

	/**
	 * Builds a life cycle graph from a LifeCycleType root node in a 3w configuration
	 * 
	 * @param lifeCycleType the life cycle node to use as a start
	 * @return a little graph showing the life cycle.
	 */
	@SuppressWarnings("unchecked")
	public static Graph<? extends Node,? extends Edge> getLifeCycleGraph(TreeGraphNode lifeCycleType) {
		GraphFactory factory = new ALGraphFactory(lifeCycleType.id());
		ALGraph<ALNode,ALEdge> lcgraph = new ALGraph<>(factory);
		Collection<TreeGraphNode> froms, tos;
		String startNode=null, endNode=null;
		// get the categories involved in this life cycle
		TreeGraphNode cset = (TreeGraphNode) get(lifeCycleType,
			outEdges(),
			selectZeroOrOne(hasTheLabel(E_APPLIESTO.label())),
			endNode());
		Collection<TreeGraphNode> cats = (Collection<TreeGraphNode>) cset.getChildren();
		// create nodes from the categories
		for (TreeGraphNode cat:cats)
			factory.makeNode(cat.id());
		// get all recruit edges and create matching edges in output graph
		Collection<TreeGraphNode> recruits = (Collection<TreeGraphNode>) get(lifeCycleType,
			children(),
			selectZeroOrMany(hasTheLabel(N_RECRUIT.label())));
		for (TreeGraphNode rec:recruits) {
			froms = (Collection<TreeGraphNode>) get(rec,
				outEdges(),
				selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())),
				edgeListEndNodes());
			for (TreeGraphNode cat:froms)
				if (cats.contains(cat))
					startNode = cat.id();
			tos = (Collection<TreeGraphNode>) get(rec,
				outEdges(),
				selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())),
				edgeListEndNodes());
			for (TreeGraphNode cat:tos)
				if (cats.contains(cat))
					endNode = cat.id();
			TreeGraphNode func = (TreeGraphNode) get(rec,
				outEdges(),
				selectZeroOrOne(hasTheLabel(E_EFFECTEDBY.label())),
				endNode());
			if ((func!=null)&&(startNode!=null)&&(endNode!=null))
				factory.makeEdge(lcgraph.findNode(startNode), 
					lcgraph.findNode(endNode), 
					func.id());
		}
		// get all produce edges and create matching edges in output graph
		Collection<TreeGraphNode> products = (Collection<TreeGraphNode>) get(lifeCycleType,
			children(),
			selectZeroOrMany(hasTheLabel(N_PRODUCE.label())));
		for (TreeGraphNode prod:products) {
			froms = (Collection<TreeGraphNode>) get(prod,
				outEdges(),
				selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())),
				edgeListEndNodes());
			for (TreeGraphNode cat:froms)
				if (cats.contains(cat))
					startNode = cat.id();
			tos = (Collection<TreeGraphNode>) get(prod,
				outEdges(),
				selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())),
				edgeListEndNodes());
			for (TreeGraphNode cat:tos)
				if (cats.contains(cat))
					endNode = cat.id();
			TreeGraphNode func = (TreeGraphNode) get(prod,
				outEdges(),
				selectZeroOrOne(hasTheLabel(E_EFFECTEDBY.label())),
				endNode());
			if ((func!=null)&&(startNode!=null)&&(endNode!=null))
				factory.makeEdge(lcgraph.findNode(startNode), 
					lcgraph.findNode(endNode), 
					func.id());
		}
		return lcgraph;
	}
	
}
