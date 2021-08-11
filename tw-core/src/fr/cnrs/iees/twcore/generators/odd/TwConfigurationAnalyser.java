package fr.cnrs.iees.twcore.generators.odd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import au.edu.anu.twcore.ecosystem.dynamics.DataTrackerNode;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
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
		@Override
		public String toString() {
			String s = "";
			if (node!=null)
				s += node.classId() + ":" +node.id();
			if (level!=null)
				s+= ":"+level;
			if (looping!=null)
				s+= ":"+looping;
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
	 * @param config a 3Worlds configuration graph
	 */
	@SuppressWarnings("unchecked")
	public static List<ExecutionStep> getExecutionFlow(TreeGraph<TreeGraphDataNode, ALEdge> config) {
		TwConfigurationAnalyser analyser = new TwConfigurationAnalyser();
		TreeGraphDataNode root = config.root();
		if (root!=null) {
			// get all component types who have an initFunction
				// TODO
			// get all timers for the loop on timers
			Collection<TreeGraphDataNode> timers = (Collection<TreeGraphDataNode>) get(root,
				childTree(),
				selectZeroOrMany(hasTheLabel(N_TIMER.label())));
			for (TreeGraphDataNode timer:timers)
				analyser.addNewStep(timer,
					ExecutionLevel.timer,
					LoopingMode.parallel,
					timers.size()==1);
			// get all processes
			SimulatorNode sim = (SimulatorNode) get(root,
				childTree(),
				selectZeroOrOne(hasTheLabel(N_DYNAMICS.label())));
			if (sim!=null) {
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
						processCallingOrder.get(allTimers).size()==1);
					// loop on all processes within a dependency rank
					for (ProcessNode proc:lproc) {
						analyser.addNewStep(proc, 
							ExecutionLevel.process, 
							LoopingMode.parallel, 
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
								ldt.size()==1);
					}
				}
			}
		}
		return analyser.executionFlow;
	}
	
	// helper for the above
	private void addNewStep(TreeGraphDataNode node, 
			ExecutionLevel level, 
			LoopingMode mode, 
			boolean unique) {
		ExecutionStep step = new ExecutionStep();
		step.node = node;
		step.level = level;
		if (unique)
			step.looping = LoopingMode.unique;
		else
			step.looping = mode;
		executionFlow.add(step);
	}
	
	@SuppressWarnings("unchecked")
	private void addFunctionSteps(Collection<FunctionNode> lfunc) {
		if (lfunc!=null) {
			if (!lfunc.isEmpty()) {
				// function type step - all are unique within a process
				addNewStep(null, 
					ExecutionLevel.functionType, 
					LoopingMode.unique, 
					false);
				// functions within a function type
				for (FunctionNode func:lfunc) {
					addNewStep(func, 
						ExecutionLevel.function, 
						LoopingMode.parallel,
						lfunc.size()==1);
					// function consequences
					Collection<FunctionNode> lcsq = (Collection<FunctionNode>) get(func,
						children(),
						selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
					for (FunctionNode csq:lcsq) {
						addNewStep(csq, 
							ExecutionLevel.consequence, 
							LoopingMode.parallel, 
							lcsq.size()==1);
					}
				}
			}
		}
	}

}
