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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * A TwProcess that loops on a list of SystemComponents and executes methods on
 * them
 * 
 * @author gignoux - 10 mars 2017
 *
 */
public class ComponentProcess extends AbstractProcess implements Categorized<SystemComponent> {

	private SortedSet<Category> focalCategories = new TreeSet<>();
	private String categoryId = null;

	private List<ChangeCategoryDecisionFunction> CCfunctions = new LinkedList<ChangeCategoryDecisionFunction>();
	private List<ChangeStateFunction> CSfunctions = new LinkedList<ChangeStateFunction>();
	private List<DeleteDecisionFunction> Dfunctions = new LinkedList<DeleteDecisionFunction>();
	private List<CreateOtherDecisionFunction> COfunctions = new LinkedList<CreateOtherDecisionFunction>();
//	private List<AggregatorFunction> Afunctions = new LinkedList<AggregatorFunction>();
	
	// local variables for looping
	private TwData ecosystemPar = null;
	private TwData ecosystemVar = null;
	private ReadOnlyPropertyList ecosystemPop = null;
	private String ecosystemName = null;
	private TwData lifeCyclePar = null;
	private TwData lifeCycleVar = null;
	private ReadOnlyPropertyList lifeCyclePop = null;
	private String lifeCycleName = null;
	private TwData groupPar = null;
	private TwData groupVar = null;
	private ReadOnlyPropertyList groupPop = null;
	private String groupName = null;
	private LifeCycle lifeCycle = null;
	private Ecosystem ecosystem = null;
	private SystemFactory group = null;
	
	public ComponentProcess(Ecosystem world, Collection<Category> categories) {
		super(world);
		focalCategories.addAll(categories);
		categoryId = buildCategorySignature();
	}
	
	// recursive loop on all sub containers of the community
	private void loop(CategorizedContainer<SystemComponent> container,
		double t, double dt) {
		if (container.categoryInfo().belongsTo(focalCategories)) {
			if (container.categoryInfo() instanceof LifeCycle) {
				lifeCyclePar = container.parameters();
				lifeCycleVar = container.variables();
				lifeCyclePop = container.populationData();
				lifeCycleName = container.id();
				lifeCycle = (LifeCycle) container.categoryInfo();
			}
			else if (container.categoryInfo() instanceof Ecosystem) {
				ecosystemPar = container.parameters();
				ecosystemVar = container.variables();
				ecosystemPop = container.populationData();
				ecosystemName = container.id();
				ecosystem = (Ecosystem) container.categoryInfo();
			}
			else if (container.categoryInfo() instanceof SystemFactory) {
				groupPar = container.parameters();
				groupVar = container.variables();
				groupPop = container.populationData();
				groupName = container.id();
				group = (SystemFactory) container.categoryInfo();
			}
			executeFunctions(container,t,dt);
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
					function.changeState(t, dt, focal);
				}
				focal.nextState().writeDisable();
			}
			// change category
			for (ChangeCategoryDecisionFunction function : CCfunctions) {
				String result = function.changeCategory(t, dt, focal);
				if (result != null) {
					if (lifeCycle!=null) {
						// find the next stage !
						Set<Category> nextcats= lifeCycle.recruitTo(focal.membership());
						// how to get the factory ???
						// must be of the proper parameter set or so... how to know that?
						// must be in the lifecycle !
					}
					
//					SystemComponent newRecruit = focal.stage().species().stage(result).newSystem();
//					for (ChangeOtherStateFunction func : function.getConsequences())
//						func.changeOtherState(t, dt, focal, newRecruit);
//					focal.stage().tagSystemForDeletion(focal);
//					focal.stage().species().stage(result).tagSystemForInsertion(newRecruit);
					// NB id should be preserved !
					// NB: what about relations ?
				}
			}
			// delete itself
			for (DeleteDecisionFunction function : Dfunctions)
				if (function.delete(t, dt, focal))
					// missing: to which object should data return to ? this must depend on a
					// relation !
					container.removeItem(focal.id());
			// creation of other SystemComponents
			for (CreateOtherDecisionFunction function : COfunctions) {
				// TODO: where do we get this info from?
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
			}
		}
	}

	@Override
	public final void execute(double t, double dt) {
		loop(ecosystem().community(),t,dt);
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
//
//	@Override
//	public SystemComponent clone(SystemComponent item) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
