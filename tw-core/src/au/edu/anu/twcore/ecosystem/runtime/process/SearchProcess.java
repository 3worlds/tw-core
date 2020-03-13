package au.edu.anu.twcore.ecosystem.runtime.process;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.RelateToDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.containers.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
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
@SuppressWarnings("unused")
public class SearchProcess 
		extends AbstractRelationProcess  {

	private List<RelateToDecisionFunction> RTfunctions = new LinkedList<>();
	private HierarchicalContext focalContext = new HierarchicalContext();
	private HierarchicalContext otherContext = new HierarchicalContext();
	private LifeCycle focalLifeCycle = null;
	private ComponentContainer focalLifeCycleContainer = null;
	private SystemFactory focalGroup = null;
	private ComponentContainer focalGroupContainer = null;
	private LifeCycle otherLifeCycle = null;
	private ComponentContainer otherLifeCycleContainer = null;
	private SystemFactory otherGroup = null;
	private ComponentContainer otherGroupContainer = null;
	
	public SearchProcess(ComponentContainer world, RelationContainer relation, 
			Timer timer, DynamicSpace<SystemComponent,LocatedSystemComponent> space,double searchR) {
		super(world, relation, timer, space, searchR);
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
					focalLifeCycleContainer = (ComponentContainer) container;
				}
				setContext(focalContext,subc);
				focalGroup = (SystemFactory) subc.categoryInfo();
				focalGroupContainer = (ComponentContainer) subc;
			}
			if (subc.categoryInfo().belongsTo(otherCategories)) {
				if (container.categoryInfo() instanceof LifeCycle) {
					setContext(otherContext,container);
					otherLifeCycle = (LifeCycle) container.categoryInfo();
					otherLifeCycleContainer = (ComponentContainer) container;
				}
				setContext(otherContext,subc);
				otherGroup = (SystemFactory) subc.categoryInfo();
				otherGroupContainer = (ComponentContainer) subc;
			}
		}
		if ((focalGroup!=null)&&(otherGroup!=null)) {
			executeFunctions(focalGroupContainer,otherGroupContainer,t,dt);
		}
	}

	private void doRelate(double t, double dt, SystemComponent focal, SystemComponent other) {
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
	
	private void executeFunctions(CategorizedContainer<SystemComponent> focalContainer,
			CategorizedContainer<SystemComponent> otherContainer,
			double t, double dt) {
		// NB: problem here:
//		Exception in thread "Thread-4" java.util.ConcurrentModificationException
//		at java.base/java.util.HashMap$HashIterator.nextNode(HashMap.java:1493)
//		at java.base/java.util.HashMap$ValueIterator.next(HashMap.java:1521)
//		at au.edu.anu.twcore.ecosystem.runtime.process.SearchProcess.executeFunctions(SearchProcess.java:108)
//		at au.edu.anu.twcore.ecosystem.runtime.process.SearchProcess.loop(SearchProcess.java:89)
//		at au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess.execute(AbstractProcess.java:132)
//		at au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator.step(Simulator.java:238)
		for (SystemComponent focal:focalContainer.items()) {
			// brute force approach - SLOW O(n²) - maybe a warning should be issued in MM
			if (space==null) {
				for (SystemComponent other:otherContainer.items())
					if (other!=focal)
						doRelate(t,dt,focal,other);
			}
			// optimised approach using space indexers
			else {
				// search radius positive, means we only search until this distance
				if (searchRadius>space.precision()) {
					Iterable<SystemComponent> lsc = space.getItemsWithin(focal,searchRadius);
					if (lsc!=null)
						for (SystemComponent other:lsc) {
							if (other!=focal)
								if (other.membership().belongsTo(otherCategories))
									if (!otherContainer.containsInitialItem(other))
										doRelate(t,dt,focal,other);	
						}
				}
				// search radius null, means we search for the nearest neighbours only 
				else {
					Iterable<SystemComponent> lsc = space.getNearestItems(focal);
					if (lsc!=null)
						for (SystemComponent other:lsc) {
							if (other!=focal)
								if (other.membership().belongsTo(otherCategories))
									if (!otherContainer.containsInitialItem(other))
										doRelate(t,dt,focal,other);
						}
				}
			}
		}
	}

}
