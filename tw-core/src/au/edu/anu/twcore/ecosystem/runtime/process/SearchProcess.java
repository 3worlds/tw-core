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

import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.RelateToDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.DescribedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;

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
	private ArenaComponent arena = null;
	private CategorizedComponent focalLifeCycle = null;
	private CategorizedComponent otherLifeCycle = null;
	private CategorizedComponent focalGroup = null;
	private CategorizedComponent otherGroup = null;

	public SearchProcess(ArenaComponent world, RelationContainer relation,
			Timer timer, DynamicSpace<SystemComponent> space,double searchR, int searchN) {
		super(world, relation, timer, space, searchR, searchN);
	}

	@Override
	public void addFunction(TwFunction function) {
		if (!isSealed()) {
			if (function instanceof RelateToDecisionFunction)
				RTfunctions.add((RelateToDecisionFunction) function);
		}
	}

	/**
	 * brute force inefficient (O(n²)) search for other SystemComponents to relate to.
	 * This version is only used for ephemeral relations.
	 * No need to set group or lifecycle here, done in executeFunctions
	 * Recursive.
	 *
	 * @param t			current time in this process Timer units
	 * @param dt		current time step in this process Timer units
	 * @param focal		the SystemComponent of interest
	 * @param others	the list of candidate SystemComponents for establishing a relation to
	 */
	private void crudeLoop(double t, double dt,
		HierarchicalComponent focal,
		DescribedContainer<SystemComponent> others) {
			// if others contains SCs of the proper categories
			if ((others.itemCategorized()!=null) &&
				(others.itemCategorized().belongsTo(otherCategories)) ) {
				// loop on all SCs contained in focal's container (focal is the avatar/descriptors)
				// check on focal categories has already been done before entering this method
				for (SystemComponent fc:focal.content().items()) {
					Collection<SystemComponent> fcOthers = fc.getOutRelatives(relContainer.type().id());
					// loop on candidates for an ephemeral relation, excluding focal component (no self relations)
					for (SystemComponent sc: others.items())
						if (fc!=sc) {
						// if other and sc are not yet related, check they can be
						if (!fcOthers.contains(sc))
							executeFunctions(t,dt,fc,sc);
						// if other and sc are already related AND autodelete is true,
						// check if they stay related or if relation is deleted
						else if (relContainer.autoDelete()) {
							// this could be optimised according to relation lifespan
							// by having two lists of items in space, one for just added items,
							// one for items added for at least 1 time step
							Collection<SystemRelation> fcRelations = fc.getOutRelations(relContainer.type().id());
							// find relation between focal and other and check it
							for (SystemRelation rel:fcRelations)
								if (rel.endNode()==sc) {
									executeFunctions(t,dt,fc,sc,rel);
									break;
							}
						}
					}
				}
			}
			// if others doesnt contain SCs of the proper categories: loop on others' subcontainers
			else
				for (CategorizedContainer<SystemComponent> subc: others.subContainers())
					crudeLoop(t,dt,focal,(DescribedContainer<SystemComponent>) subc);
	}

	/**
	 * Efficient (O(log(n))) search for other SystemComponents to relate to, based on space indexers (e.g.
	 * quad trees).
	 * NB. this looping method only works for SystemComponents
	 * No need to set group or lifecycle here, done in executeFunctions
	 * not recursive
	 *
	 * @param t		current time in this process Timer units
	 * @param dt	current time step in this process Timer units
	 * @param focal
	 */
	private void indexedLoop(double t, double dt,
			SystemComponent focal) {
		Iterable<SystemComponent> lsc = null;
		// Get candidates for a relation in a restricted area of space
		// search radius positive, means we only search until this distance
		if (searchRadius>space.precision())
			 lsc = space.getItemsWithin(focal,searchRadius);
		// search radius null, means we search for the nearest neighbours only
		else if (searchNeighbours>0)
			lsc = space.getNearestItems(focal);
		else
			lsc = space.getNearestItems(focal);
		// if any candidate found, proceed
		if (lsc!=null) {
			// search for items already related through this particular relation
			Collection<SystemComponent> others = focal.getOutRelatives(relContainer.type().id());
			for (SystemComponent other:lsc)
				// focal cannot relate to itself
				if (other!=focal)
					if (other.membership().belongsTo(otherCategories)) {
						// dont search if item already related ! (NB: might be more efficient with set intersection ?)
						if (!others.contains(other))
							executeFunctions(t,dt,focal,other);
						else if (relContainer.autoDelete()) {
							// this could be optimised according to relation lifespan
							// by having two lists of items in space, one for just added items,
							// one for items added for at least 1 time step
							Collection<SystemRelation> fcRelations = focal.getOutRelations(relContainer.type().id());
							// find relation between focal and other and check it
							for (SystemRelation rel:fcRelations)
								if (rel.endNode()==other) {
									executeFunctions(t,dt,focal,other,rel);
									break;
							}
						}
					}
		}
	}

	// recursive loop on all sub containers of the community - only for ephemeral relations
	@Override
	protected final void loop(double t, double dt, HierarchicalComponent component) {
		if (!relContainer.isPermanent()) {
			if (component instanceof ArenaComponent)
				arena = (ArenaComponent) component;
			// UNINDEXED SEARCH - SLOW O(n²) - maybe a warning should be issued in MM?
			if (space==null) {
				if (component.membership().belongsTo(focalCategories)) {
					if (arena.content()!=null)
						crudeLoop(t,dt,component,arena.content());
				}
				else if ((component.content()!=null) && (arena.content()!=null)) {
					if (component.content().itemCategorized()!=null)
						if (component.content().itemCategorized().belongsTo(focalCategories))
							crudeLoop(t,dt,component,arena.content());
					for (CategorizedContainer<SystemComponent> subc: component.content().subContainers())
						if (subc.itemCategorized().belongsTo(focalCategories)) {
							crudeLoop(t,dt,((DescribedContainer<SystemComponent>)subc).descriptors(),arena.content());
					}
				}
			}
			// INDEXED SEARCH - faster O(log(n))
			// NB: is an arena ever going to be contained in a space? normally no.
			else if (component==arena) {
				if (arena.content()!=null)
					for (SystemComponent focal:arena.content().allItems(focalCategories)) {
						indexedLoop(t,dt,focal);
					}
			}
		}
	}


	/**
	 * brute force inefficient (O(n²)) search for other SystemComponents to relate to.
	 * This version is only used for permanent relations.
	 * No need to set group or lifecycle here, done in executeFunctions
	 * Recursive
	 *
	 * @param focal		the SystemComponent of interest
	 * @param others	the list of candidate SystemComponents for establishing a relation to
	 */
	private void crudeLoop(SystemComponent focal,
			DescribedContainer<SystemComponent> others,
			double t,
			double dt) {
		if ((others.itemCategorized()!=null) &&
			(others.itemCategorized().belongsTo(otherCategories)) ) {
				Collection<SystemComponent> fcOthers = focal.getOutRelatives(relContainer.type().id());
				for (SystemComponent sc: others.items())
					if (focal!=sc)
						if (!fcOthers.contains(sc))
							executeFunctions(t,dt,focal,sc);
		}
		else
			for (CategorizedContainer<SystemComponent> subc: others.subContainers())
				crudeLoop(focal,(DescribedContainer<SystemComponent>) subc,t,dt);
	}

	//
	/**
	 * Establish permanent relations between SystemComponents at their creation time.
	 * Caution: the list of new SystemComponents passed as an argument may contain components
	 * of any category set, not necessarily compatible with this searchProcess (hence the late
	 * test for categories, ie at each SystemComponent level).
	 * The comm arguments contains all SystemComponents found in newComps.
	 *
	 * @param newComps	a list of newly created components
	 * @param comm	the arena container, which contains all already present components
	 *
	 */
	public void setPermanentRelations(Collection<SystemComponent> newComps,
			ComponentContainer comm,
			long time,
			long dtime) {
		if (relContainer.isPermanent()) {
			double t = timer.userTime(time);
			double dt = timer.dt(dtime);
			arena = (ArenaComponent) comm.descriptors();
			// 1. loop on new components as focals to relate them to existing components
			if (comm!=null) {
				if (space==null) { // unindexed search
					for (SystemComponent focal:newComps)
						if (focal.membership().belongsTo(focalCategories))
							crudeLoop(focal,comm,t,dt);
				}
				else { // indexed search
					for (SystemComponent focal:newComps)
						if (focal.membership().belongsTo(focalCategories))
							indexedLoop(t,dt,focal);
				}
			}
			// 2. loop on old components as focals to relate them to newly created components
			if (!newComps.isEmpty()) {
				Collection<SystemComponent> focs=null;
				if (arena.content()!=null)
					focs = arena.content().allItems(focalCategories);
				if (space==null) { // unindexed search
					for (SystemComponent focal:focs)
						for (SystemComponent other: newComps)
							if (focal!=other)
								if (other.membership().belongsTo(otherCategories))
									// NB focal cannot yet be related to other as other is new
									// but other may be in the same list as focal if they
									// belong to the same categories
									if (!((focalCategories==otherCategories) && (focs.contains(other))))
										executeFunctions(t,dt,focal,other);
				}
				else  {
					// indexed search
					for (SystemComponent focal:focs) {
						Iterable<SystemComponent> lsc;
						// search radius positive, means we only search until this distance
						if (searchRadius>space.precision())
							lsc = space.getItemsWithin(focal,searchRadius);
						// search radius null, means we search for the nearest neighbours only
						else
							lsc = space.getNearestItems(focal);
						for (SystemComponent other:lsc) {
							// only relate to if other is in the new component list
							// and is of the proper category for this relation
							if (other.membership().belongsTo(otherCategories))
								if (newComps.contains(other))
									if (other!=focal)
										if (!other.container().containsInitialItem(other))
											executeFunctions(t,dt,focal,other);
						}
					}
				}
			}
		}
	}

	// set the context information, ie group, othergroup, lifecycle, otherlifecycle
	private void setContext(SystemComponent focal, SystemComponent other) {
		HierarchicalComponent hc = focal.container().descriptors();
		focal.container().changeStructure();
		if (hc!=null) {
			if (hc instanceof GroupComponent) {
				focalGroup = hc;
				if (hc.getParent() instanceof LifeCycleComponent)
					focalLifeCycle = (LifeCycleComponent) hc.getParent();
				else
					focalLifeCycle = null;
			}
			else
				focalGroup = null;
		}
		hc = other.container().descriptors();
		other.container().changeStructure();
		if (hc!=null) {
			if (hc instanceof GroupComponent) {
				otherGroup = hc;
				if (hc.getParent() instanceof LifeCycleComponent)
					otherLifeCycle = (LifeCycleComponent) hc.getParent();
				else
					otherLifeCycle = null;
			}
			else
				otherGroup = null;
		}
	}

