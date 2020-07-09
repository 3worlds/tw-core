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
package au.edu.anu.twcore.ecosystem.dynamics.initial;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.dynamics.LocationEdge;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.space.Location;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupFactory;
import au.edu.anu.twcore.ecosystem.structure.newapi.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

/**
 *
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class Component
		extends InitialisableNode
		implements Sealable, LimitedEdition<List<SystemComponent>>, DefaultStrings {

	private boolean sealed = false;
//	private TwData variables = null;
	private ComponentType componentFactory = null;
	// This is FLAWED: assumes only ONE component per simulator ???, no, its fine, different components
	// have different Component nodes
	private Map<Integer,List<SystemComponent>> individuals = new HashMap<>();

	private Map<SpaceNode,double[]> coordinates = new HashMap<>();
	// the container in which new components should be placeds
	private ArenaType arena = null;
	// the group instance this component is instance of
	private Group group = null;
	// number of component types declared
	// if only one, then components can be stored in the arena
	// if >1, then even if no groups are declared, components must be stored in separate group
	// containers matching category signatures
	private int nComponentTypes = 0;
	private int nInstances = 1;

	// default constructor
	public Component(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Component(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		if (properties().hasProperty(P_COMPONENT_NINST.key()))
			nInstances = (int) properties().getPropertyValue(P_COMPONENT_NINST.key());
		nComponentTypes = ((Collection<?>)get(getParent().getParent().getChildren(),
			selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())))).size();
		componentFactory = (ComponentType) getParent();
		List<LocationEdge> spaces = (List<LocationEdge>) get(edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_LOCATION.label())));
		for (LocationEdge spe:spaces) {
			SpaceNode space = (SpaceNode) spe.endNode();
			DoubleTable tab = (DoubleTable) spe.properties().getPropertyValue(P_SPACE_COORDINATES.key());
			double[] coord = new double[tab.size()];
			for (int i=0; i<coord.length; i++)
				coord[i] = tab.getWithFlatIndex(i);
			coordinates.put(space,coord);
		}
		// this edge, if present, points to a Group node
		Edge instof = (Edge) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_INSTANCEOF.label())));
		if (instof==null)	   // system > structure >	componentType > component
			arena = (ArenaType) getParent().getParent().getParent();
		else
			group = (Group) instof.endNode();
		sealed = true;
	}

	@Override
	public int initRank() {
		return N_COMPONENT.initRank();
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

	@SuppressWarnings("unchecked")
	private Set<Category> generatedGroupCats() {
		Set<Category>result = new TreeSet<>();
		 				// 3Worlds > 	system > 	structure >	componentType > component
		TreeNode root3w =  getParent().getParent().getParent().getParent();
		TreeNode predef = (TreeNode) get(root3w.getChildren(),
			selectOne(hasTheLabel(N_PREDEFINED.label())));
		List<TreeNode> l = (List<TreeNode>) get(predef.getChildren(),
			selectZeroOrMany(hasTheLabel(N_CATEGORYSET.label())));
		for (TreeNode catSet:l) {
			for (TreeNode cat:catSet.getChildren()) {
				if (cat.id().equals(Category.group) ||
					cat.id().equals(Category.assemblage) ||
					cat.id().equals(Category.permanent))
					result.add((Category) cat);
			}
		}
		return result;
	}

	@Override
	public List<SystemComponent> getInstance(int id) {
		if (!sealed)
			initialise();
		if (!individuals.containsKey(id)) {
			List<SystemComponent> result = new ArrayList<>(nInstances);
			for (int i=0; i<nInstances; i++) {
				// instantiate component
				SystemComponent sc = componentFactory.getInstance(id).newInstance();
				// fill component with initial values
				for (TreeNode tn:getChildren())
					if (tn instanceof VariableValues) {
						// this copies all variables contained in Drivers but ignores automatc variables
						((VariableValues)tn).fill(sc.currentState());
						// this copies automatic variables, if any
						((VariableValues)tn).fill(sc.autoVar());
					}
				// including spatial coordinates
				for (SpaceNode spn:coordinates.keySet()) {
					DynamicSpace<SystemComponent,LocatedSystemComponent> sp = spn.getInstance(id);
					Location loc = sp.makeLocation(coordinates.get(spn));
					LocatedSystemComponent lsc = new LocatedSystemComponent(sc,loc);
					sp.addInitialItem(lsc);
				}
				// insert component into container
				ComponentContainer container = null;
				if (arena!=null)
					if (nComponentTypes==1) // means the container is the ArenaComponent
						container = arena.getInstance(id).getInstance().content();
					else { // group container must be created and inserted under arena
						ComponentContainer parentContainer = arena.getInstance(id).getInstance().content();
						String containerId = componentFactory.categoryId(); // check this is ok
						container = (ComponentContainer) parentContainer.subContainer(containerId);
						// POSSIBLE FLAW HERE: there is no Group node matching this group factory
						if (container==null) {
							Set<Category>cats = generatedGroupCats();
							GroupFactory gfac = new GroupFactory(cats,
								null,null,null,null,null,
								containerId,parentContainer);
							GroupComponent gComp = gfac.newInstance();
							container = gComp.content();
						}
					}
				else if (group!=null) // container is an existing group
					container = group.getInstance(id);
				container.setCategorized(componentFactory.getInstance(id));
				container.addInitialItem(sc);
				sc.setContainer((ComponentContainer)container);
				// add component instance into list of new instances
				result.add(sc);
			}
			individuals.put(id,result);
		}
		return individuals.get(id);
	}

}
