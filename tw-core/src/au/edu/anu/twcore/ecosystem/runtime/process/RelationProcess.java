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
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.runtime.containers.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTracker0D;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import fr.ens.biologie.generic.utils.Logging;

import static au.edu.anu.twcore.ecosystem.structure.RelationType.predefinedRelationTypes.returnsTo;

/**
 * A TwProcess that loops on established relations and executes methods on them or on their
 * start and end nodes
 * @author gignoux - 10 mars 2017
 *
 */
public class RelationProcess extends AbstractRelationProcess {

	private static Logger log = Logging.getLogger(RelationProcess.class);

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
	private HierarchicalContext focalContext = new HierarchicalContext();
	private HierarchicalContext otherContext = new HierarchicalContext();


	public RelationProcess(ComponentContainer world, RelationContainer relation,
			Timer timer, DynamicSpace<SystemComponent,LocatedSystemComponent> space, double searchR) {
		super(world,relation,timer,space,searchR);
	}

	private void executeFunctions(CategorizedContainer<SystemComponent> container, double t, double dt) {
		for (SystemComponent focal:container.items()) {
			for (SystemRelation sr:focal.getRelations()) { // this gets only out edges
				if (sr.membership().to().equals(to())) {
					SystemComponent other = (SystemComponent) sr.endNode();
					otherContext = getContext(other);
					// todo: data trackers ? tracking relations ?

					for (ChangeOtherCategoryDecisionFunction function:COCfunctions) {
			        	function.setFocalContext(focalContext);
			        	function.setOtherContext(otherContext);
			        	String newCat = function.changeCategory(t, dt, focal, other);
			        	if (newCat!=null) {
			        		// if other is involved in a lifecycle, it must be it's container's parent
			        		ComponentContainer otherLifeCycle = (ComponentContainer) other.container().parentContainer();
			        		if ((otherLifeCycle!=null) && (otherLifeCycle.categoryInfo() instanceof LifeCycle)) {
			        			ComponentContainer recruitContainer = null;
			        			// isnt there a better way to find the lifeCycle? via to()?
			        			// TODO: there MUST be one - cf in LifeCycle
								for (CategorizedContainer<SystemComponent> subContainer:otherLifeCycle.subContainers())
									if (subContainer.categoryInfo().categoryId().contains(newCat))
										recruitContainer = (ComponentContainer) subContainer;
								if ((recruitContainer==null) |
									!(recruitContainer.categoryInfo() instanceof SystemFactory)) {
									StringBuilder sb = new StringBuilder();
									sb.append("'")
										.append(otherContext.groupName)
										.append("' cannot recruit to '")
										.append(newCat)
										.append("'");
									log.severe(sb.toString());
								}
								else { // recruit other
									SystemComponent newRecruit = null;
									newRecruit = ((SystemFactory)recruitContainer.categoryInfo()).newInstance();
									newRecruit.autoVar().writeEnable();
									// carry over former ID as name
									if (focal.autoVar().name().isBlank())
										newRecruit.autoVar().name(other.id());
									else
										newRecruit.autoVar().name(other.autoVar().name());
									// carry over age and birthDate
									newRecruit.autoVar().age(other.autoVar().age());
									newRecruit.autoVar().birthDate(other.autoVar().birthDate());
									newRecruit.autoVar().writeDisable();
									// user-defined carry-overs
									for (ChangeOtherStateFunction func : function.getConsequences()) {
										HierarchicalContext newRecruitContext = otherContext.clone();
										setContext(newRecruitContext,recruitContainer);
										function.setOtherContext(newRecruitContext);
										function.setFocalContext(otherContext);
										func.changeOtherState(t, dt, other, newRecruit);
									}
									// replacement of old component by new one.
									other.container().removeItem(other);
									recruitContainer.addItem(newRecruit);
									// remove from tracklist - safe, data sending has already been made
									for (DataTracker0D tracker:tsTrackers)
										if (tracker.isTracked(other))
											tracker.removeTrackedItem(other);
									// CAUTION: this makes sure the new object takes the place of
									// the former one in any graph it is part of.
									// THIS WILL NOT WORK if there are edges to SystemFactory etc.
									// It is of tremendous importance that edges are only to
									// other SystemComponents.
									newRecruit.replace(other);
								}
			        		}
			        		else {
			        			log.severe("Missing life cycle");
			        		}
			        	}
					}
					if (other.currentState()!=null) // otherwise no point computing changes
				        for (ChangeOtherStateFunction function:COSfunctions) {
				        	function.setFocalContext(focalContext);
				        	function.setOtherContext(otherContext);
				        	focal.currentState().writeDisable();
				        	focal.nextState().writeDisable();
				        	other.currentState().writeDisable();
				        	other.nextState().writeEnable();
				        	function.changeOtherState(t, dt, focal, other);
				        	other.nextState().writeDisable();
			        }
			        for (DeleteOtherDecisionFunction function:DOfunctions) {
			        	function.setFocalContext(focalContext);
			        	function.setOtherContext(otherContext);
			        	if (function.delete(t, dt, focal, other)) {
			        		other.container().removeItem(other);
							for (DynamicSpace<SystemComponent,LocatedSystemComponent> space:((SystemFactory)other.membership()).spaces()) {
								space.unlocate(other);
								if (space.dataTracker()!=null)
									space.dataTracker().removeItem(currentStatus,
										other.container().itemId(other.id()));
							}
							// remove from tracklist if dead - safe, data sending has already been made
							for (DataTracker0D tracker:tsTrackers)
								if (tracker.isTracked(other))
									tracker.removeTrackedItem(other);
							// if present, spreads some values to other components
							// (e.g. "decomposition", or "erosion")
							if (!function.getConsequences().isEmpty())
								// TODO: the "returnsTo" relation type must be predefined somewhere
								for (SystemRelation to:other.getRelations(returnsTo.key())) {
									SystemComponent anotherOther = (SystemComponent) to.endNode();
									for (ChangeOtherStateFunction consequence:function.getConsequences()) {
										function.setFocalContext(otherContext);
										function.setOtherContext(getContext(anotherOther));
										consequence.changeOtherState(t, dt, other, anotherOther);
									}
							}
			        	}
			        }
			        for (MaintainRelationDecisionFunction function:MRfunctions) {
			        	function.setFocalContext(focalContext);
			        	function.setOtherContext(otherContext);
			        	if (!function.maintainRelation(t, dt, sr, focal, other))
			        		sr.container().removeItem(sr);
			        	// no consequences
			        }
			        for (ChangeRelationStateFunction function:CRfunctions) {
			        	function.setFocalContext(focalContext);
			        	function.setOtherContext(otherContext);
			        	function.changeRelationState(t, dt, focal, other, sr);
			        	// no consequences
			        }
				}
			}
		}
	}

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

	@Override
	protected void loop(CategorizedContainer<SystemComponent> container, double t, double dt) {
		if (container.categoryInfo() instanceof Ecosystem) {
			setContext(focalContext,container);
		}
		else if (container.categoryInfo() instanceof LifeCycle) {
			setContext(focalContext,container);
//			lifeCycle = (LifeCycle) container.categoryInfo();
//			lifeCycleContainer = (ComponentContainer) container;
		}
		else if (container.categoryInfo() instanceof SystemFactory)
			if (container.categoryInfo().belongsTo(focalCategories)) {
				setContext(focalContext,container);
//				focalGroup = (SystemFactory) container.categoryInfo();
				executeFunctions(container,t,dt);
				// track group state
				for (DataTracker0D tracker:tsTrackers)
					if (tracker.isTracked(container)) {
						tracker.recordItem(container.fullId());
						tracker.record(currentStatus,container.populationData());
				}
				focalContext.clear();
		}
		for (CategorizedContainer<SystemComponent> subc:container.subContainers())
			loop(subc,t,dt);

	}

}
