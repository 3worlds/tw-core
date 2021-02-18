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
package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * A container for SystemComponents
 *
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class ComponentContainer
		extends DescribedContainer<SystemComponent>
		implements ObservableDynamicGraph<SystemComponent,SystemRelation> {

	/** things which track changes in this container, eg spaces */
	private Set<DynamicGraphObserver<SystemComponent,SystemRelation>> observers = new HashSet<>();

	public ComponentContainer(String proposedId,
			ComponentContainer parent,
			HierarchicalComponent data,
			int simulatorId) {
		super(proposedId, parent, data, simulatorId);
	}

	@Override
	public final SystemComponent cloneItem(SystemComponent item) {
		if (item instanceof SystemComponent)
			return ((SystemComponent)item).clone();
		return null;
	}

	/**
	 * Advances state of all SystemComponents contained in this container only.
	 */
	public void step() {
		for (SystemComponent sc : items()) {
			for (DynamicGraphObserver<SystemComponent,SystemRelation> o:observers)
				// constants do not change, decorators are forgotten.
				o.onPropertiesChanged(sc.nextState());
			sc.stepForward();
		}
	}

	/**
	 * Advances state of all SystemComponents contained in this container and its
	 * sub-containers (recursive).
	 */
	public void stepAll() {
		step();
		for (CategorizedContainer<SystemComponent> sc : subContainers())
			((ComponentContainer) sc).stepAll();
	}


	@Override
	public void removeItem(SystemComponent item) {
		super.removeItem(item);
		// Is this needed at all? effectChanges will remove all the relations anyway...
//		for (SystemRelation sr:item.getRelations()) {
//			// BUG HERE: sometimes container() == null ??? in neighbourhood relation
//			// due to searchprocess
////			if (sr.container()!=null) // this fix is probably a wrong idea. IT IS!
//			sr.container().removeItem(sr);
//		}
	}

	/**
	 * clears decorators and population counters for next time step,
	 * only if was changed
	 */
	public void prepareStep() {
		HierarchicalComponent hv = descriptors();
		if (hv.decorators()!=null) {
//			hv.decorators().writeEnable();
			hv.decorators().clear();
//			hv.decorators().writeDisable();
		}
		if (changed()) {
			resetCounters();
			for (SystemComponent item:items()) {
				if (item.decorators()!=null) {
//					item.decorators().writeEnable();
					item.decorators().clear();
//					item.decorators().writeDisable();
				}
				else
					break; // since all items in a SystemContainer have the same categories
			}
		}
	}

	/**
	 * recursively clears decorators and population counters for next time step,
	 * only in sub-containers that were changed
	 */
	public void prepareStepAll() {
		prepareStep();
		for (CategorizedContainer<SystemComponent> sc : subContainers())
			((ComponentContainer) sc).prepareStepAll();
	}

	@Override
	public void rename(String oldId, String newId) {
		throw new TwcoreException("Renaming of '" + this.getClass().getSimpleName() + "' is not implemented.");
	}
	/**
	 * Recursively clears all container items and variables (if any). Used in
	 * loading new model states with ModelRunner.
	 */
	public void clearState() {
		clearState(this);
	}

	// best if static to avoid errors
	private static void clearState(CategorizedContainer<SystemComponent> parentContainer) {
		for (CategorizedContainer<SystemComponent> childContainer : parentContainer.subContainers())
			clearState(childContainer);
		for (SystemComponent item : parentContainer.items())
			parentContainer.removeItem(item);
		// effectAllChanges() is recursive so don't use here.
		parentContainer.effectChanges((Collection<SystemComponent>[])null);// counters are handled here
		parentContainer.clearVariables();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void effectChanges(Collection<SystemComponent>...changedLists) {
		resetCounters(); // Pb: values are actually delayed by 1 time step doing this ???
		for (String id : itemsToRemove) {
			SystemComponent sc = items.remove(id);
			if (sc != null) {
				((SystemComponent)sc).detachFromContainer();
				itemsToInitials.remove(id);
				for (DynamicGraphObserver<SystemComponent,SystemRelation> o:observers) {
					o.onEdgesRemoved(sc.getOutRelations());
					o.onEdgesRemoved(sc.getInRelations());
					o.onNodeRemoved(sc);
				}
				sc.disconnect();
			}
		}
		itemsToRemove.clear();
		for (SystemComponent item : itemsToAdd)
			if (items.put(item.id(), item) == null) {
				((SystemComponent)item).setContainer(this);
		}
		// this to return all newly created items
		// WHAT FOR, again ???
		if (!itemsToAdd.isEmpty())
			if (changedLists!=null)
				if (changedLists.length>0)
					changedLists[0].addAll(itemsToAdd);
		for (DynamicGraphObserver<SystemComponent,SystemRelation> o:observers)
			o.onNodesAdded(itemsToAdd);
		itemsToAdd.clear();
		super.effectChanges();
	}

	@Override
	protected void setInitialState() {
//		boolean yet = false;
		CategorizedComponent group = null;
		CategorizedComponent lifeCycle = null;
		CategorizedComponent arena = null;
		CategorizedComponent cc = descriptors();
		if (cc instanceof ArenaComponent)
			arena = cc;
		else if (cc instanceof LifeCycleComponent) {
			lifeCycle = cc;
			cc = ((DescribedContainer<?>)((LifeCycleComponent)cc).content().superContainer).descriptors();
			if (cc instanceof ArenaComponent)
				arena = cc;
			if (lifeCycle.initialiser()!=null)
				lifeCycle.initialiser().setInitialState(null, null, null, lifeCycle, null);
		}
		else if (cc instanceof GroupComponent) {
			group = cc;
			cc = ((DescribedContainer<?>)((GroupComponent)cc).content().superContainer).descriptors();
			if (cc instanceof LifeCycleComponent) {
				lifeCycle = cc;
				cc = ((DescribedContainer<?>)((LifeCycleComponent)cc).content().superContainer).descriptors();
				if (cc instanceof ArenaComponent)
					arena = cc;
			}
			else if (cc instanceof ArenaComponent) {
				arena = cc;
			}
			if (group.initialiser()!=null)
				group.initialiser().setInitialState(null, null, null, group, null);
		}
		for (SystemComponent item:items.values())
			if (item.initialiser()!=null) {
				if (item.constants()!=null)
					item.constants().writeEnable();
				if (item.currentState()!=null)
					item.currentState().writeEnable();
				item.initialiser().setInitialState(arena, lifeCycle, group, item, null);
				if (item.constants()!=null)
					item.constants().writeDisable();
				if (item.currentState()!=null)
					item.currentState().writeDisable();
//				if (!yet) {
//					item.initialiser().startEventQueues();
//					yet=true;
//				}
			}
		for (CategorizedContainer<SystemComponent> sc : subContainers())
			sc.setInitialState();
	}

	// ObservableDynamicGraph

	@Override
	public void addObserver(DynamicGraphObserver<SystemComponent, SystemRelation> listener) {
		observers.add(listener);
		for (CategorizedContainer<SystemComponent> cc:subContainers()) {
			ComponentContainer ccc = (ComponentContainer) cc;
			ccc.addObserver(listener);
		}
	}

	@Override
	public void removeObserver(DynamicGraphObserver<SystemComponent, SystemRelation> listener) {
		observers.remove(listener);
		for (CategorizedContainer<SystemComponent> cc:subContainers()) {
			ComponentContainer ccc = (ComponentContainer) cc;
			ccc.removeObserver(listener);
		}
	}

	@Override
	public Collection<DynamicGraphObserver<SystemComponent, SystemRelation>> observers() {
		return Collections.unmodifiableCollection(observers);
	}

}
