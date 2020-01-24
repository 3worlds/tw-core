package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Map;

import au.edu.anu.rscs.aot.collections.QuickListOfLists;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.NodeFactory;
import fr.ens.biologie.generic.Resettable;
/**
 * Read-only graph view of the 3worlds ecosystem
 * 
 * @author Jacques Gignoux - 23 janv. 2020
 *
 */
public class EcosystemGraph 
		implements Graph<SystemComponent,SystemRelation>, Resettable {

	/** "nodes" */
	private SystemContainer components = null;
	/** "edges" (NB edges are not contained in there) */
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
		return components.totalCount();
	}

	@Override
	public SystemComponent findNode(String id) {
		return components.item(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<SystemRelation> edges() {
		QuickListOfLists<SystemRelation> ql = new QuickListOfLists<>();
		for (SystemComponent sc:components.allItems())
			ql.addList((Iterable<SystemRelation>)sc.edges(Direction.OUT));
		return ql;
	}

	@Override
	public EdgeFactory edgeFactory() {
		throw new TwcoreException("Forbidden: SystemRelationss cannot be instantiated by EcosystemGraph");
	}

	@Override
	public int nEdges() {
		int n=0;
		for (SystemComponent sc:components.allItems())
			n += sc.degree(Direction.OUT);
		return n;
	}

	// CAUTION: SLOW!
	@Override	
	public SystemRelation findEdge(String id) {
		SystemRelation result = null;
		for (SystemComponent sc:components.allItems()) {
			for (Edge e:sc.edges(Direction.OUT))
				if (e.id().equals(id))
					return (SystemRelation) e;
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

	public void effectChanges() {
		// First, graph structural changes
		// remove and create all components
		components.effectAllChanges();
		// remove and create all relations
		for (RelationContainer relc:relations.values())
			relc.effectChanges();
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
