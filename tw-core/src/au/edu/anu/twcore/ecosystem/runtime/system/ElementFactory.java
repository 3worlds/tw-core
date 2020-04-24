package au.edu.anu.twcore.ecosystem.runtime.system;

import static au.edu.anu.twcore.ecosystem.runtime.system.SystemComponentPropertyListImpl.AUTO;
import static au.edu.anu.twcore.ecosystem.runtime.system.SystemComponentPropertyListImpl.CONST;
import static au.edu.anu.twcore.ecosystem.runtime.system.SystemComponentPropertyListImpl.DECO;
import static au.edu.anu.twcore.ecosystem.runtime.system.SystemComponentPropertyListImpl.DRIVERS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.GraphFactory;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Singleton;

/**
 * A factory for any system element type - ancestor to SystemFactory, LifeCycle factory, GroupFactory etc.
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public abstract class ElementFactory<T extends DataElement>
		implements Factory<T>, Categorized<T>, Singleton<T> {

	// the factory for SystemComponents and SystemRelations
	protected static GraphFactory SCfactory = new TwGraphFactory();

	/** Categorized */
	private SortedSet<Category> categories = new TreeSet<>();
	private String categoryId = null;
	private List<String> categoryNames = null;

	/** TwData templates to clone to create new systems */
	TwData autoVarTemplate = null;
	TwData driverTemplate = null;
	TwData decoratorTemplate = null;
	TwData lifetimeConstantTemplate = null;
	Map<String, Integer> propertyMap = new HashMap<String, Integer>();

	SetInitialStateFunction setinit;

	/**
	 * basic constructor
	 * @param categories
	 * @param categoryId
	 */
	// MISSING: setinitialstate function
	public ElementFactory(Set<Category> categories, String categoryId,
			TwData auto, TwData drv, TwData dec, TwData ltc,
			SetInitialStateFunction setinit) {
		super();
		this.categories.addAll(categories);
		this.categoryId = categoryId;
		categoryNames = new ArrayList<>(categories.size());
		for (Category c:categories)
			categoryNames.add(c.id()); // order is maintained
		if (auto!=null)
			autoVarTemplate = auto.clone();
		if (drv!=null)
			driverTemplate = drv.clone();
		if (dec!=null)
			decoratorTemplate = dec.clone();
		if (ltc!=null)
			lifetimeConstantTemplate = ltc.clone();
		if (driverTemplate != null)
			for (String key : driverTemplate.getKeysAsSet())
				propertyMap.put(key, DRIVERS);
		if (autoVarTemplate!=null)
			for (String key : SystemData.keySet)
				propertyMap.put(key, AUTO);
		if (decoratorTemplate != null)
			for (String key : decoratorTemplate.getKeysAsSet())
				propertyMap.put(key, DECO);
		if (lifetimeConstantTemplate != null)
			for (String key : lifetimeConstantTemplate.getKeysAsSet())
				propertyMap.put(key, CONST);
		this.setinit = setinit;
	}

	// Singleton

	@Override
	public T getInstance() {
		throw new TwcoreException("This method should never be called");
	}

	// Factory

	@Override
	public T newInstance() {
		throw new TwcoreException("This method should never be called");
	}

	// Categorized

	@Override
	public Set<Category> categories() {
		return categories;
	}

	@Override
	public String categoryId() {
		return categoryId;
	}

	public SetInitialStateFunction initialiser() {
		return setinit;
	}

}
