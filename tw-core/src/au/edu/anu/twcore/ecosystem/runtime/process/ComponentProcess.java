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

import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.tracking.TimeSeriesTracker;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.ens.biologie.generic.utils.Logging;

/**
 * A TwProcess that loops on a list of SystemComponents and executes methods on
 * them
 * 
 * @author gignoux - 10 mars 2017
 *
 */
public class ComponentProcess extends AbstractProcess implements Categorized<SystemComponent> {
	
	private class newBornSettings {
		SystemFactory factory = null;
		SystemContainer container = null;
		String name = null;
	}

	private static Logger log = Logging.getLogger(ComponentProcess.class);
	
	private SortedSet<Category> focalCategories = new TreeSet<>();
	private String categoryId = null;

	private List<ChangeCategoryDecisionFunction> CCfunctions = new LinkedList<ChangeCategoryDecisionFunction>();
	private List<ChangeStateFunction> CSfunctions = new LinkedList<ChangeStateFunction>();
	private List<DeleteDecisionFunction> Dfunctions = new LinkedList<DeleteDecisionFunction>();
	private List<CreateOtherDecisionFunction> COfunctions = new LinkedList<CreateOtherDecisionFunction>();
//	private List<AggregatorFunction> Afunctions = new LinkedList<AggregatorFunction>();
	
	// local variables for looping
	private HierarchicalContext focalContext = new HierarchicalContext();
	private LifeCycle lifeCycle = null;
//	private Ecosystem ecosystem = null;
	private SystemFactory group = null;
	
	private SystemContainer lifeCycleContainer = null;
//	private SystemContainer ecosystemContainer = null;
//	private SystemContainer groupContainer = null;
	
	public ComponentProcess(SystemContainer world, Collection<Category> categories) {
		super(world);
		focalCategories.addAll(categories);
		categoryId = buildCategorySignature();
	}
	
	// recursive loop on all sub containers of the community
	private void loop(CategorizedContainer<SystemComponent> container,
		double t, double dt) {
		if (container.categoryInfo().belongsTo(focalCategories)) {
			if (container.categoryInfo() instanceof LifeCycle) {
				focalContext.lifeCycleParameters = container.parameters();
				focalContext.lifeCycleVariables = container.variables();
				focalContext.lifeCyclePopulationData = container.populationData();
				focalContext.lifeCycleName = container.id();
				lifeCycle = (LifeCycle) container.categoryInfo();
				lifeCycleContainer = (SystemContainer) container;
			}
			else if (container.categoryInfo() instanceof Ecosystem) {
				focalContext.ecosystemParameters = container.parameters();
				focalContext.ecosystemVariables = container.variables();
				focalContext.ecosystemPopulationData = container.populationData();
				focalContext.ecosystemName = container.id();
//				ecosystem = (Ecosystem) container.categoryInfo();
//				ecosystemContainer = (SystemContainer) container;
			}
			else if (container.categoryInfo() instanceof SystemFactory) {
				focalContext.groupParameters = container.parameters();
				focalContext.groupVariables = container.variables();
				focalContext.groupPopulationData = container.populationData();
				focalContext.groupName = container.id();
				group = (SystemFactory) container.categoryInfo();
//				groupContainer = (SystemContainer) container;
				
			}
			executeFunctions(container,t,dt);
			// track group state
			for (TimeSeriesTracker tracker:tsTrackers) {
				tracker.record(container.populationData());
			}
			focalContext.clear();
		}
		for (CategorizedContainer<SystemComponent> subc:container.subContainers())
			loop(subc,t,dt);
	}
	
