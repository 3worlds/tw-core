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
@SuppressWarnings("unused")
public class SearchProcess
		extends AbstractRelationProcess  {

	private List<RelateToDecisionFunction> RTfunctions = new LinkedList<>();
	private HierarchicalContext focalContext = new HierarchicalContext();
	private HierarchicalContext otherContext = new HierarchicalContext();
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

	private void loop(double t, double dt,
		HierarchicalComponent focal,
		CategorizedContainer<SystemComponent> others) {
		if ((others.itemCategorized()!=null) &&
			(others.itemCategorized().belongsTo(otherCategories)) ) {
				if (others.hierarchicalView() instanceof GroupComponent)
					otherGroup = others.hierarchicalView();
				// todo: life cycles
				for (SystemComponent sc: others.items())
					executeFunctions(t,dt,focal,sc);
		}
		else
			for (CategorizedContainer<SystemComponent> subc: others.subContainers())
				loop(t,dt,focal,subc);
	}

	// recursive loop on all sub containers of the community
	@Override
	protected void loop(double t, double dt, HierarchicalComponent component) {
		if (component instanceof ArenaComponent)
			arena = (ArenaComponent) component;

		// UNINDEXED SEARCH
		if (space==null) {
			if (component.membership().belongsTo(focalCategories)) {
				if (arena.content()!=null)
					loop(t,dt,component,arena.content());
			}
			else
				if ((component.content()!=null) && (arena.content()!=null))
					for (CategorizedContainer<SystemComponent> subc: component.content().subContainers())
						if (subc.itemCategorized().belongsTo(focalCategories)) {
							if (subc.hierarchicalView() instanceof GroupComponent)
								focalGroup = subc.hierarchicalView();
							// TODO: life cycles
							loop(t,dt,subc.hierarchicalView(),arena.content());
					}
		}
//		if (container.categoryInfo() instanceof Ecosystem) {
//			setContext(focalContext,container);
//			setContext(otherContext,container);
//			focalContext.ecosystemParameters = container.parameters();
//			ecosystemContainer = (ComponentContainer)container;
//		}
//		for (CategorizedContainer<SystemComponent> subc:container.subContainers()) {
//			if (subc.categoryInfo() instanceof LifeCycle) {
//				loop(subc,t,dt);
//			}
//			if (subc.categoryInfo().belongsTo(focalCategories)) {
//				if (container.categoryInfo() instanceof LifeCycle) {
//					setContext(focalContext,container);
//					focalLifeCycle = (LifeCycle) container.categoryInfo();
//					focalLifeCycleContainer = (ComponentContainer) container;
//				}
//				setContext(focalContext,subc);
//				focalGroup = (SystemFactory) subc.categoryInfo();
//				focalGroupContainer = (ComponentContainer) subc;
//			}
//			if (subc.categoryInfo().belongsTo(otherCategories)) {
//				if (container.categoryInfo() instanceof LifeCycle) {
//					setContext(otherContext,container);
//					otherLifeCycle = (LifeCycle) container.categoryInfo();
//					otherLifeCycleContainer = (ComponentContainer) container;
//				}
//				setContext(otherContext,subc);
//				otherGroup = (SystemFactory) subc.categoryInfo();
//				otherGroupContainer = (ComponentContainer) subc;
//			}
//		}
//		if ((focalGroup!=null)&&(otherGroup!=null)) {
//			focalGroupContainer.change();
//			executeFunctions(focalGroupContainer,otherGroupContainer,t,dt);
//		}
	}

	@Deprecated
	private void doRelate(double t, double dt, SystemComponent focal, SystemComponent other,
			Location focalLocation, Location otherLocation, Box limits) {
		for (RelateToDecisionFunction function: RTfunctions) {
			function.setFocalContext(focalContext);
			function.setOtherContext(otherContext);
//			if (function.relate(t,dt,focal,other,focalLocation,otherLocation)) {
//			if (function.relate(t, dt, limits,
//				focalContext.ecosystemParameters, ecosystemContainer,
//				focalContext.lifeCycleParameters, focalLifeCycleContainer,
//				focalContext.groupParameters, focalGroupContainer,
//				otherContext.groupParameters, otherGroupContainer,
//				focal.autoVar(),focal.constants(),focal.currentState(),focal.decorators(),
//				focalLocation.asPoint(),
//				other.autoVar(),other.constants(),other.currentState(),other.decorators(),
//				otherLocation.asPoint())) {
//				relContainer.addItem(focal,other);
//			}
		}
	}

	private void executeFunctions(double t, double dt,
		CategorizedComponent focal, CategorizedComponent other) {
		for (RelateToDecisionFunction function: RTfunctions) {
			double[] focalLoc = null;
			double[] otherLoc = null;
			if (function.relate(t,dt,arena,focalLifeCycle,focalGroup,/*space*/ null,focal,
				otherLifeCycle,otherGroup,other,focalLoc,otherLoc))
				relContainer.addItem(focal,other);
		}
	}

	@Deprecated // but useful code in there, dont erase before spaces are ack !
	private void executeFunctions(CategorizedContainer<SystemComponent> focalContainer,
			CategorizedContainer<SystemComponent> otherContainer,
			double t, double dt) {
		for (SystemComponent focal:focalContainer.items()) {
			// brute force approach - SLOW O(n²) - maybe a warning should be issued in MM
			if (space==null) {
				for (SystemComponent other:otherContainer.items())
					if (other!=focal)
						doRelate(t,dt,focal,other,null,null,null);
			}
			// optimised approach using space indexers
			else {
				// search radius positive, means we only search until this distance
				if (searchRadius>space.precision()) {
					// dont search if item already related !
					Iterable<SystemComponent> lsc = space.getItemsWithin(focal,searchRadius);
					if (lsc!=null)
						for (SystemComponent other:lsc) {
							Location focalLoc = space.locationOf(focal);
							// focal cannot relate to itself
							if (other!=focal)
								// do no check already related components [should be done before]
								if (!focal.getRelatives(relContainer.type().id()).contains(other))
									if (other.membership().belongsTo(otherCategories))
										if (!otherContainer.containsInitialItem(other))
											doRelate(t,dt,focal,other,
												focalLoc,space.locationOf(other),space.boundingBox());
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
									if (other.membership().belongsTo(otherCategories))
										if (!otherContainer.containsInitialItem(other))
											doRelate(t,dt,focal,other,
												focalLoc,
												space.locationOf(other),
												space.boundingBox());
					}
				}
			}
		}
	}

}
