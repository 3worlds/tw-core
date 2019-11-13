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
package au.edu.anu.twcore.ecosystem;

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_BELONGSTO;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_INSTANCEOF;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_PARAMETERCLASS;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Component;
import au.edu.anu.twcore.ecosystem.dynamics.initial.InitialState;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;

/**
 * Class matching the "ecosystem" node label in the 3Worlds configuration tree.
 * Has properties. Also, produces the singleton top-container for the community of
 * SystemComponents which constitute this ecosystem.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Ecosystem 
		extends InitialisableNode 
		implements Categorized<SystemComponent>, LimitedEdition<SystemContainer>, Sealable {

	// this is the top of the system, so it doesnt belong to any category	
	// except if we want to attach parameters/variables to it
	private boolean sealed = false;
	// a 'null' category in case no category is set by the user
	private static final String rootCategoryId = ".";
	// a set of categories in case the user set some
	private String categoryId = null;
	private Set<Category> categories = new TreeSet<Category>(); 
	private TwData parameters = null;
	
	private Map<Integer,SystemContainer> communities = new HashMap<>();

	public Ecosystem(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public Ecosystem(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	// RULES HERE:
	// a CategorizedContainer MUST store items which match its categories, so:
	// 1 if an Ecosystem has been specified with categories through belongsTo edges,
	// then it cannot contain SystemComponents in its item list (cf. InitialState)
	// 2 if InitialState has Individual as direct children, then the Ecosystem
	// categories MUST be set to those of these children. Of course the children must
	// all have the same categories.
	// TODO: implement queries to check these constraints
	public void initialise() {
		if (!sealed) {
			super.initialise();
			// case 1: categories have been attached to the ecosystem - they will be used
			// to set its variables, it cannot have any initial item
			Collection<Category> cats = (Collection<Category>) get(edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), 
				edgeListEndNodes());
			if (!cats.isEmpty()) {
				categories.addAll(getSuperCategories(cats));
				categoryId = buildCategorySignature();
			}
			else {
				InitialState is = (InitialState) get(getChildren(),
					selectOne(hasTheLabel(N_DYNAMICS.label())),
					children(),
					selectZeroOrOne(hasTheLabel(N_INITIALSTATE.label())));
				List<Component> il = (List<Component>) get(is.getChildren(),
					selectZeroOrMany(hasTheLabel(N_COMPONENT.label())));
				// case 2: no categories attached to the ecosystem and no individuals initialised
				// means the ecosystem has no variables, no parameters, no items.
				if (il.isEmpty())
					categoryId = rootCategoryId;
				// case 3: initial individuals have been specified, the ecosystem
				// categories are set to those of the first individual in the list				
				else {
					Component i = il.get(0);
					ComponentType scn = (ComponentType) get(i.edges(Direction.OUT),
						selectOne(hasTheLabel(E_INSTANCEOF.label())),
						endNode());
					Collection<Category> nl = (Collection<Category>) get(scn.edges(Direction.OUT),
						selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), 
						edgeListEndNodes());
					categories.addAll(getSuperCategories(nl));
					categoryId = buildCategorySignature();
				}
			}
			if (properties().hasProperty(P_PARAMETERCLASS.key())) {
				String s = (String) properties().getPropertyValue(P_PARAMETERCLASS.key());
				if (s!=null)
					if (!s.trim().isEmpty())
						parameters = loadDataClass(s);
			}
			// TODO: automatic variables as variableTemplate
			// means data code generation must work for ecosystem too
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_SYSTEM.initRank();
	}
	
	@Override
	public Set<Category> categories() {
		if (!categories.isEmpty())
			return categories;
		return null;
	}

	@Override
	public String categoryId() {
		return categoryId;
	}
	
	private SystemContainer makeCommunity() {
		SystemContainer community = null;
		if (parameters!=null)
			community = new SystemContainer(this,"ecosystem",null,parameters.clone(),null);
		else
			community = new SystemContainer(this,"ecosystem",null,null,null);
		return community;
	}

	@Override
	public SystemContainer getInstance(int index) {
		if (!sealed)
			initialise();
		if (!communities.containsKey(index))
			communities.put(index, makeCommunity());
		return communities.get(index);
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

}
