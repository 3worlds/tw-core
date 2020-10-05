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

import java.util.LinkedList;
import java.util.List;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.DescribedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;

/**
 * A TwProcess that loops on established relations and executes methods on them or on their
 * start and end nodes
 * @author gignoux - 10 mars 2017
 *
 */
public class RelationProcess extends AbstractRelationProcess {

    private List<ChangeOtherCategoryDecisionFunction> COCfunctions =
    	new LinkedList<ChangeOtherCategoryDecisionFunction>();
    private List<ChangeOtherStateFunction> COSfunctions =
    	new LinkedList<ChangeOtherStateFunction>();
    private List<DeleteOtherDecisionFunction> DOfunctions =
    	new LinkedList<DeleteOtherDecisionFunction>();
    private List<MaintainRelationDecisionFunction> MRfunctions =
    	new LinkedList<MaintainRelationDecisionFunction>();
    private List<ChangeRelationStateFunction> CRfunctions =
    	new LinkedList<ChangeRelationStateFunction>();

	// local variables for looping
    // actually all the proper looping code is in SearchProcess and could be moved up to AbstractRelationProcess
//	private HierarchicalContext focalContext = new HierarchicalContext();
//	private HierarchicalContext otherContext = new HierarchicalContext();
//	private ComponentContainer ecosystemContainer = null;
//	private ComponentContainer lifeCycleContainer = null;
	//new API
	private ArenaComponent arena = null;
	private CategorizedComponent focalLifeCycle = null;
	private CategorizedComponent otherLifeCycle = null;
	private CategorizedComponent focalGroup = null;
	private CategorizedComponent otherGroup = null;


