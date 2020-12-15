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
import java.util.TreeMap;
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
	protected static Map<Integer,GraphFactory> SCfactory = new TreeMap<>();
	protected Integer simId;

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
	protected boolean isPermanent = true;

	/**
	 * basic constructor
	 * @param categories
	 * @param categoryId
	 */
	public ElementFactory(Set<Category> categories,
			TwData auto, TwData drv, TwData dec, TwData ltc,
			SetInitialStateFunction setinit, boolean permanent,int simulatorId) {
		super();
		simId = simulatorId;
		if (SCfactory.get(simId)==null)
			SCfactory.put(simId,new TwGraphFactory(simId));
		this.categories.addAll(categories);
//		this.categoryId = categoryId;
		this.categoryId = buildCategorySignature();
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
			for (String key : ComponentData.keySet)
				propertyMap.put(key, AUTO);
		if (decoratorTemplate != null)
			for (String key : decoratorTemplate.getKeysAsSet())
				propertyMap.put(key, DECO);
		if (lifetimeConstantTemplate != null)
			for (String key : lifetimeConstantTemplate.getKeysAsSet())
				propertyMap.put(key, CONST);
		this.setinit = setinit;
		this.isPermanent = permanent;
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
	public final Set<Category> categories() {
		return categories;
	}

	@Override
	public final String categoryId() {
		return categoryId;
	}

	public final SetInitialStateFunction initialiser() {
		return setinit;
	}

	public final boolean isPermanent() {
		return isPermanent;
	}

}
