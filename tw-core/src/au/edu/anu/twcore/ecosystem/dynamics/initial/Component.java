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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupFactory;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.ecosystem.structure.Structure;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.experiment.runtime.DataIdentifier;
import au.edu.anu.twcore.root.World;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 *
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
@Deprecated
public class Component
		extends InitialElement<SystemComponent>
		implements  DefaultStrings {

	private ComponentType componentType = null;
	// the container in which new components should be placeds
	private ArenaType arena = null;
	// the group instance this component is instance of
	private Group group = null;
	// number of component types declared
	// if only one, then components can be stored in the arena
	// if >1, then even if no groups are declared, components must be stored in separate group
	// containers matching category signatures
	private int nComponentTypes = 0;
	
	private boolean hasGroup = false;
	// helper list to retrieve already created GroupComponents (integer for simId)(String for groupId)
	private Map<Integer,Map<String,GroupComponent>> alreadyMadeGroups = new HashMap<>();
	
	private boolean hasLifeCycle = false;
	private LifeCycle lifeCycle = null;

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
		componentType = (ComponentType) getParent();
		// find if this component belongs to a group or directly depends on Arena
		hasGroup = componentType.getParent() instanceof GroupType;
		// find if group belongs to a life cycle or directly depends on Arena
		hasLifeCycle = componentType.getParent().getParent() instanceof LifeCycleType;
		// this edge, if present, points to a Group node
		// NB: if groups were loaded from files, then group = null here
		Edge instof = (Edge) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_INSTANCEOF.label())));
		if (instof!=null) {
			group = (Group) instof.endNode();
			Edge cycle = (Edge) get(group.edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(E_INSTANCEOF.label())));
			if (cycle!=null)
				lifeCycle = (LifeCycle) cycle.endNode();
		}
		// get total number of ComponentTypes declared in the structure subTree
		// NB there may be no structure node, but in this case there will be no component node either
		// so if we are here, struc!=null
		arena = (ArenaType) get(this,parent(isClass(ArenaType.class)));
		TreeNode struc = (TreeNode) get(arena.getChildren(),
			selectZeroOrOne(hasTheLabel(N_STRUCTURE.label()))); 
		nComponentTypes = ((Collection<?>) get(struc, childTree(),
			selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label()))) ).size();
		seal();
	}

	@Override
	public int initRank() {
		throw new TwcoreException("obsolete code");
//		return N_COMPONENT.initRank();
	}

	@SuppressWarnings("unchecked")
	private Set<Category> generatedGroupCats() {
		Set<Category>result = new TreeSet<>();
		TreeNode predef = (TreeNode) get(World.getRoot(this).getChildren(),
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

//	@Override
//	public List<SystemComponent> getInstance(int id) {
//		if (!sealed)
//			initialise();
//		if (!individuals.containsKey(id)) {
//			// the factory for components of this category
//			ComponentFactory factory = componentType.getInstance(id);
//
//			List<SystemComponent> result = new ArrayList<>(nInstances);
//			// for as many instances as requested:
//			for (int i=0; i<nInstances; i++) {
//				// instantiate component
//				SystemComponent sc = factory.newInstance();
//				// fill component with initial values (including coordinates) from the configuration file
//				for (TreeNode tn:getChildren()) {
//					if (tn instanceof VariableValues) {
//						// this copies all variables contained in Drivers but ignores automatic variables
//						((VariableValues)tn).fill(sc.currentState());
//						// this copies automatic variables, if any
//						((VariableValues)tn).fill(sc.autoVar());
//					}
//					else if (tn instanceof ConstantValues) {
//						((ConstantValues) tn).fill(sc.constants());
//					}
//				}
//				// fill component with initial values read from file - overtake the previous
//				if (!loadedData.isEmpty()) {
//					SimplePropertyList ldpl = loadedData.get(i);
//					for (String pkey:sc.properties().getKeysAsSet())
//						if (ldpl.hasProperty(pkey))
//							sc.properties().setProperty(pkey, ldpl.getPropertyValue(pkey));
//				}
//				// insert component into its container
//				ComponentContainer container = null;
//				// find the proper container
//				// 1st case: there is a group and possibly a life cycle
//				if (group!=null) {
//					GroupComponent grp = group.getInstance(id);
//					container = (ComponentContainer)grp.content();
//					container.setCategorized(factory);
//					// this will add into lifeCycle only if it exists. otherwise does nothing
//					grp.addGroupIntoLifeCycle();
//				}
//				// 2nd case: there is no group (and no life cycle)
//				else {
//					ComponentContainer parentContainer =
//									// ArenaType	ArenaFactory	ArenaComponent	Container
//						(ComponentContainer)arena.getInstance(id).getInstance().content();
//					// if there is only one component type, then the arena must be the container
//					if (nComponentTypes==1)		
//						container = parentContainer;
//					// otherwise, a default group container per componentType is created, with no data
//					else { // group container must be created and inserted under arena
//						// JG 17/2/2021
//						// TODO (?): Alternative solution here is to use component id() as the
//						// group containerId. This way different containers can be generated for the
//						// same componentType - let's discuss this
//						String containerId = componentType.categoryId(); 
//						container = (ComponentContainer) parentContainer.subContainer(containerId);
//						// POSSIBLE FLAW HERE: there is no Group node matching this group factory
//						// That's fine - we dont need the group node as long as the factory and matching container are here
//						if (container==null) {
//							Set<Category>cats = generatedGroupCats();
//							GroupFactory gfac = new GroupFactory(cats,
//								null,null,null,null,null,
//								containerId,id);
//							GroupComponent gComp = gfac.newInstance(parentContainer);
//							container = (ComponentContainer)gComp.content();
//						}
//					}
//					container.setCategorized(factory);
//				}
//				// prepare initial community
//				container.addInitialItem(sc);
//				sc.setContainer((ComponentContainer)container);
//				// add component instance into list of new instances
//				result.add(sc);
//			}
//			individuals.put(id,result);
//		}
//		return individuals.get(id);
//	}
	
//	private GroupComponent getGroup(int simId) {
//		GroupComponent grp = null;
//		// 1 there is a instanceOf edge to a group
//		if (group!=null) {
//			for (GroupComponent gc:group.getInstance(simId))
//				if (gc.name().equals(group.id())) {
//					grp = gc;
//					break;
//			}
//		} 
//		// groups are read from files
//		else {
//			
//		}
//		return grp;
//	}
	
	// a bit clumsy - could probably be simplified by grouping cases properly
	private ComponentContainer getContainer(int simId,String groupId,String lcId,ComponentFactory factory) {
		ComponentContainer container = null;
		ComponentContainer arenaContainer =
						// ArenaType	ArenaFactory	ArenaComponent	Container
			(ComponentContainer)arena.getInstance(simId).getInstance().content();
		// find the proper container
		// 1st case: there is a group and possibly a life cycle
		if (hasGroup) {
			GroupComponent grp = null;
			// this means groups were loaded from file
			if (group==null) {
				GroupType gt = (GroupType) getParent().getParent();
				GroupFactory gf = gt.getInstance(simId);
				Map<String,GroupComponent> lgc = null;
				if (alreadyMadeGroups.containsKey(simId))
					lgc = alreadyMadeGroups.get(simId);
				else {
					lgc = new HashMap<>();
					alreadyMadeGroups.put(simId,lgc);
				}
				if (lgc.containsKey(groupId))
					grp = lgc.get(groupId);
				else {
					gf.setName(groupId);
					// get supercontainer for group
					// no life cycle --> arena container
					if (gt.getParent() instanceof Structure) {
						if (arenaContainer.findContainer(groupId)==null)
							grp = gf.newInstance(arenaContainer);
						else
							grp = (GroupComponent) ((ComponentContainer)arenaContainer
								.findContainer(groupId)).descriptors();
					}
					// life cycle --> get the proper life cycle container
					else if (gt.getParent() instanceof LifeCycleType) {
						throw new TwcoreException("not implemented yet");
					}
//					grp = gf.newInstance();
					lgc.put(groupId,grp);
				}
			}
			// this means group was pointed to with a instanceOf edge
			// which also means this group has a group container properly attached
			else {
				for (GroupComponent gc:group.getInstance(simId)) {
					if (gc.name().equals(group.id())) {
						grp = gc;
						break;
					}
//					if (hasLifeCycle) {
//						LifeCycleComponent lcc = null;
//						if (lifeCycle==null) {
//							
//						}
//						else {
//							for (LifeCycleComponent lcc)
//						}
//					}
				}
			}
			container = (ComponentContainer)grp.content();
			container.setCategorized(factory);
			// this will add into lifeCycle only if it exists. otherwise does nothing
			grp.addGroupIntoLifeCycle();

		}
		// 2nd case: there is no group (and no life cycle)
		else {
			// if there is only one component type, then the arena must be the container
			if (nComponentTypes==1)		
				container = arenaContainer;
			// otherwise, a default group container per componentType is created, with no data
			else { // group container must be created and inserted under arena
				// JG 17/2/2021
				// TODO (?): Alternative solution here is to use component id() as the
				// group containerId. This way different containers can be generated for the
				// same componentType - let's discuss this
				String containerId = componentType.categoryId(); 
				container = (ComponentContainer) arenaContainer.subContainer(containerId);
				// POSSIBLE FLAW HERE: there is no Group node matching this group factory
				// That's fine - we dont need the group node as long as the factory and matching container are here
				if (container==null) {
					Set<Category>cats = generatedGroupCats();
					GroupFactory gfac = new GroupFactory(cats,
						null,null,null,null,null,
						containerId,simId);
					GroupComponent gComp = gfac.newInstance(arenaContainer);
					container = (ComponentContainer)gComp.content();
				}
			}
			container.setCategorized(factory);
		}
		return container;
	}

	@Override
	protected SystemComponent makeInitialComponent(int simId, 
			DataIdentifier itemId, 
			SimplePropertyList props) {
		ComponentFactory factory = componentType.getInstance(simId);
		SystemComponent sc = factory.newInstance();
		// populate new instance with loaded data
		for (String pkey:sc.properties().getKeysAsSet())
			if (props.hasProperty(pkey))
				sc.properties().setProperty(pkey,props.getPropertyValue(pkey));
		// insert component into its container
		ComponentContainer container = getContainer(simId,itemId.groupId(),itemId.lifeCycleId(),factory);
		// prepare initial community
		container.addInitialItem(sc);
		sc.setContainer((ComponentContainer)container);
		return sc;
	}

	@Override
	protected DataIdentifier fullId() {
		String groupId = "";
		String componentId = id();
		String LCId = "";		
		Edge instof = (Edge) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_INSTANCEOF.label())));
		if (instof!=null) {
			groupId = instof.endNode().id();
			Edge cycle = (Edge) get(edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(E_CYCLE.label())));
			if (cycle!=null)
				LCId = cycle.endNode().id();
		}
		return new DataIdentifier(LCId,groupId,componentId);
	}

}
