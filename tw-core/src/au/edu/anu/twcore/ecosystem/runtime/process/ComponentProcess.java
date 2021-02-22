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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentData;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.DescribedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SamplerDataTracker;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

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

	// helper class for generating new SystemComponents
	private class newBornSettings {
		ComponentFactory factory = null;
		ComponentContainer container = null;
//		String name = null;
	}

	// categories this Process applies to
	private SortedSet<Category> focalCategories = new TreeSet<>();
	private String categoryId = null;
	// functions run by this process
	private List<ChangeCategoryDecisionFunction> CCfunctions = new LinkedList<ChangeCategoryDecisionFunction>();
	private List<ChangeStateFunction> CSfunctions = new LinkedList<ChangeStateFunction>();
	private List<DeleteDecisionFunction> Dfunctions = new LinkedList<DeleteDecisionFunction>();
	private List<CreateOtherDecisionFunction> COfunctions = new LinkedList<CreateOtherDecisionFunction>();
	// local variables for looping
	private ArenaComponent arena = null;
	private GroupComponent focalGroup = null;
	private GroupComponent otherGroup = null;
	// lifecycle
	private LifeCycleComponent focalLifeCycle = null;
	private LifeCycleComponent otherLifeCycle = null;

	/**
	 * Constructor
	 * @param world the root component (ArenaComponent) for looping
	 * @param categories the categories of components this process applies to
	 * @param timer the Timer running this process
	 * @param space the Space attached to this process
	 * @param searchR the maximal search radius in this space
	 */
	public ComponentProcess(ArenaComponent world, Collection<Category> categories,
			Timer timer, DynamicSpace<SystemComponent> space, double searchR) {
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
				focalLifeCycle =null;
				focalGroup = null;
			}
			else if(component instanceof LifeCycleComponent) {
				focalLifeCycle = (LifeCycleComponent) component;
				focalGroup = null;
			}
			else if (component instanceof GroupComponent)
				focalGroup = (GroupComponent) component;
			// execute function on contained items, if any, and of proper categories
			if (component.content().itemCategorized()!=null) // if null, means all content is in subcontainers
				if (component.content().itemCategorized().belongsTo(focalCategories)) {
					component.content().change();
					for (SystemComponent sc:component.content().items())
						executeFunctions(t, dt, sc);
				}
			// in all cases, recurse on subcontainers to find more matching items
			// and recursively add context information to context.
			for (CategorizedContainer<SystemComponent> cc:component.content().subContainers()) {
				loop(t,dt,((DescribedContainer<SystemComponent>)cc).descriptors());
			}
		}
	}

	private void executeFunctions(double t, double dt, CategorizedComponent focal) {
		// normally in here arena, focalGroup and focalLifeCYcle should be uptodate if needed
		if (focal.currentState() != null) {
			focal.currentState().writeDisable(); // we dont care anymore about that, except for tables...
			focal.nextState().writeEnable();
		}
		//-----------------------------------------------------------------------------------
		// change state of this SystemComponent - easy
//		// if there are no changeState functions, then just copy currentState to nextState
//		if (CSfunctions.isEmpty()) {
//			TwData next = focal.nextState();
//			TwData current = focal.currentState();
//			for (String key:next.getKeysAsSet())
//				// TODO: what about tables ?????
//				// need a specific function or do it in generated code - that makes sense
//				next.setProperty(key, current.getPropertyValue(key));
//		}
		// if there are changeState functions, they take care of 
		if (CSfunctions.isEmpty())
			focal.setStateUnchanged(focal.stateUnchanged() & true);
		for (ChangeStateFunction function : CSfunctions) {
			function.changeState(t,dt,arena,focalLifeCycle,focalGroup,focal,space);
			focal.setStateUnchanged(false);
//			if (space!=null)
//				relocate((SystemComponent)focal);
		}
		if (focal.currentState() != null)
			focal.nextState().writeDisable();
		//-----------------------------------------------------------------------------------
		// delete decision function (NB: only applicable to SystemComponents)
		if (focal instanceof SystemComponent)
			for (DeleteDecisionFunction function : Dfunctions)
				if (function.delete(t, dt, arena, focalLifeCycle,focalGroup, focal, space)) {
			((SystemComponent)focal).container().removeItem((SystemComponent) focal); // safe - delayed removal
			// also remove from space !!!
//			if (space!=null)
//				unlocate((SystemComponent)focal);
			// remove from tracklist if dead - safe, data sending has already been made
			for (SamplerDataTracker<CategorizedComponent,?,Metadata> tracker:trackers)
				if (tracker.isTracked(focal))
					tracker.removeFromSample((SystemComponent) focal);
			// if present, spreads some values to other components
			// (e.g. "decomposition", or "erosion")
			for (ChangeOtherStateFunction consequence:function.getConsequences()) {
				for (SystemRelation to:focal.getOutRelations(returnsTo.key())) {
					SystemComponent other = (SystemComponent) to.endNode();
					otherGroup = null; // TODO: find it!
					// FLAW? here how does code generation know about the categories ?
					consequence.changeOtherState(t, dt,
						arena, null, focalGroup, focal,
						null, otherGroup, other, space);
//					if (space!=null)
//						relocate((SystemComponent)other);
				}
			}
		}
		//-----------------------------------------------------------------------------------
		// creation of other SystemComponents
		for (CreateOtherDecisionFunction function : COfunctions) {
			// if there is a life cycle, then it will return the next stage(s)
			List<newBornSettings> newBornSpecs = new ArrayList<>();
			if (focalLifeCycle!=null ) {
				newBornSettings nbs = new newBornSettings();
				GroupComponent toGroup = focalLifeCycle.produceGroup(function);
				nbs.factory = (ComponentFactory) toGroup.content().itemCategorized();
				nbs.container = (ComponentContainer) toGroup.content();
				newBornSpecs.add(nbs);
			}
			// without a life cycle, only objects of the same type can be created
			else {
				newBornSettings nbs = new newBornSettings();
				nbs.factory = (ComponentFactory) focal.elementFactory();
//				nbs.name = focal.membership().categoryId();
				nbs.container = (ComponentContainer) ((SystemComponent)focal).container();
				newBornSpecs.add(nbs);
			}
			for (newBornSettings nbs:newBornSpecs) {
				double result = function.nNew(t, dt, arena, focalLifeCycle, focalGroup, focal, space);
				// compute effective number of newBorns (taking the decimal part as a probability)
				double proba = function.rng().nextDouble();
				long n = (long) Math.floor(result);
				if (proba < (result - n))
					n += 1;
				for (int i = 0; i < n; i++) {
					SystemComponent newBorn = nbs.factory.newInstance();
					ComponentData nbcd = (ComponentData)newBorn.autoVar();
					nbcd.writeEnable();
					nbcd.birthDate(timer.twTime(t));
					nbcd.writeDisable();
					for (SetOtherInitialStateFunction func : function.getConsequences()) {
						if (focalLifeCycle==null)
							otherGroup = focalGroup;
						else
							otherGroup = (GroupComponent) nbs.container.descriptors();
						// by construction, offspring and parent belong to the same life cycle
						otherLifeCycle = focalLifeCycle;
						func.setOtherInitialState(t, dt,
							arena, focalLifeCycle, focalGroup, focal,
							otherLifeCycle, otherGroup, newBorn, space);
						// variables are set into nextState, so copy them to current
						// in order for SystemComponent.stepForward() to work properly
						if (newBorn.currentState()!=null) {
							newBorn.currentState().writeEnable();
							newBorn.currentState().setProperties(newBorn.nextState());
							newBorn.currentState().writeDisable();
						}
					}
//					if (space!=null)
//						locate(newBorn,nbs.container);
					// establish a parentTo relation if needed
					if (function.relateToOtherContainer()!=null) {
						// this to make sure the newBorn can return a valid hierarcicalId before a line is drawn
						newBorn.setContainer(nbs.container);
						function.relateToOtherContainer().addItem(focal,newBorn); // delayed addition
//						if (space!=null)
//							if (space.dataTracker()!=null)
//								space.dataTracker().createLine(((SystemComponent)focal).container().itemId(focal.id()),
//									nbs.container.itemId(newBorn.id()),
//									function.relateToOtherContainer().id());
					}
					// Reminder: this is just a list for delayed addition in ecosystem.effectChanges()
					// before this, the newBorn container field is null
					nbs.container.addItem(newBorn);
				}
			}
		}
		//-----------------------------------------------------------------------------------
		// recruit to other component type ("change category")
		// NB: only applicable to SystemComponents that are part of a LifeCycle
		if (focal instanceof SystemComponent)
			for (ChangeCategoryDecisionFunction function : CCfunctions) {
				String newCat = function.changeCategory(t, dt, arena,
					focalLifeCycle,focalGroup,focal,space);
				if (newCat != null) {
					otherGroup = focalLifeCycle.recruitGroup(newCat);
					ComponentContainer recruitContainer = (ComponentContainer)otherGroup.content();
					ComponentFactory recruitFactory = (ComponentFactory) otherGroup.content().itemCategorized();
					SystemComponent newRecruit = recruitFactory.newInstance();
					// carry over former ID as name
					ComponentData newRecruitAutoVar = (ComponentData) newRecruit.autoVar();
					ComponentData focalAutoVar = (ComponentData) focal.autoVar();
					newRecruitAutoVar.writeEnable();
					if (focalAutoVar.name().isBlank())
						newRecruitAutoVar.name(focal.id());
					else
						newRecruitAutoVar.name(focalAutoVar.name());
					// carry over age and birthdate
					newRecruitAutoVar.age(focalAutoVar.age());
					newRecruitAutoVar.birthDate(focalAutoVar.birthDate());
					newRecruitAutoVar.writeDisable();
					// apply consequences, if any
					otherLifeCycle = focalLifeCycle; // recruitment can only occur within the same life cycle
					for (SetOtherInitialStateFunction func : function.getConsequences()) {
						func.setOtherInitialState(t, dt, arena,
							focalLifeCycle, focalGroup, focal,
							otherLifeCycle, otherGroup, newRecruit,
							space);
						// variables are set into nextState, so copy them to current
						// in order for SystemComponent.stepForward() to work properly
						if (newRecruit.currentState()!=null) {
							newRecruit.currentState().writeEnable();
							newRecruit.currentState().setProperties(newRecruit.nextState());
							newRecruit.currentState().writeDisable();
						}
					}
					// replacement of old component by new one.
					((SystemComponent)focal).container().removeItem((SystemComponent) focal); // safe - delayed removal
					// this to make sure the newRecruit can return a valid hierarcicalId before a line is drawn
					newRecruit.setContainer(recruitContainer);
					recruitContainer.addItem(newRecruit); // safe: delayed addition
//					// manage space
//					if (space!=null) {
//						unlocate((SystemComponent)focal);
//						locate(newRecruit,recruitContainer);
//					}
					// if the recruit categories are compatible with the relations of the
					// focal, maintain them, otherwise discard (already done just before)
					for (SystemRelation sr:focal.getOutRelations())
						if (newRecruit.membership().belongsTo(sr.membership().from().categories())) {
							SystemComponent to = (SystemComponent)sr.endNode();
							sr.container().addItem(newRecruit,to);
//							// manage space
//							if (space.dataTracker()!=null)
//								space.dataTracker().createLine(recruitContainer.itemId(newRecruit.id()),
//									to.container().itemId(to.id()),sr.type());
					}
					for (SystemRelation sr:focal.getInRelations())
						if (newRecruit.membership().belongsTo(sr.membership().to().categories())) {
							SystemComponent from = (SystemComponent)sr.startNode();
							sr.container().addItem(from,newRecruit);
//							// manage space
//							if (space.dataTracker()!=null)
//								space.dataTracker().createLine(from.container().itemId(from.id()),
//									recruitContainer.itemId(newRecruit.id()),sr.type());
						}
					// remove from tracklist - safe, data sending has already been made
					for (SamplerDataTracker<CategorizedComponent,?,Metadata> tracker:trackers)
						if (tracker.isTracked(focal))
							tracker.removeFromSample(focal);
				}
				// else: no recruitment
			}
 		//-----------------------------------------------------------------------------------
		// call data trackers AFTER computations so that decorators are different from zero
		for (SamplerDataTracker<CategorizedComponent,?,Metadata> tracker:trackers)
			if (tracker.isTracked(focal)) {
				tracker.recordItem(focal.hierarchicalId());
				tracker.record(focal.currentState(),focal.decorators(),focal.autoVar());
		}
		//-----------------------------------------------------------------------------------
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName())
			.append(" applies to [")
			.append(categoryId())
			.append("]");
		if (CSfunctions.size()+Dfunctions.size()+COfunctions.size()+CCfunctions.size()>0) {
			sb.append(" functions {");
			for (TwFunction f:CSfunctions) sb.append(f.toString()).append(", ");
			for (TwFunction f:Dfunctions) sb.append(f.toString()).append(", ");
			for (TwFunction f:COfunctions) sb.append(f.toString()).append(", ");
			for (TwFunction f:CCfunctions) sb.append(f.toString()).append(", ");
			if (sb.charAt(sb.length()-2)==',') {
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}
			sb.append('}');
		}
		if (trackers.size()>0) {
			sb.append(" data trackers {");
			for (DataTracker<?,?> dt:trackers)
				sb.append(dt.getClass().getSimpleName()).append(", ");
			if (sb.charAt(sb.length()-2)==',') {
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}
			sb.append('}');
		}
		return sb.toString();
	}

	/**
	 * The list of function types that are compatible with a ComponentProcess
	 */
	public static TwFunctionTypes[] compatibleFunctionTypes = {
		ChangeCategoryDecision,
		ChangeState,
		DeleteDecision,
		CreateOtherDecision,
		SetInitialState
	};

}