	public RelationProcess(ArenaComponent world, RelationContainer relation,
			Timer timer, DynamicSpace<SystemComponent,LocatedSystemComponent> space, double searchR) {
		super(world,relation,timer,space,searchR);
	}

//	private void executeFunctions(CategorizedContainer<SystemComponent> container, double t, double dt) {
//		Box limits = null;
//		if (space!=null)
//			limits = space.boundingBox();
//		for (SystemComponent focal:container.items()) {
//			Point focalLocation = null;
//			if (space!=null)
//				focalLocation = space.locationOf(focal).asPoint();
//			for (SystemRelation sr:focal.getRelations()) { // this gets only out edges
//				if (sr.membership().to().equals(to())) {
//					SystemComponent other = (SystemComponent) sr.endNode();
//					otherContext = getContext(other);
//					Point otherLocation = null;
//					if (space!=null)
//						otherLocation = space.locationOf(other).asPoint();
//
//					// todo: data trackers ? tracking relations ?
//
//					for (ChangeOtherCategoryDecisionFunction function:COCfunctions) {
//			        	function.setFocalContext(focalContext);
//			        	function.setOtherContext(otherContext);
////			        	String newCat = function.changeCategory(t, dt, focal, other);
//			        	String newCat = function.changeCategory(t, dt, limits,
//			        		focalContext.ecosystemParameters, ecosystemContainer,
//			        		focalContext.lifeCycleParameters, lifeCycleContainer,
//			        		focalContext.groupParameters, (ComponentContainer) container,
//			        		otherContext.groupParameters, other.container(),
//			        		focal.autoVar(), focal.constants(), focal.currentState(),
//			        		focal.decorators(), focalLocation,
//			        		other.autoVar(), other.constants(), other.currentState(),
//			        		other.decorators(), otherLocation);
//			        	if (newCat!=null) {
//			        		// if other is involved in a lifecycle, it must be it's container's parent
//			        		ComponentContainer otherLifeCycle = (ComponentContainer) other.container().parentContainer();
//			        		if ((otherLifeCycle!=null) && (otherLifeCycle.categoryInfo() instanceof LifeCycle)) {
//			        			ComponentContainer recruitContainer = null;
//			        			// isnt there a better way to find the lifeCycle? via to()?
//			        			// TODO: there MUST be one - cf in LifeCycle
//								for (CategorizedContainer<SystemComponent> subContainer:otherLifeCycle.subContainers())
//									if (subContainer.categoryInfo().categoryId().contains(newCat))
//										recruitContainer = (ComponentContainer) subContainer;
//								if ((recruitContainer==null) |
//									!(recruitContainer.categoryInfo() instanceof SystemFactory)) {
//									StringBuilder sb = new StringBuilder();
//									sb.append("'")
//										.append(otherContext.groupName)
//										.append("' cannot recruit to '")
//										.append(newCat)
//										.append("'");
//									log.severe(sb.toString());
//								}
//								else { // recruit other
//									SystemComponent newRecruit = null;
//									newRecruit = ((SystemFactory)recruitContainer.categoryInfo()).newInstance();
//									newRecruit.autoVar().writeEnable();
//									// carry over former ID as name
//									if (focal.autoVar().name().isBlank())
//										newRecruit.autoVar().name(other.id());
//									else
//										newRecruit.autoVar().name(other.autoVar().name());
//									// carry over age and birthDate
//									newRecruit.autoVar().age(other.autoVar().age());
//									newRecruit.autoVar().birthDate(other.autoVar().birthDate());
//									newRecruit.autoVar().writeDisable();
//									// user-defined carry-overs
//									// NB carry over from other to newRecruit - focal doesnt change!
//									// NB recruits belong to the same life cycle as pre-recruits
//									// but maybe not as focals
//									for (SetOtherInitialStateFunction func : function.getConsequences()) {
//										HierarchicalContext newRecruitContext = otherContext.clone();
//										setContext(newRecruitContext,recruitContainer);
//										function.setOtherContext(newRecruitContext);
//										function.setFocalContext(otherContext);
////										func.changeOtherState(t, dt, other, newRecruit);
//										double[] newRecruitLoc = new double[otherLocation.dim()];
//										func.setOtherInitialState(t, dt, limits,
//							        		focalContext.ecosystemParameters, ecosystemContainer,
//							        		otherContext.lifeCycleParameters, lifeCycleContainer, // check this one
//							        		otherContext.groupParameters, other.container(),
//							        		newRecruitContext.groupParameters, recruitContainer,
//							        		other.autoVar(), other.constants(), other.currentState(),
//							        		other.decorators(), otherLocation,
//							        		newRecruit.constants(), newRecruit.nextState(),newRecruitLoc);
//										// TODO here: set new location !!
//										//
//										//
//									}
//									// replacement of old component by new one.
//									other.container().removeItem(other);
//									recruitContainer.addItem(newRecruit);
//									// remove from tracklist - safe, data sending has already been made
//									for (DataTracker0D tracker:tsTrackers)
//										if (tracker.isTracked(other))
//											tracker.removeTrackedItem(other);
//									// CAUTION: this makes sure the new object takes the place of
//									// the former one in any graph it is part of.
//									// THIS WILL NOT WORK if there are edges to SystemFactory etc.
//									// It is of tremendous importance that edges are only to
//									// other SystemComponents.
//									newRecruit.replace(other);
//								}
//			        		}
//			        		else {
//			        			log.severe("Missing life cycle");
//			        		}
//			        	}
//					}
//					if (other.currentState()!=null) // otherwise no point computing changes
//				        for (ChangeOtherStateFunction function:COSfunctions) {
//				        	function.setFocalContext(focalContext);
//				        	function.setOtherContext(otherContext);
//				        	// these shouldnt be needed anymore because user code cannot write in there
//				        	focal.currentState().writeDisable();
//				        	focal.nextState().writeDisable();
//				        	other.currentState().writeDisable();
//				        	other.nextState().writeEnable();
////				        	function.changeOtherState(t, dt, focal, other);
//				        	double[] nextOtherLoc = null;
//				        	if (otherLocation!=null)
//				        		nextOtherLoc = new double[otherLocation.dim()];
//				        	function.changeOtherState(t, dt, limits,
//				        		focalContext.ecosystemParameters, ecosystemContainer,
//				        		focalContext.lifeCycleParameters, lifeCycleContainer,
//				        		focalContext.groupParameters, (ComponentContainer) container,
//				        		otherContext.groupParameters, other.container(),
//				        		focal.autoVar(), focal.constants(), focal.currentState(),
//				        		focal.decorators(), focalLocation,
//				        		other.autoVar(), other.constants(), other.currentState(),
//				        		other.decorators(), otherLocation,
//				        		other.nextState(), nextOtherLoc);
//				        	// TODO: set new other location
//				        	//
//				        	//
//				        	other.nextState().writeDisable();
//			        }
//			        for (DeleteOtherDecisionFunction function:DOfunctions) {
//			        	function.setFocalContext(focalContext);
//			        	function.setOtherContext(otherContext);
////			        	if (function.delete(t, dt, focal, other)) {
//			        	if (function.delete(t, dt, limits,
//			        		focalContext.ecosystemParameters, ecosystemContainer,
//			        		focalContext.lifeCycleParameters, lifeCycleContainer,
//			        		focalContext.groupParameters, (ComponentContainer) container,
//			        		otherContext.groupParameters, other.container(),
//			        		focal.autoVar(), focal.constants(), focal.currentState(),
//			        		focal.decorators(), focalLocation,
//			        		other.autoVar(), other.constants(), other.currentState(),
//			        		other.decorators(), otherLocation)) {
//			        		other.container().removeItem(other);
//							for (DynamicSpace<SystemComponent,LocatedSystemComponent> space:((SystemFactory)other.membership()).spaces()) {
//								space.unlocate(other);
//								if (space.dataTracker()!=null)
//									space.dataTracker().removeItem(currentStatus,
//										other.container().itemId(other.id()));
//							}
//							// remove from tracklist if dead - safe, data sending has already been made
//							for (DataTracker0D tracker:tsTrackers)
//								if (tracker.isTracked(other))
//									tracker.removeTrackedItem(other);
//							// if present, spreads some values to other components
//							// (e.g. "decomposition", or "erosion")
//							// NB: carry-over data is from other to another other
//							for (ChangeOtherStateFunction consequence:function.getConsequences()) {
//								for (SystemRelation to:other.getRelations(returnsTo.key())) {
//									SystemComponent anotherOther = (SystemComponent) to.endNode();
//									// FLAW? here how does code generation know about the categories ?
//									Point anotherOtherLoc = null;
//									double[] newLoc = null;
//									if (space!=null) {
//										anotherOtherLoc = space.locationOf(anotherOther).asPoint();
//										newLoc = new double[anotherOtherLoc.dim()];
//									}
//									consequence.changeOtherState(t, dt, limits,
//										focalContext.ecosystemParameters, ecosystem(),
//										otherContext.lifeCycleParameters, lifeCycleContainer, // check this one
//										otherContext.groupParameters, other.container(),
//										other.container().parameters(), other.container(),
//										other.autoVar(), other.constants(),
//										other.currentState(), other.decorators(), otherLocation,
//										anotherOther.autoVar(), anotherOther.constants(),
//										anotherOther.currentState(), anotherOther.decorators(),
//										anotherOtherLoc,
//										anotherOther.nextState(),newLoc);
//									// TODO: set newLoc
//									//
//									//
//								}
//							}
////							if (!function.getConsequences().isEmpty())
////								// TODO: the "returnsTo" relation type must be predefined somewhere
////								for (SystemRelation to:other.getRelations(returnsTo.key())) {
////									SystemComponent anotherOther = (SystemComponent) to.endNode();
////									for (ChangeOtherStateFunction consequence:function.getConsequences()) {
////										function.setFocalContext(otherContext);
////										function.setOtherContext(getContext(anotherOther));
////										consequence.changeOtherState(t, dt, other, anotherOther);
////									}
////							}
//			        	}
//			        }
//			        // this should bein a loop on relations as it is symmetrical
//			        for (MaintainRelationDecisionFunction function:MRfunctions) {
//			        	function.setFocalContext(focalContext);
//			        	function.setOtherContext(otherContext);
////			        	if (!function.maintainRelation(t, dt, sr, focal, other))
//			        	if (!function.maintainRelation(t, dt, limits,
//			        		focalContext.ecosystemParameters, ecosystemContainer,
//			        		focalContext.lifeCycleParameters, lifeCycleContainer,
//			        		focalContext.groupParameters, (ComponentContainer) container,
//			        		otherContext.groupParameters, other.container(),
//			        		focal.autoVar(), focal.constants(), focal.currentState(),
//			        		focal.decorators(), focalLocation,
//			        		other.autoVar(), other.constants(), other.currentState(),
//			        		other.decorators(), otherLocation))
//			        		sr.container().removeItem(sr);
//			        	// no consequences
//			        }
//			        // this should be in a loop on relations as it is symmetrical
//			        for (ChangeRelationStateFunction function:CRfunctions) {
//			        	function.setFocalContext(focalContext);
//			        	function.setOtherContext(otherContext);
//						double[] focalNextLoc = null;
//						double[] otherNextLoc = null;
//						if (space!=null) {
//							focalNextLoc = new double[focalLocation.dim()];
//							otherNextLoc = new double[otherLocation.dim()];
//						}
//
////			        	function.changeRelationState(t, dt, focal, other, sr);
//			        	function.changeRelationState(t, dt, limits,
//				        		focalContext.ecosystemParameters, ecosystemContainer,
//				        		focalContext.lifeCycleParameters, lifeCycleContainer,
//				        		focalContext.groupParameters, (ComponentContainer) container,
//				        		otherContext.groupParameters, other.container(),
//				        		focal.autoVar(), focal.constants(), focal.currentState(),
//				        		focal.decorators(), focalLocation,
//				        		other.autoVar(), other.constants(), other.currentState(),
//				        		other.decorators(), otherLocation,
//				        		focal.nextState(), focalNextLoc,
//				        		other.nextState(), otherNextLoc);
//			        	// no consequences
//			        }
//				}
//			}
//		}
//	}

