package au.edu.anu.twcore.ecosystem.runtime.process;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.system.SystemComponent;

/**
 * A TwProcess that loops on a list of SystemComponents and executes methods on
 * them
 * 
 * @author gignoux - 10 mars 2017
 *
 */
public class ComponentProcess extends AbstractProcess implements Categorized {

	private SortedSet<Category> focalCategories = new TreeSet<>();
	private String categoryId = null;

	private List<ChangeCategoryDecisionFunction> CCfunctions = new LinkedList<ChangeCategoryDecisionFunction>();
	private List<ChangeStateFunction> CSfunctions = new LinkedList<ChangeStateFunction>();
	private List<DeleteDecisionFunction> Dfunctions = new LinkedList<DeleteDecisionFunction>();
	private List<CreateOtherDecisionFunction> COfunctions = new LinkedList<CreateOtherDecisionFunction>();
//	private List<AggregatorFunction> Afunctions = new LinkedList<AggregatorFunction>();

	public ComponentProcess(Ecosystem world, Collection<Category> categories) {
		super(world);
		focalCategories.addAll(categories);
		categoryId = buildCategorySignature();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void execute(double t, double dt) {
//		// get current systems to work with
//		Iterable<SystemComponent> focals = (Iterable<SystemComponent>) world().getSystemsByCategory(focalCategories);
//		// preparing data sampling
//		for (AggregatorFunction function : Afunctions)
//			function.prepareForSampling(focals, t);
//		// apply all functions attached to this Process
//		for (SystemComponent focal : focals) {
//			// A model can have no state
//			boolean haveStates = focal.currentState() != null;
//			if (haveStates) {
//				focal.currentState().writeDisable();
//				focal.nextState().writeEnable();
//			}
//			// change state of this SystemComponent - easy
//			for (ChangeStateFunction function : CSfunctions) {
//				function.changeState(t, dt, focal);
//			}
//			if (haveStates)
//				focal.nextState().writeDisable();
//			// change category
//			for (ChangeCategoryDecisionFunction function : CCfunctions) {
//				String result = function.changeCategory(t, dt, focal);
//				if (result != null) {
//					SystemComponent newRecruit = focal.stage().species().stage(result).newSystem();
//					for (ChangeOtherStateFunction func : function.getConsequences())
//						func.changeOtherState(t, dt, focal, newRecruit);
//					focal.stage().tagSystemForDeletion(focal);
//					focal.stage().species().stage(result).tagSystemForInsertion(newRecruit);
//					// NB id should be preserved !
//					// NB: what about relations ?
//				}
//			}
//			// delete itself
//			for (DeleteDecisionFunction function : Dfunctions)
//				if (function.delete(t, dt, focal))
//					// missing: to which object should data return to ? this must depend on a
//					// relation !
//					focal.stage().tagSystemForDeletion(focal);
//			// creation of other SystemComponents
//			for (CreateOtherDecisionFunction function : COfunctions) {
//				for (String stage : focal.stage().produceStages()) {
//					double result = function.nNew(t, dt, focal, stage);
//					double proba = Math.random(); // or self made RNG
//					long n = (long) Math.floor(result);
//					if (proba >= (result - n))
//						n += 1;
//					for (int i = 0; i < n; i++) {
//						SystemComponent newBorn = focal.stage().species().stage(stage).newSystem();
//						for (ChangeStateFunction func : function.getChangeStateConsequences())
//							func.changeState(t, dt, newBorn);
//						for (ChangeOtherStateFunction func : function.getChangeOtherStateConsequences())
//							func.changeOtherState(t, dt, focal, newBorn);
//						for (RelateToDecisionFunction func : function.getRelateToDecisionConsequences())
//							if (func.relate(t, dt, focal, newBorn))
//								// TODO: how to know the type of relation to establish ?
//								focal.newEdge(newBorn, "");
//						focal.stage().species().stage(stage).tagSystemForInsertion(newBorn);
//					}
//				}
//			}
//			// aggregate data for data tracking
//			for (AggregatorFunction function : Afunctions)
//				function.aggregate(focal, focal.stage().species().getName(), focal.stage().name());
//		}
//		// send aggregator function results
//		for (AggregatorFunction function : Afunctions)
//			function.sendData(t);
	}

	@Override
	public void addFunction(TwFunction function) {
		if (!isSealed()) {
			if (ChangeCategoryDecisionFunction.class.isAssignableFrom(function.getClass()))
				CCfunctions.add((ChangeCategoryDecisionFunction) function);
			else if (ChangeStateFunction.class.isAssignableFrom(function.getClass()))
				CSfunctions.add((ChangeStateFunction) function);
			else if (DeleteDecisionFunction.class.isAssignableFrom(function.getClass()))
				Dfunctions.add((DeleteDecisionFunction) function);
			else if (CreateOtherDecisionFunction.class.isAssignableFrom(function.getClass()))
				COfunctions.add((CreateOtherDecisionFunction) function);
//			else if (AggregatorFunction.class.isAssignableFrom(function.getClass()))
//				Afunctions.add((AggregatorFunction) function);
		}
	}

	@Override
	public Set<Category> categories() {
		return focalCategories;
	}

	@Override
	public String categoryId() {
		return categoryId;
	}

}
