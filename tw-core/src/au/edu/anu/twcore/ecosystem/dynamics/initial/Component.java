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

import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupFactory;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import au.edu.anu.twcore.ecosystem.structure.Structure;
import au.edu.anu.twcore.root.World;
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
	private ComponentType componentType = null;
	private Map<Integer,List<SystemComponent>> individuals = new HashMap<>();
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

	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		if (properties().hasProperty(P_COMPONENT_NINST.key()))
			nInstances = (int) properties().getPropertyValue(P_COMPONENT_NINST.key());
		if (nInstances==0)
			nInstances=1;
		TreeNode struc = (TreeNode) get(this,parent(isClass(Structure.class)));
		nComponentTypes = ((Collection<?>) get(struc,
			childTree(),
			selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label()))) ).size();
//		nComponentTypes = ((Collection<?>)get(getParent().getParent().getChildren(),
//			selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())))).size();
		componentType = (ComponentType) getParent();
		// this edge, if present, points to a Group node
		Edge instof = (Edge) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_INSTANCEOF.label())));
		if (instof==null)
//			arena = (ArenaType) getParent().getParent().getParent();
			arena = (ArenaType) get(this,parent(isClass(ArenaType.class)));
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
		TreeNode root3w =  (TreeNode) get(this,parent(isClass(World.class)));
//		 				// 3Worlds > 	system > 	structure >	componentType > component
//		TreeNode root3w =  getParent().getParent().getParent().getParent();
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
	@SuppressWarnings("unchecked")
	public List<SystemComponent> getInstance(int id) {
		if (!sealed)
			initialise();
		if (!individuals.containsKey(id)) {
			// the factory for components of this category
			ComponentFactory factory = componentType.getInstance(id);
			// the spaces in this model
			TreeNode struc = (TreeNode) get(this,parent(isClass(Structure.class)));
//			TreeNode struc = this;
//			while (!(struc instanceof Structure))
//				struc = struc.getParent();
			Collection<SpaceNode> spaces = (Collection<SpaceNode>) get(struc,
				children(),
				selectZeroOrMany(hasTheLabel(N_SPACE.label())));
			List<SystemComponent> result = new ArrayList<>(nInstances);
			// for as many instances as requested:
			for (int i=0; i<nInstances; i++) {
				// instantiate component
				SystemComponent sc = factory.newInstance();
				// fill component with initial values (including coordinates)
				for (TreeNode tn:getChildren()) {
					if (tn instanceof VariableValues) {
						// this copies all variables contained in Drivers but ignores automatc variables
						((VariableValues)tn).fill(sc.currentState());
						// this copies automatic variables, if any
						((VariableValues)tn).fill(sc.autoVar());
					}
					else if (tn instanceof ConstantValues) {
						((ConstantValues) tn).fill(sc.constants());
					}
				}
				// place component into spaces, if any
				for (SpaceNode space:spaces) {
					DynamicSpace<SystemComponent> sp = space.getInstance(id);
					// locate component in all spaces
					// Rule: if n instances >1 or no constantValue or variableValue node,
					// then generate random locations - otherwise use provided locations
					if ((getChildren().isEmpty())||(nInstances>1)) { // no coordinates were passed in init
						// generate random coordinates
						double[] initLoc = sp.randomLocation();
						sc.locationData().setCoordinates(initLoc);
						sp.addInitialItem(sc);
					}
					else // use coordinates found in children nodes
						sp.addInitialItem(sc);
				}
				// insert component into its container
				ComponentContainer container = null;
				// find the proper container
				// 1st case: there is a group and possibly a life cycle
				if (group!=null) {
					GroupComponent grp = group.getInstance(id);
					container = (ComponentContainer)grp.content();
					container.setCategorized(factory);
					grp.addGroupIntoLifeCycle();
				}
				// 2nd case: there is no group (and no life cycle)
				else if (arena!=null) {
					// if there is only one component type, then the arena must be the container
					if (nComponentTypes==1)		// ArenaType	ArenaFactory	ArenaComponent	Container
						container = (ComponentContainer)arena.getInstance(id).getInstance().content();
					// otherwise, a default group container per componentType is created, with no data
					else { // group container must be created and inserted under arena
						ComponentContainer parentContainer = (ComponentContainer)arena.getInstance(id).getInstance().content();
						String containerId = componentType.categoryId(); // check this is ok - IT's NOT!!!
//						String containerId = "group";
						container = (ComponentContainer) parentContainer.subContainer(containerId);
						// POSSIBLE FLAW HERE: there is no Group node matching this group factory
						if (container==null) {
							Set<Category>cats = generatedGroupCats();
							GroupFactory gfac = new GroupFactory(cats,
								null,null,null,null,null,
								containerId,id);
							GroupComponent gComp = gfac.newInstance(parentContainer);
							container = (ComponentContainer)gComp.content();
						}
					}
					container.setCategorized(factory);
				}
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
