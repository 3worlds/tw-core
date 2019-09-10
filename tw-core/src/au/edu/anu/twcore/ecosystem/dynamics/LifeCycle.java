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
package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.utils.Logging;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_PARAMETERCLASS;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * Class matching the "ecosystem/dynamics/lifeCycle" node label in the 
 * 3Worlds configuration tree. Has no properties.
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class LifeCycle 
		extends InitialisableNode 
		implements Categorized<SystemComponent>, Sealable {

	private static Logger log = Logging.getLogger(LifeCycle.class);
	
	private boolean sealed = false;
	private SortedSet<Category> categories = new TreeSet<>();
	private String categoryId = null;
	
	private TwData parameterTemplate = null;
	
	// the map of category recruitment transitions - key is the 'from' category, value is the 'to' cat.
	private Map<Category,Category> recruit = new HashMap<Category,Category>();
	// the map of category recruitment transitions - key is the 'from' category, value is the 'to' cat.
	private Map<Category,Category> produce = new HashMap<Category,Category>();
	
	// The SystemComponent containers instantiated by this LifeCycle
	private Map<String,SystemContainer> containers = new HashMap<String,SystemContainer>();
	
	// default constructor
	public LifeCycle(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public LifeCycle(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		// manage categories
		Collection<Category> nl = (Collection<Category>) get(edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), 
			edgeListEndNodes());
		categories.addAll(getSuperCategories(nl));
		// check if user-defined data classes were generated
		if (properties().hasProperty(P_PARAMETERCLASS.key())) {
			String s = (String) properties().getPropertyValue(P_PARAMETERCLASS.key());
			if (s!=null)
				if (!s.trim().isEmpty())
					parameterTemplate = loadDataClass(s);
		}
		// build the recruitment table
		Collection<Recruit> recs = (Collection<Recruit>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_RECRUIT.label())));
		for (Recruit r:recs) {
			Category toc = (Category) get(r.edges(Direction.OUT),
				selectOne(hasTheLabel(E_TOCATEGORY.label())),
				endNode());
			Category froc = (Category) get(r.edges(Direction.OUT),
				selectOne(hasTheLabel(E_FROMCATEGORY.label())),
				endNode());
			recruit.put(froc,toc);
		}
		// build the reproduction table
		Collection<Produce> prods = (Collection<Produce>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_PRODUCE.label())));
		for (Produce r:prods) {
			Category toc = (Category) get(r.edges(Direction.OUT),
				selectOne(hasTheLabel(E_TOCATEGORY.label())),
				endNode());
			Category froc = (Category) get(r.edges(Direction.OUT),
				selectOne(hasTheLabel(E_FROMCATEGORY.label())),
				endNode());
			produce.put(froc,toc);
		}
		sealed = true; // important - next statement access this class methods
		categoryId = buildCategorySignature();
	}

	@Override
	public int initRank() {
		return N_LIFECYCLE.initRank();
	}
	
	/**
	 * returns the categoryset in which an object recruits according to this lifecycle
	 * 
	 * @param from
	 * @return
	 */
	public Set<Category> recruitTo(Categorized<?> from) {
		Category tocat = null;
		// search if one of the categories of the argument
		// matches one of the 'from' categories of the recruit table
		for (Category c:from.categories()) {
			tocat = recruit.get(c);
			// if found, construct a category set with c replaced by tocat and all the categories
			// already present in the argument (we assume the life cycle only changes one category)
			if (tocat!=null) {
				Set<Category> result = new HashSet<Category>(from.categories());
				result.remove(c);
				result.add(tocat);
				return result;
			}
		}
		return null; 
	}

	/**
	 * returns the categoryset which an object produces as offspring according to this lifecycle
	 * 
	 * @param from
	 * @return
	 */
	public Set<Category> produceTo(Categorized<?> from) {
		Category tocat = null;
		// search if one of the categories of the argument
		// matches one of the 'from' categories of the produce table
		for (Category c:from.categories()) {
			tocat = produce.get(c);
			// if found, construct a category set with c replaced by tocat and all the categories
			// already present in the argument (we assume the life cycle only changes one category)
			if (tocat!=null) {
				Set<Category> result = new HashSet<Category>(from.categories());
				result.remove(c);
				result.add(tocat);
				return result;
			}
		}
		return null; 
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
	public Set<Category> categories() {
		if (sealed)
			return categories;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	@Override
	public String categoryId() {
		if (sealed)
			return categoryId;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	public Collection<SystemContainer> containers() {
		if (sealed)
			return containers.values();
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	/**
	 * returns the container matching the name.
	 * 
	 * @param name
	 * @return
	 */
	public SystemContainer container(String name) {
		if (sealed)
			return containers.get(name);
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}
	
	/**
	 * Either return container matching 'name' or create it if not yet there. This way, only
	 * one instance of that container will exist.
	 * will only make a container if it does not yet exist under that name */
	public SystemContainer makeContainer(String name) {
		if (sealed) {
			SystemContainer result = containers.get(name);
			if (result==null) {
				SystemContainer sc = ((Ecosystem)getParent().getParent()).getInstance();
				result = new SystemContainer(this, name, sc, 
					parameterTemplate.clone(), null);
				if (!result.id().equals(name))
					log.warning("Unable to instantiate a container with id '"+name+"' - '"+result.id()+"' used instead");
				containers.put(result.id(),result);
			}
			return result;
		} else
			throw new TwcoreException("attempt to access uninitialised data");
	}
}
