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
package au.edu.anu.twcore.ecosystem.runtime.containers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.QuickListOfLists;
import au.edu.anu.rscs.aot.graph.property.PropertyKeys;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Population;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.Sealable;
import static fr.cnrs.iees.twcore.constants.PopulationVariables.*;

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
		extends AbstractPopulationContainer<T>
		implements NestedContainer<T>, NestedDynamicContainer<T>, ResettableContainer<T>, 
			StateContainer, Resettable, Sealable {
	
	static {
		props.add(TCOUNT.shortName());
		props.add(TNADDED.shortName());
		props.add(TNREMOVED.shortName());
		propsPK = new PropertyKeys(props);
	}

	private boolean sealed = false;
	// category info (shared)
	private Categorized<T> categoryInfo = null;
	// parameters (unique, owned)
	private TwData parameters = null;
	// variables (unique, owned)
	private TwData variables = null;
	// items contained at this level (owned)
	protected Map<String, T> items = new HashMap<>();
	// items contained at lower levels
	private Map<String, CategorizedContainer<T>> subContainers = new HashMap<>();
	// my container, if any
	private CategorizedContainer<T> superContainer = null;
	// initial state
	protected Set<T> initialItems = new HashSet<>();
	// a map of runtime item ids to initial items
	protected Map<String, T> itemsToInitials = new HashMap<>();
	// data for housework
	protected Set<String> itemsToRemove = new HashSet<>();
	protected Set<T> itemsToAdd = new HashSet<>();

	// Population data
	private class popData2 extends popData {
		@Override
		public Object getPropertyValue(String key) {
			Object result = super.getPropertyValue(key);
			if (result==null) {
				if (key.equals(TCOUNT.shortName()))
					return totalCount();
				else if (key.equals(TNADDED.shortName()))
					return totalAdded();
				else if (key.equals(TNREMOVED.shortName()))
					return totalRemoved();
			}
			return result;
		}
		@Override
		public ReadOnlyPropertyList clone() {
			SimplePropertyList pl = new SharedPropertyListImpl(propsPK);
			pl.setProperties(this);
			return pl;
		}
	}

	public CategorizedContainer(Categorized<T> cats, String proposedId, CategorizedContainer<T> parent,
			TwData parameters, TwData variables) {
		super(proposedId);
		populationData = new popData2();
		categoryInfo = cats;
		this.parameters = parameters;
		this.variables = variables;
		if (parent != null) {
			superContainer = parent;
			superContainer.subContainers.put(id(), this);
		}
	}

	// four ways to add items to the initialItems list
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

	public void setInitialItems(Iterable<T> items) {
		initialItems.clear();
		for (T item : items)
			initialItems.add(item);
	}

	public void addInitialItem(T item) {
		initialItems.add(item);
	}

	public Set<T> getInitialItems() {
		return initialItems;
	}

	/**
	 * Returns the set of categories ({@linkplain Category}) associated to this
	 * container. If this container has variables and parameters, they are specified
	 * by these categories.
	 * 
	 * @return the object holding all the category information
	 */
	public Categorized<T> categoryInfo() {
		return categoryInfo;
	}

	/**
	 * Returns the parameter set associated to this container. It is specified by
	 * the categories associated to the container, accessible through the
	 * {@code categoryInfo()} method.
	 * 
	 * @return the parameter set - may be {@code null}
	 */
	public TwData parameters() {
		return parameters;
	}

	/**
	 * Returns the variables associated to this container. It is specified by the
	 * categories associated to the container, accessible through the
	 * {@code categoryInfo()} method.
	 * 
	 * @return the variables - may be {@code null}
	 */
	public TwData variables() {
		return variables;
	}

	/**
	 * Returns the {@linkplain Population} data associated to this container.
	 * Population data are automatic variables added to any container (they include
	 * such things as number of items, number of newly created and deleted items).
	 * Population data are computed internally depending on the dynamics of the
	 * items stored in the container.
	 * 
	 * @return the population data as a read-only property list
	 */
	public ReadOnlyPropertyList populationData() {
		return populationData;
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
	public Iterable<T> items() {
		return items.values();
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
	public Iterable<CategorizedContainer<T>> subContainers() {
		return subContainers.values();
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
	public Iterable<T> allItems() {
		QuickListOfLists<T> l = new QuickListOfLists<T>();
		addItems(l, this);
		return l;
	}

	// Recursive
	private void addItems(QuickListOfLists<T> result, CategorizedContainer<T> container, Set<Category> requestedCats) {
		if (container.categoryInfo().belongsTo(requestedCats))
			result.addList(container.items());
		for (CategorizedContainer<T> sc : container.subContainers.values())
			addItems(result, sc);
	}

	/**
	 * Gets all items matching a particular category signature. Searches the whole
	 * sub-container hierarchy.
	 * 
	 * @param requestedCats the required categories
	 * @return a read-only list of items
	 */
	public Iterable<T> allItems(Set<Category> requestedCats) {
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
	// Recursive
	public void effectAllChanges() {
		effectChanges();
		for (CategorizedContainer<T> c : subContainers())
			c.effectAllChanges();
	}

	private int totalCount(CategorizedContainer<T> container, int cumulator) {
		cumulator += container.populationData.count;
		for (CategorizedContainer<T> subc : container.subContainers.values())
			cumulator += totalCount(subc, cumulator);
		return cumulator;
	}

	/** counts the total number of items, including those of subContainers */
	@Override
	public int totalCount() {
		return totalCount(this, 0);
	}

	private int totalAdded(CategorizedContainer<T> container, int cumulator) {
		cumulator += container.populationData.nAdded;
		for (CategorizedContainer<T> subc : container.subContainers.values())
			cumulator += totalCount(subc, cumulator);
		return cumulator;
	}

	/** counts the total number of added items, including those of subContainers */
	@Override
	public int totalAdded() {
		return totalAdded(this, 0);
	}

	private int totalRemoved(CategorizedContainer<T> container, int cumulator) {
		cumulator += container.populationData.nRemoved;
		for (CategorizedContainer<T> subc : container.subContainers.values())
			cumulator += totalCount(subc, cumulator);
		return cumulator;
	}

	/** counts the total number of added items, including those of subContainers */
	@Override
	public int totalRemoved() {
		return totalRemoved(this, 0);
	}

	@Override
	public int depth() {
		int result = 0;
		CategorizedContainer<?> superC = this;
		while (superC != null) {
			result++;
			superC = superC.superContainer;
		}
		return result;
	}


	@Override
	public void resetCounters() {
		populationData.count = items.size();
		populationData.nAdded = 0;
		populationData.nRemoved = 0;
	}

	// Resettable methods

	// NB: Recursive on sub-containers
	@Override
	public void reset() {
		items.clear();
		itemsToRemove.clear();
		itemsToAdd.clear();
		itemsToInitials.clear();
		for (T item : initialItems) {
			T c = cloneItem(item); // Pb! coordinates - how to get the spaces from here ?
			items.put(c.id(), c);
			itemsToInitials.put(c.id(), item);
		}
		resetCounters();
		for (CategorizedContainer<T> sc : subContainers.values())
			sc.reset();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append("container:");
		sb.append(id().toString());
		sb.append('[');
		if (categoryInfo.categories() != null) {
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append("categories:");
			sb.append(categoryInfo.categories().toString());
		}
		if (parameters != null) {
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append("parameters:(");
			sb.append(parameters.toString());
			sb.append(')');
		}
		if (first)
			first = false;
		else
			sb.append(' ');
		sb.append("variables:(");
		sb.append(populationData.toString());
		sb.append(')');
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

	// NB two methods must be overridden in descendants: clone(item) and
	// newInstance();
	protected abstract T cloneItem(T item);

}