//	/**
//	 * For permanent and NON-AUTODELETE ephemeral relations
//	 * @param focal
//	 * @param other
//	 */
////	private void establishRelation(SystemComponent focal,SystemComponent other) {
////		relContainer.addItem(focal,other);
////	}
//
//	/**
//	 * Only for AUTODELETE EPHEMERAL relations
//	 */
////	private void deleteRelation(SystemComponent focal,
////			SystemComponent other,
////			SystemRelation rel) {
////		relContainer.removeItem(rel);
////	}


	/**
	 * Call user defined function on (focal,other); sets (focalGroup,otherGroup,focalLifeCycle,
	 * otherLifeCycle).
	 * Called by crudeLoop(...) and indexedLoop(...) and setPermanentRelations(...)
	 * Assumes that only SystemComponents can establish relations (ie no higher-level components)
	 *
	 * @param t		current time in this process Timer units
	 * @param dt	current time step in this process Timer units
	 * @param focal	the SystemComponent of interest
	 * @param other the SystemComponent candidate for establishing a relation with focal
	 */
	private void executeFunctions(double t, double dt,
			SystemComponent focal,
			SystemComponent other) {
		setContext(focal,other);
		// FLAW: this wont work if there are multiple relateTo functions in the same process
		for (RelateToDecisionFunction function: RTfunctions)
			if (function.relate(t,dt,arena,focalLifeCycle,focalGroup,focal,
				otherLifeCycle,otherGroup,other,space))
					relContainer.addItem(focal,other);
	}
	/**
	 * Only for AUTODELETE EPHEMERAL relations.
	 *
	 * @param t
	 * @param dt
	 * @param focal
	 * @param other
	 */
	private void executeFunctions(double t, double dt,
			SystemComponent focal,
			SystemComponent other,
			SystemRelation rel) {
		setContext(focal,other);
		// FLAW: this wont work if there are multiple relateTo functions in the same process
		for (RelateToDecisionFunction function: RTfunctions)
			if (!function.relate(t,dt,arena,focalLifeCycle,focalGroup,focal,
				otherLifeCycle,otherGroup,other,space))
					relContainer.removeItem(rel);
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(" functions {");
		for (TwFunction f:RTfunctions) sb.append(f.toString()).append(", ");
		if (sb.charAt(sb.length()-2)==',') {
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append('}');
		return sb.toString();
	}

}