	// single loop on a container which matches the process categories
	private void executeFunctions(CategorizedContainer<SystemComponent> container,
		double t, double dt) {
		for (SystemComponent focal:container.items()) {
			if (focal.currentState() != null) { // otherwise no point computing changes!
				focal.currentState().writeDisable();
				focal.nextState().writeEnable();
				// change state of this SystemComponent - easy
				for (ChangeStateFunction function : CSfunctions) {
					function.setFocalContext(focalContext);
					function.changeState(t, dt, focal);
				}
				focal.nextState().writeDisable();
			}
			// recruit to other component type ("change category")
			for (ChangeCategoryDecisionFunction function : CCfunctions) {
				function.setFocalContext(focalContext);
				String newCat = function.changeCategory(t, dt, focal);
				if (newCat != null) {
					if (lifeCycle!=null) {
						// find the next stage & instantiate new component
						SystemContainer recruitContainer = null;
						for (CategorizedContainer<SystemComponent> subContainer:
							lifeCycleContainer.subContainers()) 
							if (subContainer.categoryInfo().categoryId().contains(newCat))
								recruitContainer = (SystemContainer) subContainer;
						if (recruitContainer==null) {
							StringBuilder sb = new StringBuilder();
							sb.append("'")
								.append(focalContext.groupName)
								.append("' cannot recruit to '")
								.append(newCat)
								.append("'");
							log.severe(sb.toString());
						}
						else {
							SystemComponent newRecruit = recruitContainer.newInstance();
							newRecruit.autoVar().writeEnable();
							// carry over former ID as name
							if (focal.autoVar().name().isBlank())
								newRecruit.autoVar().name(focal.id());
							else
								newRecruit.autoVar().name(focal.autoVar().name());
							// carry over age and birthDate
							newRecruit.autoVar().age(focal.autoVar().age());
							newRecruit.autoVar().birthDate(focal.autoVar().birthDate());
							newRecruit.autoVar().writeDisable();
							// user-defined carry-overs
							for (ChangeOtherStateFunction func : function.getConsequences()) {
								function.setFocalContext(focalContext);
								func.changeOtherState(t, dt, focal, newRecruit);
							}
							// replacement of old component by new one.
							container.removeItem(focal.id());
							recruitContainer.addItem(newRecruit);
							// CAUTION: this makes sure the new object takes the place of
							// the former one in any graph it is part of.
							// THIS WILL NOT WORK if there are edges to SystemFactory etc.
							// It is of tremendous importance that edges are only to
							// other SystemComponents.
							newRecruit.replace(focal);
						}
					}
				}
			}
			// delete itself
			for (DeleteDecisionFunction function : Dfunctions) {
				function.setFocalContext(focalContext);
				if (function.delete(t, dt, focal)) {
					container.removeItem(focal.id()); // safe - delayed removal
					// if present, spreads some values to other components 
					// (e.g. "decomposition", or "erosion")
					if (!function.getConsequences().isEmpty())
						// TODO: the "returnsTo" relation type must be predefined somewhere
						for (SystemRelation to:focal.getRelations("returnsTo")) {
							SystemComponent other = (SystemComponent) to.endNode();
							for (ChangeOtherStateFunction consequence:function.getConsequences()) {
								function.setFocalContext(focalContext);
								consequence.changeOtherState(t, dt, focal, other);
							}
					}
				}
			}
			// creation of other SystemComponents
			for (CreateOtherDecisionFunction function : COfunctions) {
				// if there is a life cycle, then it will return the next stage(s)
				List<newBornSettings> newBornSpecs = new ArrayList<>(); 
				if (lifeCycle!=null ) {
					// search for category signatures of produce targets
					for (String catSignature:lifeCycle.produceTo(group))
						for (CategorizedContainer<SystemComponent> subc:
							lifeCycleContainer.subContainers()) 
						// since lifeCycle stages only have one category this test should do
						if (subc.categoryInfo().categoryId().contains(catSignature)) {
							newBornSettings nbs = new newBornSettings();
							nbs.name = subc.categoryInfo().categoryId();
							nbs.factory = (SystemFactory) subc.categoryInfo();
							nbs.container = (SystemContainer) subc;
							newBornSpecs.add(nbs);
					}
				} 
				// without a life cycle, only objects of the same type can be created
				else {
					newBornSettings nbs = new newBornSettings();
					nbs.factory = group;
					nbs.name = group.categoryId();
					nbs.container = (SystemContainer) container;
					newBornSpecs.add(nbs);
				}
				function.setFocalContext(focalContext);
				for (newBornSettings nbs:newBornSpecs) {
					double result = function.nNew(t, dt, focal, nbs.name);
					// compute effective number of newBorns (taking the decimal part as a probability)
					double proba = function.rng().nextDouble();
					long n = (long) Math.floor(result);
					if (proba < (result - n))
						n += 1;
					for (int i = 0; i < n; i++) {
						SystemComponent newBorn = nbs.factory.newInstance();
						for (ChangeStateFunction func : function.getChangeStateConsequences()) {
							function.setFocalContext(focalContext);
							func.changeState(t, dt, newBorn);
						}
						for (ChangeOtherStateFunction func : function.getChangeOtherStateConsequences()) {
							function.setFocalContext(focalContext);
							func.changeOtherState(t, dt, focal, newBorn);
						}
						for (RelateToDecisionFunction func : function.getRelateToDecisionConsequences()) {
							function.setFocalContext(focalContext);
							if (func.relate(t, dt, focal, newBorn)) {
								// TODO: how to know the type of relation to establish ?
								focal.relateTo(newBorn,"parentTo");
							}
						}
						// welcome newBorn in container!
						nbs.container.addItem(newBorn); // safe - delayed addition
					}
				}
			}
			// track component state
			for (TimeSeriesTracker tracker:tsTrackers) {
				tracker.record(focal.currentState());
			}
		}
	}

	@Override
	public final void execute(double t, double dt) {
		loop(ecosystem(),t,dt);
//		// get current systems to work with
//		Iterable<SystemComponent> focals = (Iterable<SystemComponent>) world().getSystemsByCategory(focalCategories);
//		// preparing data sampling
//		for (AggregatorFunction function : Afunctions)
//			function.prepareForSampling(focals, t);
//		// apply all functions attached to this Process
//		for (SystemComponent focal : focals) {
//			// A model can have no state
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
	
	public static TwFunctionTypes[] compatibleFunctionTypes = {
		ChangeCategoryDecision,
		ChangeState,
		DeleteDecision,
		CreateOtherDecision,
	};

}
