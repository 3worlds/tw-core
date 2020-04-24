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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * <p>Class matching the "ecosystem/dynamics/lifeCycle" node label in the
 * 3Worlds configuration tree. Has no properties.</p>
 *
 * <p>A life cycle is a graph representing two kinds of links between life <em>stages</em>, i.e.
 * discrete states that characterize an item with a definite life span:</p>
 * <dl>
 * <dt>recruitment</dt>
 * <dd>items of a stage transform into another stage. This is specified using {@linkplain Recruit}
 * nodes in the 3worlds specification.</dd>
 * <dt>reproduction</dt>
 * <dd>items of a stage produce new items of another stage. This is specified using {@linkplain Produce}
 * nodes in the 3worlds specification.</dd>
 * </dl>
 *
 * <p>A {@code LifeCycle} is attached to a single {@linkplain CategorySet} and must use all different
 * categories appearing in this set in its {@linkplain Recruit} and {@linkplain Produce} child
 * nodes.
 * Stages are each characterized by a single {@linkplain Category} matching a particular
 * {@linkplain SystemComponent} group, and a set of variables and parameters passed at model
 * initialisation.</p>
 *
 * <p>Some rules apply to build a life cycle:</p>
 * <ul>
 * <li>By definition, it is nonsense to recruit to a stage of the same set of categories.</li>
 * <li>A single stage can recruit to more than one stage, and can produce items of more than
 * one stage.</li>
 * <li>If no life cycle is defined, it is assumed that there is no recruitment in the model,
 * and that reproduction produces items of the same stage of the producing item.</li>
 * <li>Recruitment and Produce targets must belong to different categories.</li>
 * </ul>
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
	private Map<Integer,Map<String,ComponentContainer>> containers = new HashMap<>();

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
			selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())),
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
	 * This method returns the list of category signatures of all targets of a recruit
	 * edge starting from the item passed as argument
	 *
	 * @param from
	 * @return
	 */
	public List<String> recruitTo(Categorized<?> from) {
		List<String> catSignatures = new ArrayList<>();
		Category tocat = null;
		// search if one of the categories of the argument
		// matches one of the 'from' categories of the recruit table
		for (Category c:from.categories()) {
			tocat = recruit.get(c);
			if (tocat!=null) {
				catSignatures.add(tocat.id());
//				Set<Category> result = new HashSet<Category>(from.categories());
//				result.remove(c);
//				result.add(tocat);
//				return result;
			}
		}
		return catSignatures;
	}

	/**
	 * This method returns the list of category signatures of all targets of a produce
	 * edge starting from the item passed as argument
	 *
	 * @param from
	 * @return
	 */
	public List<String> produceTo(Categorized<?> from) {
		List<String> catSignatures = new ArrayList<>();
		Category tocat = null;
		// search if one of the categories of the argument
		// matches one of the 'from' categories of the produce table
		for (Category c:from.categories()) {
			tocat = produce.get(c);
			// if found, construct a category set with c replaced by tocat and all the categories
			// already present in the argument (we assume the life cycle only changes one category)
			if (tocat!=null) {
				catSignatures.add(tocat.id());
//				Set<Category> result = new HashSet<Category>(from.categories());
//				result.remove(c);
//				result.add(tocat);
//				return result;
			}
		}
		return catSignatures;
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

	public Collection<ComponentContainer> containers(int simId) {
		if (sealed)
			return containers.get(simId).values();
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	/**
	 * returns the container matching the name.
	 *
	 * @param name
	 * @return
	 */
	public ComponentContainer container(int simId, String name) {
		if (sealed)
			return containers.get(simId).get(name);
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	/**
	 * Either return container matching 'name' or create it if not yet there. This way, only
	 * one instance of that container will exist.
	 * will only make a container if it does not yet exist under that name.
	 *
	 * @param simId the simulation id
	 * @param name the name of the container
	 * @return the container
	 */
	public ComponentContainer makeContainer(int simId, String name, ComponentContainer parent) {
		if (!sealed)
			initialise();
		Map<String,ComponentContainer> lsc = containers.get(simId);
		if (lsc==null)
			containers.put(simId,new HashMap<String,ComponentContainer>());
		ComponentContainer result = containers.get(simId).get(name);
		if (result==null) {
// CODE BROKEN HERE

//			if (parameterTemplate!=null)
//				result = new ComponentContainer(this,name,parent,parameterTemplate.clone(),null);
//			else
//				result = new ComponentContainer(this,name,parent,null,null);
//			if (!result.id().equals(name))
//				log.warning("Unable to instantiate a container with id '"+name+"' - '"+result.id()+"' used instead");
//			containers.get(simId).put(result.id(),result);
		}
		return result;
	}
}