	@Override
	public void addFunction(TwFunction function) {
		if (!isSealed()) {
			if (function instanceof ChangeOtherCategoryDecisionFunction)
				COCfunctions.add((ChangeOtherCategoryDecisionFunction) function);
			else if (function instanceof ChangeOtherStateFunction)
				COSfunctions.add((ChangeOtherStateFunction) function);
			else if (function instanceof ChangeRelationStateFunction)
				CRfunctions.add((ChangeRelationStateFunction) function);
			else if (function instanceof DeleteOtherDecisionFunction)
				DOfunctions.add((DeleteOtherDecisionFunction) function);
			else if (function instanceof MaintainRelationDecisionFunction)
				MRfunctions.add((MaintainRelationDecisionFunction) function);
		}
	}

	private void executeFunctions(double t, double dt,
			CategorizedComponent focal,
			CategorizedComponent other,
			SystemRelation rel) {
		// ChangeOtherStateFunction
        for (ChangeOtherStateFunction function:COSfunctions) {
        	if (other.currentState()!=null) {
	        	other.currentState().writeDisable();
	        	other.nextState().writeEnable();
        	}
			double[] newLoc = null;
			if (space!=null)
				newLoc = new double[space.ndim()];
        	function.changeOtherState(t,dt,
        		arena, focalLifeCycle, focalGroup, focal,
        		otherLifeCycle, otherGroup, other, space,newLoc);
			if (space!=null)
				relocate((SystemComponent)other,newLoc);
        	if (other.currentState()!=null)
        		other.nextState().writeDisable();
        }
        
        // MaintainRelationDecisionFunction for ephemeral relations
        if (!rel.container().isPermanent()) {
	        // MaintainRelationDecision
	        for (MaintainRelationDecisionFunction function:MRfunctions) {
	        	if (!function.maintainRelation(t, dt, arena, 
	        		/*lifeCycle*/null, focalGroup, focal, 
	        		/*otherLifeCycle*/null, otherGroup, other, space)) {
	        		rel.container().removeItem(rel);
		        	if (space!=null)
		        		if (space.dataTracker()!=null)
		        			space.dataTracker().deleteLine(((SystemComponent)focal).container().itemId(focal.id()),
		        				((SystemComponent)other).container().itemId(other.id()));
	        	}
	        }
	        // if there is no maintainrelation function, the relation only lasts for 1 time step
	        if (MRfunctions.isEmpty())
	        	rel.container().removeItem(rel);
        }   
        
        // ChangeRelationStateFunction
        for (ChangeRelationStateFunction function:CRfunctions) {
        	if (other.currentState()!=null) {
	        	other.currentState().writeDisable();
	        	other.nextState().writeEnable();
        	}
        	if (focal.currentState()!=null) {
        		focal.currentState().writeDisable();
        		focal.nextState().writeEnable();
        	}
			double[] newFocalLoc = null;
			double[] newOtherLoc = null;
			if (space!=null) {
				newFocalLoc = new double[space.ndim()];
				newOtherLoc = new double[space.ndim()];
			}
        	function.changeRelationState(t, dt, arena, /*lifeCycle*/null, focalGroup, focal, 
        			/*otherLifeCycle*/null, otherGroup, other, space, newFocalLoc, newOtherLoc);
			if (space!=null) {
				if (!space.equalLocation(space.locationOf((SystemComponent)focal), newFocalLoc))
					relocate((SystemComponent)focal,newFocalLoc);
				if (!space.equalLocation(space.locationOf((SystemComponent)other), newOtherLoc))
					relocate((SystemComponent)other,newOtherLoc);
			}
        	if (other.currentState()!=null)
        		other.nextState().writeDisable();
        	if (focal.currentState()!=null)
        		focal.nextState().writeDisable();
        }
	}

