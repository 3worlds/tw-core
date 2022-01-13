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

import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleFactory;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.experiment.runtime.DataIdentifier;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class matching the "system/structure/groupType/group" node of the 3w configuration.
 * 
 * If a group points to dataSources, it may load more than one group.
 * otherwise it matches only one group with the name = group id
 *
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class Group
		extends InitialElement<GroupComponent>
		implements  DefaultStrings {
//		extends InitialisableNode
//		implements Sealable, LimitedEdition<GroupComponent> {

//	private Map<Integer,GroupComponent> groups = new HashMap<>();
	private static final int baseInitRank = N_GROUP.initRank();
	private GroupType groupType = null;
	private LifeCycle lifeCycle = null;
//	private ComponentType groupOf = null;
	private boolean hasLifeCycle = false;
	// helper list to retrieve already created LifeCycleComponents (integer for simId)(String for lcId)
	private Map<Integer,Map<String,LifeCycleComponent>> alreadyMadeLCs = new HashMap<>();

	// default constructor
	public Group(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Group(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		groupType = (GroupType) getParent();
		// find if this group belongs to a life cycle or directly depends on Arena
		hasLifeCycle = groupType.getParent() instanceof LifeCycleType;
		// this edge, if present, points to a lifeCycle node
		// NB: if lifecycles were loaded from files, then lifeCycle = null here
		Edge cycle = (Edge) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())));
		if (cycle!=null)
			lifeCycle = (LifeCycle) cycle.endNode();
		seal();
	}

	// this to call groups in proper dependency order, i.e. higher groups must be initialised first
	private int initRank(Group g, int rank) {
		if (g.getParent() instanceof Group)
			rank = initRank((Group)g.getParent(),rank) + 1;
		return rank;
	}

	@Override
	public int initRank() {
		return initRank(this,baseInitRank);
	}

