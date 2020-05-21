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
import static au.edu.anu.twcore.ecosystem.structure.RelationType.predefinedRelationTypes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.Metadata;
//import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTracker0D;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.generic.utils.Logging;

/**
 * A TwProcess that loops on a list of SystemComponents and executes methods on
 * them
 *
 * @author gignoux - 10 mars 2017
 *
 */
public class ComponentProcess
		extends AbstractProcess
		implements Categorized<SystemComponent> {

	private class newBornSettings {
		SystemFactory factory = null;
		ComponentContainer container = null;
		String name = null;
	}

	private static Logger log = Logging.getLogger(ComponentProcess.class);

	private SortedSet<Category> focalCategories = new TreeSet<>();
	private String categoryId = null;

	private List<ChangeCategoryDecisionFunction> CCfunctions = new LinkedList<ChangeCategoryDecisionFunction>();
	private List<ChangeStateFunction> CSfunctions = new LinkedList<ChangeStateFunction>();
	private List<DeleteDecisionFunction> Dfunctions = new LinkedList<DeleteDecisionFunction>();
	private List<CreateOtherDecisionFunction> COfunctions = new LinkedList<CreateOtherDecisionFunction>();
//	private List<RelocateFunction> Rfunctions = new LinkedList<RelocateFunction>();

	// local variables for looping
	private HierarchicalContext focalContext = new HierarchicalContext();
	private LifeCycle lifeCycle = null;
//	private Ecosystem ecosystem = null;
	private SystemFactory group = null;

	private ComponentContainer lifeCycleContainer = null;
//	private SystemContainer ecosystemContainer = null;
//	private SystemContainer groupContainer = null;

	// new API
	// the whole system component - always valie, always here, always unique
	private ArenaComponent arena = null;
	// lifecycle
	private GroupComponent focalGroup = null;

	public ComponentProcess(ArenaComponent world, Collection<Category> categories,
			Timer timer, DynamicSpace<SystemComponent,LocatedSystemComponent> space, double searchR) {
		super(world,timer,space,searchR);
		focalCategories.addAll(categories);
		categoryId = buildCategorySignature();
	}

	// recursive loop on all sub containers of the community
	@Override
	protected void loop( double t, double dt,
			HierarchicalComponent component) {
		// execute function on this item if proper categories
		if (component.membership().belongsTo(focalCategories))
			executeFunctions(t, dt, component);
		else if (component.content()!=null) {
			// set contextual information
			if (component instanceof ArenaComponent) {
				arena = (ArenaComponent) component;
				lifeCycle =null;
				focalGroup = null;
			}
			// lifecycle
			else if (component instanceof GroupComponent)
				focalGroup = (GroupComponent) component;
			// execute function on contained items, if any, and of proper categories
			if (component.content().itemCategorized()!=null) // if null, means all content is in subcontainers
				if (component.content().itemCategorized().belongsTo(focalCategories))
					for (SystemComponent sc:component.content().items())
						executeFunctions(t, dt, sc);
			// in all cases, recurse on subcontainers to find more matching items
			// and recursively add context information to context.
			for (CategorizedContainer<SystemComponent> cc:component.content().subContainers()) {
				loop(t,dt,cc.hierarchicalView());
			}
		}
	}

	private void executeFunctions(double t, double dt, CategorizedComponent focal) {
		// normally in here arena, focalGroup and focalLifeCYcle should be uptodate if needed
//		System.out.println("coucou from "+focal.toShortString()+" t = "+t);

//		if (space!=null) {
//			Box limits = space.boundingBox();
//			Point location = space.locationOf(focal).asPoint();
//			double[] newLoc = new double[location.dim()];
//		}
		if (focal.currentState() != null) {
			focal.currentState().writeDisable(); // we dont care anymore about that, except for tables...
			focal.nextState().writeEnable();
		}
		// change state of this SystemComponent - easy
		for (ChangeStateFunction function : CSfunctions) {
			function.changeState(t,dt,arena,null,focalGroup,null,focal,/*newLoc*/null);
		}
		if (focal.currentState() != null)
			focal.nextState().writeDisable();

		// call data trackers AFTER computations so that decorators are different from zero
		for (DataTracker<?,Metadata> tracker:trackers)
			if (tracker.isTracked(focal)) {
				tracker.recordItem(focalContext.buildItemId(focal.id()));
				tracker.record(currentStatus,focal.currentState(),focal.decorators(),focal.autoVar());
		}
	}

	// single loop on a container which matches the process categories
	private void executeFunctions(CategorizedContainer<SystemComponent> container,
		double t, double dt) {
//		Box limits = null;
//		if (space!=null)
//			limits = space.boundingBox();
//		for (SystemComponent focal:container.items()) {
//			// track component state
//			for (DataTracker0D tracker:tsTrackers)
//				if (tracker.isTracked(focal)) {
//				tracker.recordItem(focalContext.buildItemId(focal.id()));
//				tracker.record(currentStatus,focal.currentState());
//			}
//			// compute changes
//			Point location = null;
//			double[] newLoc = null;
//			if (space!=null) {
//				limits = space.boundingBox();
//				location = space.locationOf(focal).asPoint();
//				newLoc = new double[location.dim()];
//			}
//			if (focal.currentState() != null) { // otherwise no point computing changes!
//				focal.currentState().writeDisable();
//				focal.nextState().writeEnable();
//				// change state of this SystemComponent - easy
//				for (ChangeStateFunction function : CSfunctions) {
//					function.setFocalContext(focalContext);
////					function.changeState(t, dt, focal);
//					// NEW code for new TwFunction API
//					function.changeState(t, dt, limits,
//						focalContext.ecosystemParameters, ecosystem(),
//						focalContext.lifeCycleParameters, lifeCycleContainer,
//						focalContext.groupParameters, focal.container(),
//						focal.autoVar(), focal.constants(), focal.currentState(), focal.decorators(),
//						location, focal.nextState(), newLoc);
//					// end new code
//				}
//				focal.nextState().writeDisable();
//			}
//			// recruit to other component type ("change category")
//			for (ChangeCategoryDecisionFunction function : CCfunctions) {
////				function.setFocalContext(focalContext);
////				String newCat = function.changeCategory(t, dt, focal);
//				String newCat = function.changeCategory(t, dt, limits,
//					focalContext.ecosystemParameters, ecosystem(),
//					focalContext.lifeCycleParameters, lifeCycleContainer,
//					focalContext.groupParameters, focal.container(),
//					focal.autoVar(), focal.constants(), focal.currentState(), focal.decorators(),
//					location);
//				if (newCat != null) {
//					if (lifeCycle!=null) {
//						// find the next stage & instantiate new component
//						ComponentContainer recruitContainer = null;
//						for (CategorizedContainer<SystemComponent> subContainer:
//							lifeCycleContainer.subContainers())
//							if (subContainer.categoryInfo().categoryId().contains(newCat))
//								recruitContainer = (ComponentContainer) subContainer;
//						if ((recruitContainer==null) |
//							!(recruitContainer.categoryInfo() instanceof SystemFactory)) {
//							StringBuilder sb = new StringBuilder();
//							sb.append("'")
//								.append(focalContext.groupName)
//								.append("' cannot recruit to '")
//								.append(newCat)
//								.append("'");
//							log.severe(sb.toString());
//						}
//						else {
//							SystemComponent newRecruit = null;
//							newRecruit = ((SystemFactory)recruitContainer.categoryInfo()).newInstance();
//							newRecruit.autoVar().writeEnable();
//							// carry over former ID as name
//							if (focal.autoVar().name().isBlank())
//								newRecruit.autoVar().name(focal.id());
//							else
//								newRecruit.autoVar().name(focal.autoVar().name());
//							// carry over age and birthDate
//							newRecruit.autoVar().age(focal.autoVar().age());
//							newRecruit.autoVar().birthDate(focal.autoVar().birthDate());
//							newRecruit.autoVar().writeDisable();
//							// user-defined carry-overs
//							for (SetOtherInitialStateFunction func : function.getConsequences()) {
//								HierarchicalContext otherContext = focalContext.clone();
//								otherContext.groupParameters = recruitContainer.parameters();
////								otherContext.groupVariables = recruitContainer.variables();
//								otherContext.groupPopulationData = recruitContainer.populationData();
//								otherContext.groupName = recruitContainer.id();
////								function.setOtherContext(otherContext);
////								function.setFocalContext(focalContext);
////								func.changeOtherState(t, dt, focal, newRecruit);
//								func.setOtherInitialState(t, dt, limits,
//									focalContext.ecosystemParameters, ecosystem(),
//									focalContext.lifeCycleParameters, lifeCycleContainer,
//									focalContext.groupParameters, focal.container(),
//									otherContext.groupParameters, recruitContainer,
//									focal.autoVar(), focal.constants(),
//									focal.currentState(), focal.decorators(), location,
//									newRecruit.constants(), newRecruit.nextState(), newLoc);
//							}
//							// replacement of old component by new one.
//							container.removeItem(focal);
//							recruitContainer.addItem(newRecruit);
//							// remove from tracklist - safe, data sending has already been made
//							for (DataTracker0D tracker:tsTrackers)
//								if (tracker.isTracked(focal))
//									tracker.removeTrackedItem(focal);
//							// CAUTION: this makes sure the new object takes the place of
//							// the former one in any graph it is part of.
//							// THIS WILL NOT WORK if there are edges to SystemFactory etc.
//							// It is of tremendous importance that edges are only to
//							// other SystemComponents.
//							newRecruit.replace(focal);
//						}
//					}
//				}
//			}
//			// delete itself
//			for (DeleteDecisionFunction function : Dfunctions) {
////				function.setFocalContext(focalContext); // not needed anymore
////				if (function.delete(t, dt, focal)) {
//				if (function.delete(t, dt, limits,
//					focalContext.ecosystemParameters, ecosystem(),
//					focalContext.lifeCycleParameters, lifeCycleContainer,
//					focalContext.groupParameters, focal.container(),
//					focal.autoVar(), focal.constants(),
//					focal.currentState(), focal.decorators(), location)) {
//					container.removeItem(focal); // safe - delayed removal
//					// also remove from space !!!
//					for (DynamicSpace<SystemComponent,LocatedSystemComponent> space:
//							((SystemFactory)focal.membership()).spaces()) {
//						space.unlocate(focal);
//						if (space.dataTracker()!=null)
//							space.dataTracker().removeItem(currentStatus,container.itemId(focal.id()));
//					}
//					// remove from tracklist if dead - safe, data sending has already been made
//					for (DataTracker0D tracker:tsTrackers)
//						if (tracker.isTracked(focal))
//							tracker.removeTrackedItem(focal);
//					// if present, spreads some values to other components
//					// (e.g. "decomposition", or "erosion")
//					for (ChangeOtherStateFunction consequence:function.getConsequences()) {
//						for (SystemRelation to:focal.getRelations(returnsTo.key())) {
//							SystemComponent other = (SystemComponent) to.endNode();
//							// FLAW? here how does code generation know about the categories ?
//							consequence.changeOtherState(t, dt, limits,
//								focalContext.ecosystemParameters, ecosystem(),
//								focalContext.lifeCycleParameters, lifeCycleContainer,
//								focalContext.groupParameters, focal.container(),
//								other.container().parameters(), other.container(),
//								focal.autoVar(), focal.constants(),
//								focal.currentState(), focal.decorators(), location,
//								other.autoVar(), other.constants(),
//								other.currentState(), other.decorators(), location,
//								other.nextState(), newLoc);
//						}
//					}
////					replaced by the above
////					if (!function.getConsequences().isEmpty())
////						// TODO: the "returnsTo" relation type must be predefined somewhere
////						for (SystemRelation to:focal.getRelations(returnsTo.key())) {
////							SystemComponent other = (SystemComponent) to.endNode();
////							for (ChangeOtherStateFunction consequence:function.getConsequences()) {
////								function.setFocalContext(focalContext);
////								consequence.changeOtherState(t, dt, focal, other);
////							}
////					}
//				}
//			}
//			// creation of other SystemComponents
//			for (CreateOtherDecisionFunction function : COfunctions) {
//				// if there is a life cycle, then it will return the next stage(s)
//				List<newBornSettings> newBornSpecs = new ArrayList<>();
//				if (lifeCycle!=null ) {
//					// search for category signatures of produce targets
//					for (String catSignature:lifeCycle.produceTo(group))
//						for (CategorizedContainer<SystemComponent> subc:
//							lifeCycleContainer.subContainers())
//						// since lifeCycle stages only have one category this test should do
//						if (subc.categoryInfo().categoryId().contains(catSignature)) {
//							newBornSettings nbs = new newBornSettings();
//							nbs.name = subc.categoryInfo().categoryId();
//							nbs.factory = (SystemFactory) subc.categoryInfo();
//							nbs.container = (ComponentContainer) subc;
//							newBornSpecs.add(nbs);
//					}
//				}
//				// without a life cycle, only objects of the same type can be created
//				else {
//					newBornSettings nbs = new newBornSettings();
//					nbs.factory = group;
//					nbs.name = group.categoryId();
//					nbs.container = (ComponentContainer) container;
//					newBornSpecs.add(nbs);
//				}
//				function.setFocalContext(focalContext);
//				for (newBornSettings nbs:newBornSpecs) {
////					double result = function.nNew(t, dt, focal, nbs.name);
//					double result = function.nNew(t, dt, limits,
//						focalContext.ecosystemParameters, ecosystem(),
//						focalContext.lifeCycleParameters, lifeCycleContainer,
//						focalContext.groupParameters, focal.container(),
//						focal.autoVar(), focal.constants(),
//						focal.currentState(), focal.decorators(), location);
//					// compute effective number of newBorns (taking the decimal part as a probability)
//					double proba = function.rng().nextDouble();
//					long n = (long) Math.floor(result);
//					if (proba < (result - n))
//						n += 1;
//					for (int i = 0; i < n; i++) {
//						SystemComponent newBorn = nbs.factory.newInstance();
//						for (SetOtherInitialStateFunction func : function.getConsequences()) {
////							function.setFocalContext(focalContext);
////							func.changeState(t, dt, newBorn);
//							// TODO workout multiple category sets for descendants
//							HierarchicalContext newBornContext = focalContext.clone();
//							newBornContext.groupParameters = nbs.container.parameters();
////							newBornContext.groupVariables = nbs.container.variables();
//							newBornContext.groupPopulationData = nbs.container.populationData();
//							newBornContext.groupName = nbs.container.id();
//							func.setOtherInitialState(t, dt, limits,
//								focalContext.ecosystemParameters, ecosystem(),
//								focalContext.lifeCycleParameters, lifeCycleContainer,
//								focalContext.groupParameters, focal.container(),
//								newBornContext.groupParameters, nbs.container,
//								focal.autoVar(), focal.constants(),
//								focal.currentState(), focal.decorators(), location,
//								newBorn.constants(), newBorn.nextState(), newLoc);
//							if (newLoc==null) {
//								log.warning("No location returned by relocate(...): default location generated");
//								newLoc = space.defaultLocation();
//							}
//							if (newLoc.length!=space.ndim()) {
//								log.warning("Wrong number of dimensions: default location generated");
//								newLoc = space.defaultLocation();
//							}
//							space.locate(newBorn,newLoc);
//							if (space.dataTracker()!=null)
//								space.dataTracker().recordItem(currentStatus,newLoc,
//									// caution - item not yet in container.
//									nbs.container.itemId(newBorn.id()));
//						}
////						HierarchicalContext newBornContext = null;
////						if ((!function.getChangeOtherStateConsequences().isEmpty()) |
////								(!((SystemFactory)newBorn.membership()).spaces().isEmpty())) {
////							newBornContext = focalContext.clone();
////							newBornContext.groupParameters = nbs.container.parameters();
////							newBornContext.groupVariables = nbs.container.variables();
////							newBornContext.groupPopulationData = nbs.container.populationData();
////							newBornContext.groupName = nbs.container.id();
////						}
////						for (ChangeOtherStateFunction func : function.getChangeOtherStateConsequences()) {
////							function.setOtherContext(newBornContext);
////							function.setFocalContext(focalContext);
////							func.changeOtherState(t, dt, focal, newBorn);
////						}
//						// location of newBorn in space // now done in init
////						for (DynamicSpace<SystemComponent,LocatedSystemComponent> space:((SystemFactory)newBorn.membership()).spaces()) {
////							RelocateFunction func = ((SystemFactory)newBorn.membership()).locatorFunction(space);
////							func.setFocalContext(newBornContext);
////							double[] newLocation = func.relocate(t, dt, newBorn, null,
////								focal, space.locationOf(focal), space.boundingBox());
////							if (newLocation==null) {
////								log.warning("No location returned by relocate(...): default location generated");
////								newLocation = space.defaultLocation();
////							}
////							if (newLocation.length!=space.ndim()) {
////								log.warning("Wrong number of dimensions: default location generated");
////								newLocation = space.defaultLocation();
////							}
////							space.locate(newBorn,newLocation);
////							if (space.dataTracker()!=null)
////								space.dataTracker().recordItem(currentStatus,newLocation,
////									// caution - item not yet in container.
////									nbs.container.itemId(newBorn.id()));
////						}
//						if (function.relateToOther())
//							focal.relateTo(newBorn,parentTo.key()); // delayed addition
//						// welcome newBorn in container!
//						nbs.container.addItem(newBorn); // safe - delayed addition
//					}
//				}
//			}
//			// TODO: relocate self (ie movement)
//		}
	}

	@Override
	public void addFunction(TwFunction function) {
		if (!isSealed()) {
			if (function instanceof ChangeCategoryDecisionFunction)
				CCfunctions.add((ChangeCategoryDecisionFunction) function);
			else if (function instanceof ChangeStateFunction)
				CSfunctions.add((ChangeStateFunction) function);
			else if (function instanceof DeleteDecisionFunction)
				Dfunctions.add((DeleteDecisionFunction) function);
			else if (function instanceof CreateOtherDecisionFunction)
				COfunctions.add((CreateOtherDecisionFunction) function);
//			else if (function instanceof RelocateFunction)
//				Rfunctions.add((RelocateFunction) function);
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
		SetInitialState
//		Relocate
	};

}