	// manages the looping over others
	// NB: two possible optimisations here
	// * process both ends of a relation in one single pass - changeOtherState enables it
	// * replace edge list by a map indexed by edge labels -> faster access to the proper edges
	private void loopOnOthers(double t, double dt, CategorizedComponent focal) {
		for (SystemRelation sr:focal.getRelations()) {
			if (sr.membership().to().equals(to())) {
				CategorizedComponent other = (CategorizedComponent) sr.endNode();
				((SystemComponent) other).container().change();
				otherGroup = ((SystemComponent) other).container().descriptors();
				// TODO: fix this:
//				otherLifeCycle = otherGroup.container().hierarchicalView();
				executeFunctions(t,dt,focal,other,sr);
			}
		}
	}

	// almost same as in ComponentProcess
	// RECURSIVE
	// manages the looping over focals
	@Override
	protected void loop(double t, double dt, HierarchicalComponent component) {
		if (component.membership().belongsTo(focalCategories)) {
			loopOnOthers(t,dt,component);
			component.content().change();
		}
		else if (component.content()!=null) {
			if (component instanceof ArenaComponent) {
				arena = (ArenaComponent) component;
				focalLifeCycle = null;
				focalGroup = null;
			}
			// TODO: lifecycle
			else if (component instanceof GroupComponent) {
				focalGroup = component;
			}
			// execute function on contained items, if any, and of proper categories
			if (component.content().itemCategorized()!=null) // if null, means all content is in subcontainers
				if (component.content().itemCategorized().belongsTo(focalCategories)) {
					component.content().change();
					for (SystemComponent sc:component.content().items())
						loopOnOthers(t, dt, sc);
				}
			// in all cases, recurse on subcontainers to find more matching items
			// and recursively add context information to context.
			for (CategorizedContainer<SystemComponent> cc:component.content().subContainers()) {
				loop(t,dt,((DescribedContainer<SystemComponent>)cc).descriptors());
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(" functions {");
		for (TwFunction f:COSfunctions) sb.append(f.toString()).append(", ");
		for (TwFunction f:MRfunctions) sb.append(f.toString()).append(", ");
		for (TwFunction f:CRfunctions) sb.append(f.toString()).append(", ");
		if (sb.charAt(sb.length()-2)==',') {
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append('}');
		return sb.toString();
	}

}
