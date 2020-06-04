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

import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * A container for SystemComponents
 *
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class ComponentContainer extends CategorizedContainer<SystemComponent> {

	public ComponentContainer(String proposedId,
			ComponentContainer parent, HierarchicalComponent data) {
		super(proposedId, parent, data);
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
		for (SystemComponent sc : items())
			sc.stepForward();
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


	/**
	 * clears decorators and population counters for next time step,
	 * only if was changed
	 */
	public void prepareStep() {
		HierarchicalComponent hv = hierarchicalView();
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
	// TO IAN: note that the clearAllVariables() and clearAllItems() methods do the same job now
	public void clearState() {
		clearState(this);
	}

	// best if static to avoid errors
	// TO IAN: note that the clearAllVariables() and clearAllItems() methods do the same job now
	private static void clearState(CategorizedContainer<SystemComponent> parentContainer) {
		for (CategorizedContainer<SystemComponent> childContainer : parentContainer.subContainers())
			clearState(childContainer);
		for (SystemComponent item : parentContainer.items())
			parentContainer.removeItem(item);
		// effectAllChanges() is recursive so don't use here.
		parentContainer.effectChanges();// counters are handled here
		parentContainer.clearVariables();
// replaced by method below
//		if (parentContainer.variables() != null) {
//			/**
//			 * TODO not tested yet. I assume it's readOnly until executing Twfunctions. If
//			 * so replace with writeEnable()/writeDisable() without testing.
//			 */
//			boolean readOnly = parentContainer.variables().isReadOnly();
//			if (readOnly)
//				parentContainer.variables().writeEnable();
//			parentContainer.variables().clear();
//			if (readOnly)
//				parentContainer.variables().writeDisable();
//		}
//
	}

	@Override
	public void clearVariables() {
//		if (variables() != null) {
//			boolean readOnly = variables().isReadOnly();
//			if (readOnly)
//				variables().writeEnable();
//			variables().clear();
//			if (readOnly)
//				variables().writeDisable();
//		}
	}

	@Override
	public void clearAllVariables() {
		clearVariables();
		for (CategorizedContainer<SystemComponent> childContainer: subContainers())
			childContainer.clearAllVariables();
	}

	@Override
	public void effectChanges() {
		resetCounters(); // Pb: values are actually delayed by 1 time step doing this ???
		for (String id : itemsToRemove) {
			SystemComponent sc = items.remove(id);
			((SystemComponent)sc).removeFromContainer();
			if (sc != null) {
//				populationData.count--;
//				populationData.nRemoved++;
				itemsToInitials.remove(id);
				sc.disconnect();
			}
		}
		itemsToRemove.clear();
		for (SystemComponent item : itemsToAdd)
			if (items.put(item.id(), item) == null) {
//				populationData.count++;
//				populationData.nAdded++;
				((SystemComponent)item).setContainer(this);
			}
		itemsToAdd.clear();
	}

	@Override
	protected void setInitialState() {
		boolean yet = false;
		for (SystemComponent item:items.values())
			if (item.initialiser()!=null) {
				// TODO: search hierarchicalyy for the proper group information!
				if (item.constants()!=null)
					item.constants().writeEnable();
				if (item.currentState()!=null)
					item.currentState().writeEnable();
				item.initialiser().setInitialState(null, null, null, null, item, null);
				if (item.constants()!=null)
					item.constants().writeDisable();
				if (item.currentState()!=null)
					item.currentState().writeDisable();
				if (!yet) {
					item.initialiser().startEventQueues();
					yet=true;
				}
			}
		for (CategorizedContainer<SystemComponent> sc : subContainers())
			sc.setInitialState();
	}

}
