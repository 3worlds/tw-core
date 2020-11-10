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

//import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.RelateToDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.space.Location;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.DescribedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;

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

// TODO: a setInitialRelation function for permanent components with permanent relations
@SuppressWarnings("unused")
public class SearchProcess
		extends AbstractRelationProcess  {

	private List<RelateToDecisionFunction> RTfunctions = new LinkedList<>();
	private ComponentContainer ecosystemContainer = null;

	private ComponentContainer focalLifeCycleContainer = null;
	private ComponentContainer focalGroupContainer = null;
	private ComponentContainer otherLifeCycleContainer = null;
	private ComponentContainer otherGroupContainer = null;
	// new API
	private ArenaComponent arena = null;
	private CategorizedComponent focalLifeCycle = null;
	private CategorizedComponent otherLifeCycle = null;
	private CategorizedComponent focalGroup = null;
	private CategorizedComponent otherGroup = null;

	public SearchProcess(ArenaComponent world, RelationContainer relation,
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

	private void crudeLoop(double t, double dt,
		HierarchicalComponent focal,
		DescribedContainer<SystemComponent> others) {
//		if (!relContainer.isPermanent()) {
			if ((others.itemCategorized()!=null) &&
				(others.itemCategorized().belongsTo(otherCategories)) ) {
					if (others.descriptors() instanceof GroupComponent)
						otherGroup = others.descriptors();
					// todo: life cycles
					for (SystemComponent fc:focal.content().items())
						for (SystemComponent sc: others.items())
							executeFunctions(t,dt,fc,sc);
			}
			else
				for (CategorizedContainer<SystemComponent> subc: others.subContainers())
					crudeLoop(t,dt,focal,(DescribedContainer<SystemComponent>) subc);
//		}
	}

	// this looping method only works on SystemComponents
	// TODO: find life cycle!
	private void indexedLoop(double t, double dt,
			SystemComponent focal) {
//		if (!relContainer.isPermanent()) {
		// search radius positive, means we only search until this distance
		if (searchRadius>space.precision()) {
			// dont search if item already related !
			Iterable<SystemComponent> lsc = space.getItemsWithin(focal,searchRadius);
			// in all cases: do not add already related stuff. how?
			// if ephemeral: relative may have moved
			// if permanent: only remove relative when gone from
			if (lsc!=null) {
				Location focalLoc = space.locationOf(focal);
				for (SystemComponent other:lsc) {
					// focal cannot relate to itself
					if (other!=focal)
						// do no check already related components [should be done before]
						// this could be optimised according to relation lifespan
						// by having two lists of items in space, one for just added items,
						// one for items added for at least 1 time step
						if (!focal.getRelatives(relContainer.type().id()).contains(other)) {
							if (!other.container().containsInitialItem(other))
								if (other.membership().belongsTo(otherCategories)) // slow? check this.
									executeFunctions(t,dt,focal,other);
						}
				}
			}
		}
		// search radius null, means we search for the nearest neighbours only
		else {
			Iterable<SystemComponent> lsc = space.getNearestItems(focal);
			if (lsc!=null)
				for (SystemComponent other:lsc) {
					Location focalLoc = space.locationOf(focal);
					if (other!=focal)
						if (!focal.getRelatives(relContainer.type().id()).contains(other))
							if (!other.container().containsInitialItem(other))
								if (other.membership().belongsTo(otherCategories))
									executeFunctions(t,dt,focal,other);
			}
		}
//		}
	}

	// recursive loop on all sub containers of the community - only for ephemeral relations
	@Override
	protected void loop(double t, double dt, HierarchicalComponent component) {
		if (!relContainer.isPermanent()) {
			if (component instanceof ArenaComponent)
				arena = (ArenaComponent) component;
			// UNINDEXED SEARCH - SLOW O(n²) - maybe a warning should be issued in MM?
			if (space==null) {
				if (component.membership().belongsTo(focalCategories)) {
					if (arena.content()!=null)
						crudeLoop(t,dt,component,arena.content());
				}
				else
					if ((component.content()!=null) && (arena.content()!=null)) {
						if (component.content().itemCategorized()!=null)
							if (component.content().itemCategorized().belongsTo(focalCategories))
								crudeLoop(t,dt,component,arena.content());
						for (CategorizedContainer<SystemComponent> subc: component.content().subContainers())
							if (subc.itemCategorized().belongsTo(focalCategories)) {
								if (((DescribedContainer<SystemComponent>)subc).descriptors() instanceof GroupComponent)
									focalGroup = ((DescribedContainer<SystemComponent>)subc).descriptors();
								// TODO: life cycles
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


//	// for permanent relations only
	private void crudeLoop(SystemComponent focal,
		DescribedContainer<SystemComponent> others) {
		if (focal.membership().belongsTo(focalCategories))
			if ((others.itemCategorized()!=null) &&
				(others.itemCategorized().belongsTo(otherCategories)) ) {
					if (others.descriptors() instanceof GroupComponent)
						otherGroup = others.descriptors();
				// todo: life cycles
					for (SystemComponent sc: others.items())
						if (focal!=sc)
							executeFunctions(0.0,0.0,focal,sc);
		}
		else
			for (CategorizedContainer<SystemComponent> subc: others.subContainers())
				crudeLoop(focal,(DescribedContainer<SystemComponent>) subc);
	}

	// establish permanent relations at systemComponent creation time
	public void setPermanentRelations(Collection<SystemComponent> comps,ComponentContainer comm) {
		if (relContainer.isPermanent()) {
			arena = (ArenaComponent) comm.descriptors();
			if (space==null) {
				// unindexed search
				if (comm!=null)
					for (SystemComponent focal:comps)
						crudeLoop(focal,comm);
			}
			else {
				// indexed search
				if (comm!=null)
					for (SystemComponent focal:comps)
						indexedLoop(0.0,0.0,focal);
			}
		}
	}

	private void executeFunctions(double t, double dt, SystemComponent focal, SystemComponent other) {
		// group container
		HierarchicalComponent hc = focal.container().descriptors();
		focal.container().change();
		if (hc!=null)
			if (hc instanceof GroupComponent)
				focalGroup = hc;
		hc = other.container().descriptors();
		other.container().change();
		if (hc!=null)
			if (hc instanceof GroupComponent)
				otherGroup = hc;
		// TODO: lifeCycle
		for (RelateToDecisionFunction function: RTfunctions) {
			double[] focalLoc = null;
			double[] otherLoc = null;
			if (focal.getRelatives(relContainer.id()).contains(other))
				;
			else
			if (function.relate(t,dt,arena,focalLifeCycle,focalGroup,focal,
				otherLifeCycle,otherGroup,other,space)) {
				relContainer.addItem(focal,other);
				if (space!=null)
					if (space.dataTracker()!=null)
						space.dataTracker().createLine(focal.container().itemId(focal.id()),
							other.container().itemId(other.id()));
			}
		}
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
