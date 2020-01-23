package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Map;

import au.edu.anu.rscs.aot.collections.QuickListOfLists;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.NodeFactory;
import fr.ens.biologie.generic.Resettable;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
/**
 * Read-only graph view of the 3worlds ecosystem
 * 
 * @author Jacques Gignoux - 23 janv. 2020
 *
 */
public class EcosystemGraph implements Graph<SystemComponent,SystemRelation>, Resettable {

	/** "nodes" */
	private SystemContainer components = null;
	/** "edges" */
	private Map<String,RelationContainer> relations = null;
	
	public EcosystemGraph(SystemContainer components, Map<String,RelationContainer> relations) {
		super();
		this.components = components;
		this.relations = relations;
	}
	
	// GRAPH interface
	
	@Override
	public Iterable<SystemComponent> nodes() {
		return components.allItems();
	}

	@Override
	public boolean contains(SystemComponent node) {
		return components.contains(node);
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
		return components.count();
	}

	@Override
	public SystemComponent findNode(String id) {
		return components.item(id);
	}

	@Override
	public Iterable<SystemRelation> edges() {
		QuickListOfLists<SystemRelation> ql = new QuickListOfLists<>();
		for (RelationContainer relc:relations.values())
			ql.addList(relc.items());
		return ql;
	}

	@Override
	public EdgeFactory edgeFactory() {
		throw new TwcoreException("Forbidden: SystemRelationss cannot be instantiated by EcosystemGraph");
	}

	@Override
	public int nEdges() {
		int n=0;
		for (RelationContainer relc:relations.values())
			n += relc.count();
		return n;
	}

	@Override
	public SystemRelation findEdge(String id) {
		SystemRelation result = null;
		for (RelationContainer relc:relations.values()) {
			result = relc.item(id);
			if (result!=null)
				return result;
		}
		return result;
	}

	// LOCAL methods
	
	public SystemContainer community() {
		return components;
	}
	
	public RelationContainer relations(RelationType rel) {
		return relations.get(rel.id());
	}
	
	public RelationContainer relations(String rel) {
		return relations.get(rel);
	}
	
	private void getRelationsToRemove(CategorizedContainer<SystemComponent> comps) {
		for (String id:comps.itemsToRemove) {
			for (Edge e:comps.items.get(id).edges()) {
				SystemRelation sr = (SystemRelation) e;
				relations.get(sr.properties().getPropertyValue(P_RELATIONTYPE.key()))
					.removeItem(sr.id());;
			}
		}
		for (CategorizedContainer<SystemComponent> sc:comps.subContainers())
			getRelationsToRemove(sc);
	}

	public void effectChanges() {
		// First, graph structural changes
		// get all relations that have to disappear because their end/start component disappears
		getRelationsToRemove(components);
		// remove and create all relations
		for (RelationContainer relc:relations.values())
			relc.effectChanges();
		// remove and create all components
		components.effectAllChanges();
		// Second, graph state changes
		components.stepAll(); // must be done after -> no need to step dead ones + need to init newborns properly
	}

	@Override
	public void reset() {
		components.reset();
		for (RelationContainer rc: relations.values())
			rc.reset();		
	}
	
}
