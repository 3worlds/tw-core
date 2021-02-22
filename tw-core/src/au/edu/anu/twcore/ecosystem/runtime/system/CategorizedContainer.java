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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.QuickListOfLists;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.containers.NestedContainer;
import au.edu.anu.twcore.ecosystem.runtime.containers.NestedDynamicContainer;
import au.edu.anu.twcore.ecosystem.runtime.containers.ResettableContainer;
import au.edu.anu.twcore.ecosystem.runtime.containers.SimpleContainer;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.impl.ResettableLocalScope;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.Sealable;

/**
 * <p>
 * The class holding the SystemComponents (actually, any object with an id
 * (class {@linkplain Identity}) can be stored in such containers) at runtime,
 * and responsible for their creation and deletion.
 * </p>
 * <p>
 * The rule is: all items (type {@code T}) contained in this class have the same
 * data structure. The data structure is defined by the categoryInfo field,
 * which refers to a set of categories which tell how data is associated to this
 * container. These data consist in constant values (parameters) and values
 * varying with time (variables). Both are available as ReadOnlyPropertyList in
 * this class, since it's not its business to know how to change the values.
 * </p>
 * <p>
 * A container can have <em>sub-containers</em>, so that they can be organized
 * in a hierarchy.
 * </p>
 * <p>
 * This class is meant to be used at runtime in a time-synchronized way: changes
 * (additions and deletions) are only recorded, but not effected until the
 * {@code effectChanges()} or {@code effectAllChanges()} method is called.
 * </p>
 * <p>
 * This class is meant to hold a population of simulated items, starting with an
 * initial population that can be <em>reset</em> later, i.e. the whole container
 * can revert to an initial state.
 * </p>
 *
 * @author Jacques Gignoux - 1 juil. 2019
 *
 */
