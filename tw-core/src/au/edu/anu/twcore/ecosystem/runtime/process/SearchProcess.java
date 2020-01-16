package au.edu.anu.twcore.ecosystem.runtime.process;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.RelateToDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;

/**
 * Processes for searching the SystemComponent lists to establish relations between them.
 * The difference with RelationProcess is that here we do not have relations yet, so we look
 * for pairs of SystemComponents to establish a relation between them
 * 
 * NB: this Process should not have datatrackers !
 * 
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public class SearchProcess 
		extends AbstractRelationProcess  {

	private List<RelateToDecisionFunction> RTfunctions = new LinkedList<>();
	private HierarchicalContext focalContext = new HierarchicalContext();
	private HierarchicalContext otherContext = new HierarchicalContext();
	private LifeCycle focalLifeCycle = null;
	private SystemContainer focalLifeCycleContainer = null;
	private SystemFactory focalGroup = null;
	private SystemContainer focalGroupContainer = null;
	private LifeCycle otherLifeCycle = null;
	private SystemContainer otherLifeCycleContainer = null;
	private SystemFactory otherGroup = null;
	private SystemContainer otherGroupContainer = null;
	
	public SearchProcess(SystemContainer world, RelationContainer relation, Timer timer) {
		super(world, relation, timer);
	}

	@Override
	public void addFunction(TwFunction function) {
		if (!isSealed()) {
			if (function instanceof RelateToDecisionFunction)
				RTfunctions.add((RelateToDecisionFunction) function);
		}
	}
	
	@Override
	protected void loop(CategorizedContainer<SystemComponent> container, double t, double dt) {
		if (container.categoryInfo() instanceof Ecosystem) {
			setContext(focalContext,container);
			setContext(otherContext,container);
		}
		for (CategorizedContainer<SystemComponent> subc:container.subContainers()) {
			if (subc.categoryInfo() instanceof LifeCycle) {
				loop(subc,t,dt);
			}
			if (subc.categoryInfo().belongsTo(focalCategories)) {
				if (container.categoryInfo() instanceof LifeCycle) {
					setContext(focalContext,container);
					focalLifeCycle = (LifeCycle) container.categoryInfo();
					focalLifeCycleContainer = (SystemContainer) container;
				}
				setContext(focalContext,subc);
				focalGroup = (SystemFactory) subc.categoryInfo();
				focalGroupContainer = (SystemContainer) subc;
			}
			if (subc.categoryInfo().belongsTo(otherCategories)) {
				if (container.categoryInfo() instanceof LifeCycle) {
					setContext(otherContext,container);
					otherLifeCycle = (LifeCycle) container.categoryInfo();
					otherLifeCycleContainer = (SystemContainer) container;
				}
				setContext(otherContext,subc);
				otherGroup = (SystemFactory) subc.categoryInfo();
				otherGroupContainer = (SystemContainer) subc;
			}
		}
		if ((focalGroup!=null)&&(otherGroup!=null)) {
			executeFunctions(focalGroupContainer,otherGroupContainer,t,dt);
//			for (DataTracker0D tracker:tsTrackers) {
//				if (tracker.isTracked(focalGroupContainer)) {
//					tracker.recordItem(focalContext.buildItemId(null));
//					tracker.record(currentStatus,container.populationData());
//				}
//				if (focalGroup!=otherGroup) // no need to track the same group twice
//					if (tracker.isTracked(otherGroupContainer)) {
//					tracker.recordItem(otherContext.buildItemId(null));
//					tracker.record(currentStatus,container.populationData());
//				}
//			}
		}
	}

	private void executeFunctions(CategorizedContainer<SystemComponent> focalContainer,
			CategorizedContainer<SystemComponent> otherContainer,
			double t, double dt) {
		// brute force approach
		for (SystemComponent focal:focalContainer.items()) {
//			// track component state
//			for (DataTracker0D tracker:tsTrackers) 
//				if (tracker.isTracked(focal)) {
//				tracker.recordItem(focalContext.buildItemId(focal.id()));
//				tracker.record(currentStatus,focal.currentState());
//			}
			for (SystemComponent other:otherContainer.items()) {
				for (RelateToDecisionFunction function: RTfunctions) {
					function.setFocalContext(focalContext);
					function.setOtherContext(otherContext);
					if (function.relate(t,dt,focal,other)) {
						// tag items for future relation
//						System.out.println("Relating "+focal.id()+ " to "+other.id());
						relContainer.addItem(focal,other);
					}
				}
			}
		}
		// optimised approach using quadtrees
		// TODO: how?
	}

}