//	@Override
//	public GroupComponent getInstance(int id) {
//		if (!sealed)
//			initialise();
//		if (!groups.containsKey(id)) {
//			// instantiate GroupComponent (with container, and super container)
//			// with this group's id as name
//			groupType.getInstance(id).setName(id());
//			GroupFactory gf = groupType.getInstance(id);
//			// put group into container hierarchy
//			ComponentContainer superContainer = null;
//			LifeCycleComponent lcc = null;
//			TreeNode parent = null;
//			// 1st case: there is a lifeCycle
//			if (lifeCycle!=null) {
//				lcc = lifeCycle.getInstance(id);
//				superContainer = (ComponentContainer) lcc.content();
//				parent = lcc;
//			}
//			// 2nd case: there is no lifeCycle
//			else {
//				ArenaType system = (ArenaType) get(this,parent(isClass(ArenaType.class)));
//				superContainer = (ComponentContainer)system.getInstance(id).getInstance().content();
//				parent = system;
//			}
//			GroupComponent gc = gf.newInstance(superContainer);
//			gc.connectParent(parent);
//			// fill group with initial values from the configuration file
//			for (TreeNode tn:getChildren())
//				if (tn instanceof VariableValues)
//					((VariableValues)tn).fill(gc.currentState());
//				else if (tn instanceof ConstantValues)
//					((ConstantValues) tn).fill(gc.constants());
//			// fill group with initial values read from file - overtake the previous
//			if (loadedData!=null)
//				for (String pkey:gc.properties().getKeysAsSet())
//					if (loadedData.hasProperty(pkey))
//						gc.properties().setProperty(pkey,loadedData.getPropertyValue(pkey));
//			// if no components, then some more inits must be done
//			if (groupOf!=null) {
//				// set itemCategories
//				gc.content().setCategorized(groupOf.getInstance(id));
//				gc.addGroupIntoLifeCycle();
//			}
//			groups.put(id,gc);
//		}
//		return groups.get(id);
//	}
	
	protected static ComponentContainer getContainingGroup(Component comp) {
//		ComponentContainer container = null;
//		// 1st case: there is a lifeCycle
//		if (hasLifeCycle) {
//			LifeCycleComponent lcc = null;
//			// this means life cycles were loaded from file
//			if (lifeCycle==null) {
//				LifeCycleType lct = (LifeCycleType) getParent().getParent();
//				LifeCycleFactory lcf = lct.getInstance(simId);
//				Map<String,LifeCycleComponent> llcc = null;
//				if (alreadyMadeLCs.containsKey(simId)) 
//					llcc = alreadyMadeLCs.get(simId);
//				else {
//					llcc = new HashMap<>();
//					alreadyMadeLCs.put(simId,llcc);
//				}
//				if (llcc.containsKey(lcId)) {
//					lcc = llcc.get(lcId);
//				}
//				else {
//					lcf.setName(lcId);
//					lcc = lcf.newInstance();
//					llcc.put(lcId,lcc);
//				}
//				container = (ComponentContainer) lcc.content();
////				container.setCategorized(factory.); // cannot be called?
//			}
//			// this means life cycle was pointed to with a cycle edge
//			else {
//				List<LifeCycleComponent> llcc = lifeCycle.getInstance(simId);
//				lcc = llcc.get(0);
//				container = (ComponentContainer) lcc.content();
//			}
//		}
//		// 2nd case: there is no lifeCycle
//		else {
//			ArenaType system = (ArenaType) get(this,parent(isClass(ArenaType.class)));
//			container = (ComponentContainer)system.getInstance(simId).getInstance().content();
//		}
//		return container;
//
		return null;
	}

	private ComponentContainer getContainer(int simId,String lcId,GroupFactory factory) {
		ComponentContainer container = null;
		// 1st case: there is a lifeCycle
		if (hasLifeCycle) {
			LifeCycleComponent lcc = null;
			// this means life cycles were loaded from file
			if (lifeCycle==null) {
				LifeCycleType lct = (LifeCycleType) getParent().getParent();
				LifeCycleFactory lcf = lct.getInstance(simId);
				Map<String,LifeCycleComponent> llcc = null;
				if (alreadyMadeLCs.containsKey(simId)) 
					llcc = alreadyMadeLCs.get(simId);
				else {
					llcc = new HashMap<>();
					alreadyMadeLCs.put(simId,llcc);
				}
				if (llcc.containsKey(lcId)) {
					lcc = llcc.get(lcId);
				}
				else {
					lcf.setName(lcId);
					lcc = lcf.newInstance();
					llcc.put(lcId,lcc);
				}
				container = (ComponentContainer) lcc.content();
//				container.setCategorized(factory.); // cannot be called?
			}
			// this means life cycle was pointed to with a cycle edge
			else {
				List<LifeCycleComponent> llcc = lifeCycle.getInstance(simId);
				lcc = llcc.get(0);
				container = (ComponentContainer) lcc.content();
			}
		}
		// 2nd case: there is no lifeCycle
		else {
			ArenaType system = (ArenaType) get(this,parent(isClass(ArenaType.class)));
			container = (ComponentContainer)system.getInstance(simId).getInstance().content();
		}
		return container;
	}
	
	
	// what about the groupId ?
	// TODO: refactor this - the search for the life cycle is wrong
	@Override
	protected GroupComponent makeInitialComponent(int simId, 
			DataIdentifier itemId, 
			SimplePropertyList props) {
		GroupFactory gf = groupType.getInstance(simId);
		// put group into container hierarchy
		ComponentContainer superContainer = getContainer(simId,itemId.lifeCycleId(),gf);
		// (temporarily) set the group name to itemId so that the groupComponent is initialized 
		// with proper name in container. Will handle case where itemId=null
		if (itemId!=null)
			gf.setName(itemId.groupId());
		GroupComponent gc = gf.newInstance(superContainer);
		gc.connectParent(superContainer.descriptors());
		for (String pkey:gc.properties().getKeysAsSet())
			if (props.hasProperty(pkey))
				gc.properties().setProperty(pkey,props.getPropertyValue(pkey));
		return gc;
	}

	@Override
	protected DataIdentifier fullId() {
		String groupId = id();
		String componentId = "";
		String LCId = "";		
		Edge cycle = (Edge) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())));
		if (cycle!=null)
			LCId = cycle.endNode().id();
		return new DataIdentifier(LCId,groupId,componentId);
	}

}
