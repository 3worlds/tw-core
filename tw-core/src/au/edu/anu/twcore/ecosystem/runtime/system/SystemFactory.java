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

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.RelocateFunction;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.Factory;
import static au.edu.anu.twcore.ecosystem.runtime.system.SystemComponentPropertyListImpl.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Factory for system components, ie the simulated items.
 * 
 * @author Jacques Gignoux - 25 avr. 2013
 *
 */
public class SystemFactory 
		implements Factory<SystemComponent>, Categorized<SystemComponent> {
	
	// the factory for SystemComponents and SystemRelations
	private static GraphFactory SCfactory = new TwGraphFactory();
	
	private SortedSet<Category> categories = new TreeSet<>();
	private String categoryId = null;
//	private boolean sealed = false;
	private boolean permanent;
	/** TwData templates to clone to create new systems */
//	private TwData parameterTemplate = null;
	private TwData driverTemplate = null;
	private TwData decoratorTemplate = null;
	private Map<String, Integer> propertyMap = new HashMap<String, Integer>();
	private Map<DynamicSpace<SystemComponent,LocatedSystemComponent>,RelocateFunction> Rfunctions = new HashMap<>();

	/**
	 * Constructor. All arguments to constructors are cloned or copied if immutable
	 * @param par
	 * @param drv
	 * @param dec
	 * @param categories
	 * @param categoryId
	 */
	public SystemFactory(TwData drv, TwData dec, boolean perm,
			SortedSet<Category> categories, String categoryId, 
			Map<DynamicSpace<SystemComponent,LocatedSystemComponent>,RelocateFunction> spacelocators) {
		super();
//		if (par!=null)
//			parameterTemplate = par.clone();
		if (drv!=null)
			driverTemplate = drv.clone();
		if (dec!=null)
			decoratorTemplate = dec.clone();
		if (driverTemplate != null)
			for (String key : driverTemplate.getKeysAsSet())
				propertyMap.put(key, DRIVERS);
		for (String key : SystemData.keySet)
			propertyMap.put(key, AUTO);
		if (decoratorTemplate != null)
			for (String key : decoratorTemplate.getKeysAsSet())
				propertyMap.put(key, DECO);
		permanent = perm;
		this.categories.addAll(categories);
		this.categoryId = categoryId;
		for (DynamicSpace<SystemComponent,LocatedSystemComponent> sf:spacelocators.keySet()) {
			Rfunctions.put(sf,spacelocators.get(sf));
		}
	}
	
	public RelocateFunction locatorFunction(Space<SystemComponent> spaceName) {
		return Rfunctions.get(spaceName);
	}

	public Collection<DynamicSpace<SystemComponent,LocatedSystemComponent>> spaces() {
		return Rfunctions.keySet();
	}
	
	/**
	 * 
	 * @return a new SystemComponent with the proper data structure
	 */
	@Override
	public final SystemComponent newInstance() {
		SimplePropertyList props = new SystemComponentPropertyListImpl(driverTemplate,
			decoratorTemplate,2,propertyMap);
		SystemComponent result = (SystemComponent) 
			SCfactory.makeNode(SystemComponent.class,"C0",props);
		result.setCategorized(this);
		return result;
	}

//	/** returns a new parameterSet of the proper structure for this SystemFactory */
//	public final TwData newParameterSet() {
//		if (parameterTemplate != null)
//			return parameterTemplate.clone().clear();
//		else
//			return null;
//	}
//	
//	/** returns a new variableSet of the proper structure for this SystemFactory 
//	 * NB for use at initialisation only*/
//	public final TwData newVariableSet() {
//		if (driverTemplate != null)
//			return driverTemplate.clone().clear();
//		else
//			return null;
//	}

	
	@Override
	public Set<Category> categories() {
		return categories;
	}
	
	public boolean isPermanent() {
		return permanent;
	}
	
//	@Override
//	public Sealable seal() {
//		sealed = true;
//		return this;
//	}
//
//	@Override
//	public boolean isSealed() {
//		return sealed;
//	}
	
	@Override
	public String categoryId() {
		return categoryId;
	}
//	
//	public Collection<SystemContainer> containers() {
//		return containers.values();
//	}
//
//	/**
//	 * Either return container matching 'name' or create it if not yet there. This way, only
//	 * one instance of that container will exist.
//	 * 
//	 * @param name
//	 * @return
//	 */
//	public SystemContainer container(String name) {
//		return containers.get(name);
//	}

}