// Tested OK with version 0.1.3 on 1/7/2019
public abstract class CategorizedContainer<T extends Identity>
		implements NestedContainer<T>, NestedDynamicContainer<T>, ResettableContainer<T>,
		SimpleContainer<T>,
		Resettable, Sealable {

	private Identity id = null;
	private String[] fullId = null;
	int depth = -1;
	private scopes scope = new scopes();

	private boolean sealed = false;

	// items contained at this level (owned)
	protected Map<String, T> items = new HashMap<>();
	// items contained at lower levels
	protected Map<String, CategorizedContainer<T>> subContainers = new HashMap<>();
	// my container, if any
	protected CategorizedContainer<T> superContainer = null;
	// initial state
	protected Set<T> initialItems = new HashSet<>();
	// a map of runtime item ids to initial items
	protected Map<String, T> itemsToInitials = new HashMap<>();
	// data for housework
	protected Set<String> itemsToRemove = new HashSet<>();
	protected Set<T> itemsToAdd = new HashSet<>();
	private boolean changed = false;
	private Categorized<T> itemCategories = null;

	public CategorizedContainer(String proposedId,
			CategorizedContainer<T> parent,
			int simulatorId) {
		super();
		scope = new scopes();
		scope.setSimId(simulatorId);
		if (scope.getContainerScope(simulatorId)==null)
			scope.setContainerScope(simulatorId, new ResettableLocalScope(containerScopeName+"-"+simulatorId));
		id = scope().newId(true,proposedId);
		if (parent != null) {
			superContainer = parent;
			superContainer.subContainers.put(id(), this);
		}
	}

	@Override
	public ResettableLocalScope scope() {
		return scope.getContainerScope(scope.getSimId());
	}

	/**
	 * CAUTION: can be set only once, ideally just after construction
	 */
	public final void setCategorized(Categorized<T> cats) {
		if (itemCategories==null)
			itemCategories = cats;
	}

	// three ways to add items to the initialItems list
	@SuppressWarnings("unchecked")
	public void setInitialItems(T... items) {
		initialItems.clear();
		for (T item : items)
			initialItems.add(item);
	}

	public void setInitialItems(Collection<T> items) {
		initialItems.clear();
		initialItems.addAll(items);
	}

	public void addInitialItem(T item) {
		initialItems.add(item);
	}

	public Set<T> getInitialItems() {
		return initialItems;
	}

//	/**
//	 * Returns the set of categories ({@linkplain Category}) associated to this
//	 * container. If this container has variables and parameters, they are specified
//	 * by these categories.
//	 *
//	 * @return the object holding all the category information
//	 */
//	public Categorized<?> containerCategorized() {
//		return avatar.membership();
//	}

	/**
	 * Returns the set of categories ({@linkplain Category}) associated to the items
	 * stored in this container.
	 *
	 * @return
	 */
	public Categorized<T> itemCategorized() {
		return itemCategories;
	}

	/**
	 * Tag an item for addition into this container's item list. The item will be
	 * effectively added only when {@code effectChanges()} or
	 * {@code effectAllChanges()} is called thereafter. This enables one to keep the
	 * container state consistent over time in discrete time simulations.
	 *
	 * @param item the item to add
	 */
	public void addItem(T item) {
		itemsToAdd.add(item);
	}

	/**
	 * Tag an item for removal from this container's item list. The item will be
	 * effectively removed only when {@code effectChanges()} or
	 * {@code effectAllChanges()} is called thereafter. This enables one to keep the
	 * container state consistent over time in discrete time simulations.
	 *
	 * @param id the id of the item to remove
	 */
	@Override
	public void removeItem(T item) {
		itemsToRemove.add(item.id());
	}


	/**
	 * Gets the item matching the id passed as argument. Only searches this
	 * container item list, not those of the sub-containers.
	 *
	 * @param id the id to search for
	 * @return the matching item, {@code null} if not found
	 */
	@Override
	public T item(String id) {
		return items.get(id);
	}

	/**
	 * Gets all items contained in this container only, without those contained in
	 * sub-containers.
	 *
	 * @return a read-only item list
	 */
	@Override
	public Collection<T> items() {
		return Collections.unmodifiableCollection(items.values());
	}

	/**
	 * Gets the sub-container matching the id passed as an argument. Only searches
	 * this container sub-container list, not those of its sub-containers.
	 *
	 * @param containerId the sub-container to search for
	 * @return the matching sub-container, {@code null} if not found
	 */
	@Override
	public CategorizedContainer<T> subContainer(String containerId) {
		return subContainers.get(containerId);
	}

	// recursive
	// TODO: test it ! seems to work
	private CategorizedContainer<T> findContainer(String containerId, CategorizedContainer<T> container) {
		CategorizedContainer<T> result = container.subContainers.get(containerId);
		if (result == null)
			for (CategorizedContainer<T> c : container.subContainers.values()) {
				result = findContainer(containerId, c);
				if (result != null)
					break;
			}
		return result;
	}

	/**
	 * Gets the sub-container matching the id passed as an argument. Searches this
	 * container whole sub-container hierarchy, ie including all its sub-containers.
	 *
	 * @param containerId the sub-container to search for
	 * @return the matching sub-container, {@code null} if not found
	 */
	@Override
	public CategorizedContainer<T> findContainer(String containerId) {
		return findContainer(containerId, this);
	}

	/**
	 * Gets all sub-containers contained in this container only, without those
	 * contained in sub-containers.
	 *
	 * @return a read-only container list
	 */
	@Override
	public Collection<CategorizedContainer<T>> subContainers() {
		return Collections.unmodifiableCollection(subContainers.values());
	}

	// Recursive
	private void addItems(QuickListOfLists<T> result, CategorizedContainer<T> container) {
		result.addList(container.items());
		for (CategorizedContainer<T> sc : container.subContainers.values())
			addItems(result, sc);
	}

	/**
	 * Gets all items contained in this container, including those contained in
	 * sub-containers. CAUTION: these items may belong to different categories, i.e.
	 * they may not store the same sets of variables/parameters.
	 *
	 * @return a read-only list of items
	 */
	@Override
	public Collection<T> allItems() {
		QuickListOfLists<T> l = new QuickListOfLists<T>();
		addItems(l, this);
		return l;
	}

	// Recursive
	private void addItems(QuickListOfLists<T> result, CategorizedContainer<T> container, Set<Category> requestedCats) {
		if (container.itemCategorized()!=null)
			if (container.itemCategorized().belongsTo(requestedCats))
				result.addList(container.items());
		for (CategorizedContainer<T> sc : container.subContainers.values())
			addItems(result, sc, requestedCats);
	}

	/**
	 * Gets all items matching a particular category signature. Searches the whole
	 * sub-container hierarchy.
	 *
	 * @param requestedCats the required categories
	 * @return a read-only list of items
	 */
	public Collection<T> allItems(Set<Category> requestedCats) {
		QuickListOfLists<T> l = new QuickListOfLists<T>();
		addItems(l, this, requestedCats);
		return l;
	}

	@Override
	public boolean contains(T item) {
		return items.values().contains(item);
	}

	@Override
	public boolean contains(String item) {
		return items.keySet().contains(item);
	}

	@Override
	public boolean containsInitialItem(T item) {
		return initialItems.contains(item);
	}

	/**
	 * Returns the initial item from which an item is a copy, null if it's not a
	 * copy.
	 *
	 * @param item
	 * @return
	 */
	@Override
	public T initialForItem(String id) {
		return itemsToInitials.get(id);
	}

	/**
	 * Effectively remove <em>and</em> add items from the container lists and from
	 * <em>all</em> its sub-containers (before a call to this method, items are just
	 * stored into {@code itemsToRemove} and {@code itemsToAdd})
	 */
	@SafeVarargs
	// Recursive
	public final void effectAllChanges(Collection<T>...changedLists) {
		effectChanges(changedLists);
		for (CategorizedContainer<T> c : subContainers())
			c.effectAllChanges(changedLists);
	}

	@Override
	public int depth() {
		if (depth==-1) {
			depth = 0;
			CategorizedContainer<?> superC = this;
			while (superC != null) {
				depth++;
				superC = superC.superContainer;
			}
		}
		return depth;
	}

	// CAUTION HERE!
//	@Override
	public void resetCounters() {
//		if (avatar.autoVar() instanceof ContainerData)
//			((ContainerData)avatar.autoVar()).resetCounters();
	}

	// NB: Recursive on sub-containers
	@Override
	public void preProcess() {
		SimpleContainer.super.preProcess();
		for (T item : initialItems) {
			T c = cloneItem(item);
			items.put(c.id(), c);
			itemsToInitials.put(c.id(), item);
		}
		for (CategorizedContainer<T> sc : subContainers.values())
			sc.preProcess();
		resetCounters();
		setInitialState();
	}

	protected void setInitialState() {
		// nothing to do here
	}

	// NB: Recursive on sub-containers
	@Override
	public void postProcess() {
		items.clear();
		itemsToRemove.clear();
		itemsToAdd.clear();
		itemsToInitials.clear();
		for (CategorizedContainer<T> sc : subContainers.values())
			sc.postProcess();
		resetCounters();
//		((ResettableLocalScope)scope()).postProcess();
		fullId = null;
		depth = -1;
		SimpleContainer.super.postProcess();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void effectChanges(Collection<T>...changedLists) {
		changed = false;
	}

//	@Override
	public void clearVariables() {
		changed = false;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append("container:");
		sb.append(id().toString());
		sb.append('[');
		if (first)
			first = false;
		else
			sb.append(' ');
		sb.append("variables:(");
		sb.append(')');
		// TODO: adapt this !
		if (!initialItems.isEmpty()) {
			sb.append(" initial_items:");
			sb.append(initialItems.toString());
		}
		if (!items.isEmpty()) {
			sb.append(" local_items:");
			sb.append(items.toString());
		}
		if (superContainer != null) {
			sb.append(" super_container:");
			sb.append(superContainer.id());
		}
		if (!subContainers.isEmpty()) {
			sb.append(" sub_containers:");
			sb.append(subContainers.keySet().toString());
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

	@Override
	public void clearItems() {
		items.clear();
		itemsToRemove.clear();
		itemsToAdd.clear();
		itemsToInitials.clear();
		resetCounters();
	}

	@Override
	public void clearAllItems() {
		clearItems();
		for (CategorizedContainer<T> sc : subContainers.values())
			sc.clearAllItems();
	}

	protected abstract T cloneItem(T item);

	@Override
	public String id() {
		return id.id();
	}


	@Override
	public String[] fullId() {
		if (fullId==null) {
			fullId = new String[depth()];
			int i = fullId.length;
			CategorizedContainer<?> superC = this;
			while (superC != null) {
				i--;
				fullId[i] = superC.id();
				superC = superC.superContainer;
			}
		}
		return fullId;
	}

	// NB: this will return a valid hierarchical id even if the item is not yet in the container
	// use with caution.
	@Override
	public String[] itemId(String itid) {
		String[] result = Arrays.copyOf(fullId(),fullId().length+1);
		result[result.length-1] = itid;
		return result;
	}

	@Override
	public CategorizedContainer<T> parentContainer() {
		return superContainer;
	}

	@Override
	public boolean changed() {
		return changed;
	}

	@Override
	public void change() {
		changed = true;
	}

}
