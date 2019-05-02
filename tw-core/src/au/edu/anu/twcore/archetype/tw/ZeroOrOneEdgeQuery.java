package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.List;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;


public class ZeroOrOneEdgeQuery extends Query {

	private String edgeLabel = null;
	
	public ZeroOrOneEdgeQuery(String name) {
		edgeLabel=name;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		Node localItem = (Node) input;
		List<Edge> el = (List<Edge>) get(localItem,
			outEdges(),
			selectZeroOrMany(hasTheLabel(edgeLabel)));
		if (el.size()<=1) satisfied=true;
		return this;
	}
	
	public String toString() {
		return "[" + stateString() + " Node must have 0..1 out edges with label '" + edgeLabel+"']";
	}


}
