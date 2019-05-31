package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import au.edu.anu.rscs.aot.collections.tables.StringTable;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;


/**
 * @author Jacques Gignoux - 5/9/2016
 * Constraint: some nodes must have at least ONE of a property or an edge 
 * NB the check is made on the edge's endNode label because edges can sometimes have a _child label
 */
public class EdgeOrPropertyQuery extends Query {
	
	private String nodeLabel = null;
	private String propertyName = null;
	
	public EdgeOrPropertyQuery(StringTable args) {
		nodeLabel = args.getWithFlatIndex(0);
		propertyName = args.getWithFlatIndex(1);
	}

	@Override
	public Query process(Object input) { // NB: input is the AotNode on which the Query is called		
		defaultProcess(input);
		Node localItem = (Node) input;
		boolean propertyPresent = false;
		if (localItem instanceof ReadOnlyDataHolder)
			propertyPresent = (((ReadOnlyDataHolder) localItem).properties().hasProperty(propertyName));
		Node n = (Node) get(localItem,
			outEdges(),
			edgeListEndNodes(),
			selectZeroOrOne(hasTheLabel(nodeLabel)));
		boolean edgePresent = (n!=null);
		satisfied = (propertyPresent|edgePresent);
		return this;
	}
	
	public String toString() {
		return "[" + stateString() + " Node must have property '" + propertyName.toString() + "' or edge to '"+nodeLabel+"']";
	}

}
