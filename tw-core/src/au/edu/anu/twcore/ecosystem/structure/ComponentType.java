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
package au.edu.anu.twcore.ecosystem.structure;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.RelocateFunction;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemFactory;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.OmugiClassLoader;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.LifespanType;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.ens.biologie.generic.utils.NameUtils.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class matching the "ecosystem/structure/component" node label in the 3Worlds configuration tree.
 *  
 * @author Jacques Gignoux - 25 avr. 2013
 *
 */
public class ComponentType 
		extends InitialisableNode 
		implements LimitedEdition<SystemFactory>, Categorized<SystemComponent>, Sealable {
	
	
//	// the factory for SystemComponents and SystemRelations
//	private static GraphFactory SCfactory = null;
//	static {
//		Map<String,String> labels = new HashMap<>();
//		labels.put("component", SystemComponent.class.getName());
//		labels.put("relation", SystemRelation.class.getName());
//		SCfactory = new ALGraphFactory("3w",labels);
//	}
	
	private SortedSet<Category> categories = new TreeSet<>();
	private String categoryId = null;
	private boolean sealed = false;
	private boolean permanent;
	/** TwData templates to clone to create new systems */
	private TwData parameterTemplate = null;
	private TwData driverTemplate = null;
	private TwData decoratorTemplate = null;
	private Map<String,Constructor<? extends RelocateFunction>> fConstructors = new HashMap<>();
	private Map<String,SpaceNode> spaces = new HashMap<>();
	private Map<Integer,SystemFactory> factories = new HashMap<>();
	
	// The SystemComponent containers instantiated by this SystemFactory
	private Map<Integer,Map<String,ComponentContainer>> containers = new HashMap<>();

	// temp explore code
	public Collection<Map<String, ComponentContainer>> containers() {
		return containers.values();
	}
	public ComponentType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public ComponentType(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	// assumes user-specific classes have been generated before
	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			sealed = false;
			Collection<Category> nl = (Collection<Category>) get(edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), 
				edgeListEndNodes());
			categories.addAll(getSuperCategories(nl));
			permanent = ((LifespanType) properties().getPropertyValue(P_COMPONENT_LIFESPAN.key()))==LifespanType.permanent;
			// These ARE optional - inserted by codeGenerator!
			String s = null;
			if (properties().hasProperty(P_PARAMETERCLASS.key())) {
				s = (String) properties().getPropertyValue(P_PARAMETERCLASS.key());
				if (s!=null)
					if (!s.trim().isEmpty())
						parameterTemplate = loadDataClass(s);
			}
			if (properties().hasProperty(P_DRIVERCLASS.key())) {
				s = (String) properties().getPropertyValue(P_DRIVERCLASS.key());
				if (s!=null)
					if (!s.trim().isEmpty())
						driverTemplate = loadDataClass(s);
			}
			if (properties().hasProperty(P_DECORATORCLASS.key())) {
				s = (String) properties().getPropertyValue(P_DECORATORCLASS.key());
				if (s!=null)
					if (!s.trim().isEmpty())
						decoratorTemplate = loadDataClass(s);
			}
			// add automatically generated relocate functions
			if (properties().hasProperty(P_RELOCATEFUNCTION.key())) {
				// get all spaces
				TreeGraphNode arena = (TreeGraphNode) get(getParent(),
					children(),
					selectZeroOrOne(hasTheLabel(N_ARENA.label())));
				List<SpaceNode> sp = null;
				if (arena!=null) {
					sp = (List<SpaceNode>) get(arena.getChildren(),
						selectZeroOrMany(hasTheLabel(N_SPACE.label())));
					for (SpaceNode sn:sp)
						spaces.put(initialUpperCase(sn.id()),sn);
				}
				StringTable st = (StringTable) properties().getPropertyValue(P_RELOCATEFUNCTION.key());
				ClassLoader classLoader = OmugiClassLoader.getJarClassLoader();
				for (int i=0; i<st.size(); i++) {
					Class<? extends RelocateFunction> functionClass;
					String className = st.getWithFlatIndex(i);
					try {
						functionClass = (Class<? extends RelocateFunction>) Class.forName(className,true,classLoader);
						String spaceName = functionClass.getSimpleName()
							.substring(0,functionClass.getSimpleName().indexOf('_'));
						fConstructors.put(spaceName,functionClass.getConstructor());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
//			if (driverTemplate != null)
//				for (String key : driverTemplate.getKeysAsSet())
//					propertyMap.put(key, DRIVERS);
//			for (String key : SystemData.keySet)
//				propertyMap.put(key, AUTO);
//			if (decoratorTemplate != null)
//				for (String key : decoratorTemplate.getKeysAsSet())
//					propertyMap.put(key, DECO);
			sealed = true; // important - next statement access this class methods
			categoryId = buildCategorySignature();
		}
	}


	@Override
	public int initRank() {
		return N_COMPONENTTYPE.initRank();
	}

	/** returns a new parameterSet of the proper structure for this SystemFactory */
	public final TwData newParameterSet() {
		if (parameterTemplate != null)
			return parameterTemplate.clone().clear();
		else
			return null;
	}
	
	/** returns a new variableSet of the proper structure for this SystemFactory 
	 * NB for use at initialisation only*/
	public final TwData newVariableSet() {
		if (driverTemplate != null)
			return driverTemplate.clone().clear();
		else
			return null;
	}

	
	@Override
	public Set<Category> categories() {
		if (sealed)
			return categories;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}
	
	public boolean isPermanent() {
		if (sealed)
			return permanent;
		else
			throw new TwcoreException("attempt to access uninitialised data");
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
	public String categoryId() {
		if (sealed)
			return categoryId;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}
	
	/**
	 * Returns a new container, either nested in a group container or in the Ecosystem
	 * container, depending on what is found.
	 * 
	 * @param name
	 * @return
	 */
	public ComponentContainer makeContainer(int index,String name, ComponentContainer parent) {
		if (sealed) {
			Map<String,ComponentContainer> lsc = containers.get(index);
			if (lsc==null)
				containers.put(index,new HashMap<>());
			ComponentContainer result = containers.get(index).get(name);
			if (result==null) {
				if (parameterTemplate!=null)
					result = new ComponentContainer(getInstance(index), name, parent, parameterTemplate.clone(), null);
				else
					result = new ComponentContainer(getInstance(index), name, parent, null, null);
				if (!result.id().equals(name))
					log.warning("Unable to instantiate a container with id '"+name+"' - '"+result.id()+"' used instead");
				containers.get(index).put(result.id(),result);
			}
			return result;
		} else
			throw new TwcoreException("attempt to access uninitialised data");
	}
	
	private SystemFactory makeFactory(int index) {
		Map<DynamicSpace<SystemComponent,LocatedSystemComponent>,RelocateFunction> spaceLocators = new HashMap<>();
		if (!fConstructors.isEmpty()) {
			for (String spc:fConstructors.keySet()) {
				Constructor<? extends RelocateFunction> fc = fConstructors.get(spc);
				try {
					RelocateFunction f = fc.newInstance();
					DynamicSpace<SystemComponent,LocatedSystemComponent> sp = spaces.get(spc).getInstance(index);
					f.setRng(sp.rng());
					spaceLocators.put(sp,f);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		SystemFactory result = new SystemFactory(driverTemplate,
			decoratorTemplate,
			permanent,
			categories,
			categoryId,
			spaceLocators);
		return result;
	}

	@Override
	public SystemFactory getInstance(int id) {
		if (!sealed)
			initialise();
		if (!factories.containsKey(id))
			factories.put(id,makeFactory(id));
		return factories.get(id);
	}
	
	public Map<Integer,SystemFactory> getFactories(){
		return factories;
	}

}
