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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.QuickListOfLists;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.ens.biologie.generic.Resettable;
/**
 * Read-only graph view of the 3worlds ecosystem
 *
 * @author Jacques Gignoux - 23 janv. 2020
 *
 */
public class EcosystemGraph
		implements Graph<SystemComponent,SystemRelation>,
			Resettable,
			ObservableDynamicGraph<SystemComponent,SystemRelation> {

	/** "nodes" */
	private ComponentContainer components = null;
	/** "edges" (NB edges are not contained in there) */
	private Map<String,RelationContainer> relations = null;

	private ArenaComponent arena = null;

	/** things which track changes in this graph, eg spaces */
	private Set<DynamicGraphObserver<SystemComponent,SystemRelation>> observers = new HashSet<>();

	public EcosystemGraph(ArenaComponent arena, Map<String,RelationContainer> relations) {
		super();
		this.relations = relations;
		this.arena = arena;
		this.components = (ComponentContainer) arena.content(); // may be null
	}

	// GRAPH interface

	public EcosystemGraph(ArenaComponent arena) {
		super();
		this.relations = new HashMap<String,RelationContainer>();
		this.arena = arena;
		this.components = (ComponentContainer) arena.content(); // may be null
	}

	@Override
	public Collection<SystemComponent> nodes() {
		if (components!=null)
			return components.allItems();
		return null;
	}

	@Override
	public boolean contains(SystemComponent node) {
		if (components!=null)
			return components.contains(node);
		return false;
	}

	@Override
	public NodeFactory nodeFactory() {
		throw new TwcoreException("Forbidden: SystemComponents cannot be instantiated by EcosystemGraph");
	}

	@Override
	public void addNode(SystemComponent node) {
		throw new TwcoreException("Forbidden: SystemComponents cannot be added to EcosystemGraph");
	}

	@Override
	public void removeNode(SystemComponent node) {
		throw new TwcoreException("Forbidden: SystemComponents cannot be removed from EcosystemGraph");
	}

	@Override
	public int nNodes() {
		// TODO
//		if (components!=null)
//			return components.populationData().totalCount();
		return 0;
	}

	@Override
	public SystemComponent findNode(String id) {
		if (components!=null)
			return components.item(id);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<SystemRelation> edges() {
		QuickListOfLists<SystemRelation> ql = new QuickListOfLists<>();
		if (components!=null)
			for (SystemComponent sc:components.allItems())
				ql.addList((Collection<SystemRelation>)sc.edges(Direction.OUT));
		return ql;
	}

	@Override
	public EdgeFactory edgeFactory() {
		throw new TwcoreException("Forbidden: SystemRelationss cannot be instantiated by EcosystemGraph");
	}

	@Override
	public int nEdges() {
		int n=0;
		if (components!=null)
			for (SystemComponent sc:components.allItems())
				n += sc.degree(Direction.OUT);
		return n;
	}

	// CAUTION: SLOW!
	@Override
	public SystemRelation findEdge(String id) {
		SystemRelation result = null;
		if (components!=null)
			for (SystemComponent sc:components.allItems()) {
				for (Edge e:sc.edges(Direction.OUT))
					if (e.id().equals(id))
						return (SystemRelation) e;
		}
		return result;
	}

	// LOCAL methods

	public ArenaComponent arena() {
		return arena;
	}

	public ComponentContainer community() {
		return components;
	}

	public RelationContainer relations(RelationType rel) {
		return relations.get(rel.id());
	}

	public RelationContainer relations(String rel) {
		return relations.get(rel);
	}

	public Collection<RelationContainer> relations() {
		return Collections.unmodifiableCollection(relations.values());
	}

	/**
	 *
	 * @return the list of newly created components
	 */
	public Collection<SystemComponent> effectChanges() {
		// First, graph structural changes
		// remove and create all relations
		for (RelationContainer relc:relations.values())
			relc.effectChanges();
		// remove and create all components
		List<SystemComponent> newComponents = new ArrayList<>();
		if (components!=null) {
			// this may possibly remove relations set just before
			components.effectAllChanges(newComponents);
			// Second, graph state changes (recursive)
			components.stepAll(); // must be done after -> no need to step dead ones + need to init newborns properly
		}
		if (arena.getDataTracker()!=null) {
			arena.getDataTracker().recordItem(SimulatorStatus.Active,this,arena.id());
		}
		// special treatment of arena only
		arena.stepForward();
		return newComponents;
	}

	@Override
	public void preProcess() {
		if (components!=null) {
			components.preProcess();
			components.setInitialState();
		}
		for (RelationContainer rc: relations.values())
			rc.preProcess();
		// reinitialise state variables and constants
		arena.preProcess();
		if (arena.getDataTracker()!=null) {
			arena.getDataTracker().setInitialTime();
			arena.getDataTracker().recordItem(SimulatorStatus.Initial,this , arena.id());
		}
	}

	@Override
	public void postProcess() {
		if (components!=null)
			components.postProcess();
		for (RelationContainer rc: relations.values())
			rc.postProcess();
	}

	// ObservableDynamicGraph

	@Override
	public void addObserver(DynamicGraphObserver<SystemComponent, SystemRelation> listener) {
		observers.add(listener);
		for (RelationContainer rc:relations.values())
			rc.addObserver(listener);
		components.addObserver(listener);
	}

	@Override
	public void removeObserver(DynamicGraphObserver<SystemComponent, SystemRelation> listener) {
		observers.remove(listener);
		for (RelationContainer rc:relations.values())
			rc.removeObserver(listener);
		components.removeObserver(listener);
	}

	@Override
	public Collection<DynamicGraphObserver<SystemComponent, SystemRelation>> observers() {
		return Collections.unmodifiableCollection(observers);
	}

}
