package au.edu.anu.twcore.ecosystem.runtime.system;

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
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.LocalScope;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Resettable;

/**
 * <p>The class holding the SystemComponents (actually, any object with an Id can be stored 
 * in such containers) at runtime, and responsible for their creation and deletion.</p>
 * <p>The rule is: all items (type T) contained in this class have the same data structure.
 * The data structure is defined by the categoryInfo field, which refers to a set of
 * categories which tell how data is associated to this container. These data consist in
 * constant values (parameters) and values varying with time (variables). Both are available
 * as ReadOnlyPropertyList in this class, since it's not its business to know how
 * to change the values. Items contain in this container either appear as a flat list
 * indexed by id, or in sub-containers of this same class.</p>
 * <p>This class is meant to be used at runtime in a time-synchronized way: changes (additions
 * and deletions) are only recorded, but not effected until the effectChanges() method is called.
 * </p> 
 * 
 * @author Jacques Gignoux - 1 juil. 2019
 *
 */
// Tested OK with version 0.1.3 on 1/7/2019
public abstract class CategorizedContainer<T extends Identity> 
		implements Population, Identity, Resettable, Factory<T> {

	// class-level constants
	private static final IdentityScope scope = new LocalScope("3w-runtime-container");
	private static final String COUNT = "population.size";
	private static final String NADDED = "population.births";
	private static final String NREMOVED = "population.deaths";
	private static final String TCOUNT = "total.population.size";
	private static final String TADDED = "total.population.births";
	private static final String TREMOVED = "total.population.deaths";
	private static final Set<String> props = new HashSet<String>();
	private static final PropertyKeys propsPK;
	static { 
		props.add(COUNT);	props.add(NADDED);	props.add(NREMOVED);
		props.add(TCOUNT); 	props.add(TADDED);	props.add(TREMOVED);
		propsPK = new PropertyKeys(props);
	}
			
	// unique id for this container (matches the parameter set)
	private Identity id = null;
	// category info (shared)
	private Categorized<T> categoryInfo = null;
	// parameters (unique, owned)
	private TwData parameters = null;
	// variables (unique, owned)
	private TwData variables = null;
	// items contained at this level (owned)
	private Map<String,T> items = new HashMap<>();
	// items contained at lower levels
	private Map<String,CategorizedContainer<T>> subContainers = new HashMap<>();
	// my container, if any
	private CategorizedContainer<T> superContainer = null;
	// initial state
	private Set<T> initialItems = new HashSet<>();
	// data for housework
	private Set<String> itemsToRemove = new HashSet<>();
	private Set<T> itemsToAdd = new HashSet<>();
	// Population data
	private class popData implements ReadOnlyPropertyList {
		public int count = 0;
		public int nAdded = 0;
		public int nRemoved = 0;
		@Override
		public Object getPropertyValue(String key) {
			switch (key) {
				case COUNT: 	return count;
				case NADDED: 	return nAdded;
				case NREMOVED: 	return nRemoved;
				case TCOUNT: 	return totalCount();
				case TADDED: 	return totalAdded();
				case TREMOVED: 	return totalRemoved();
			}
			return null;
		}
		@Override
		public boolean hasProperty(String key) {
			if (key.equals(COUNT) || key.equals(NADDED) || key.equals(NREMOVED))
				return true;
			return false;
		}
		@Override
		public Set<String> getKeysAsSet() {
			return props;
		}
		@Override
		public int size() {
			return 3;
		}
		@Override
		public ReadOnlyPropertyList clone() {
			SimplePropertyList pl = new SharedPropertyListImpl(propsPK);
			pl.setProperties(this);
			return pl;
		}
	}
	private popData populationData = new popData();
		
	public CategorizedContainer(Categorized<T> cats, 
			String proposedId,
			CategorizedContainer<T> parent,
			TwData parameters,
			TwData variables) {
		super();
		categoryInfo = cats;
		id = scope.newId(proposedId);
		this.parameters = parameters;
		this.variables = variables;
		if (parent!=null) {
			superContainer = parent;
			superContainer.subContainers.put(id(),this);
		}
	}
	
	// three ways to add items to the initialItems list
	public void setInitialItems(T[] items) {
		initialItems.clear();
		for (T item:items)
			initialItems.add(item);
	}
	public void setInitialItems(Collection<T> items) {
		initialItems.clear();
		initialItems.addAll(items);
	}
	public void setInitialItems(Iterable<T> items) {
		initialItems.clear();
		for (T item:items)
			initialItems.add(item);
	}

	public Categorized<T> categoryInfo() {
		return categoryInfo;
	}
	
	public TwData parameters() {
		return parameters;
	}
	
	public TwData variables() {
		return variables;
	}
	
	public ReadOnlyPropertyList populationData() {
		return populationData;
	}

	// delayed addition
	public void addItem(T item) {
		itemsToAdd.add(item);
	}

	// delayed removal
	public void removeItem(String id) {
		itemsToRemove.add(id);
	}
	
	public T item(String id) {
		return items.get(id);
	}
	
	/** gets all items contained in this container only, without those contained in sub-containers */
	public Iterable<T> items() {
		return items.values();
	}
	
	public CategorizedContainer<T> subContainer(String containerId) {
		return subContainers.get(containerId);
	}
	
	public Iterable<CategorizedContainer<T>> subContainers() {
		return subContainers.values();
	}
	
	// Recursive
	private void addItems(QuickListOfLists<T> result,CategorizedContainer<T> container) {
		result.addList(container.items());
		for (CategorizedContainer<T> sc:container.subContainers.values())
			addItems(result,sc);
	}
	
	/** gets all items contained in this container, including those contained in sub-containers */
	public Iterable<T> allItems() {
		QuickListOfLists<T> l = new QuickListOfLists<T>();
		addItems(l,this);
		return l;
	}
	
	public void effectChanges() {
		for (String id:itemsToRemove)
			if (items.remove(id)!=null) {
				populationData.count--;
				populationData.nRemoved++;
		}
		itemsToRemove.clear();
		for (T item:itemsToAdd)
			if (items.put(item.id(),item)==null) {
				populationData.count++;
				populationData.nAdded++;
		}
		itemsToAdd.clear();
	}
	
	private int totalCount(CategorizedContainer<T> container,int cumulator) {
		cumulator += container.populationData.count;
		for (CategorizedContainer<T> subc:container.subContainers.values())
			cumulator += totalCount(subc,cumulator);
		return cumulator;
	}
	/** counts the total number of items, including those of subContainers  */
	public int totalCount() {
		return totalCount(this,0);
	}
	
	private int totalAdded(CategorizedContainer<T> container,int cumulator) {
		cumulator += container.populationData.nAdded;
		for (CategorizedContainer<T> subc:container.subContainers.values())
			cumulator += totalCount(subc,cumulator);
		return cumulator;
	}
	/** counts the total number of added items, including those of subContainers  */
	public int totalAdded() {
		return totalAdded(this,0);
	}
	
	private int totalRemoved(CategorizedContainer<T> container,int cumulator) {
		cumulator += container.populationData.nRemoved;
		for (CategorizedContainer<T> subc:container.subContainers.values())
			cumulator += totalCount(subc,cumulator);
		return cumulator;
	}
	/** counts the total number of added items, including those of subContainers  */
	public int totalRemoved() {
		return totalRemoved(this,0);
	}

	// Population methods
	
	@Override
	public int count() {
		return populationData.count;
	}

	@Override
	public int nAdded() {
		return populationData.nAdded;
	}

	@Override
	public int nRemoved() {
		return populationData.nRemoved;
	}

	@Override
	public void resetCounters() {
		populationData.count = items.size();
		populationData.nAdded = 0;
		populationData.nRemoved = 0;
	}
	
	// Identity methods

	@Override
	public String id() {
		return id.id();
	}

	@Override
	public IdentityScope scope() {
		return scope;
	}

	// Resettable methods
	
	// NB: Recursive on sub-containers
	@Override
	public void reset() {
		items.clear();
		itemsToRemove.clear();
		itemsToAdd.clear();
		for (T item:initialItems) {
			T c = clone(item);
			items.put(c.id(),c);
		}
		resetCounters();
		for (CategorizedContainer<T> sc:subContainers.values())
			sc.reset();
	}
	
	// NB tow methods must be overriden in descendants: clone(item) and newInstance();
	public abstract T clone(T item);
	
}
