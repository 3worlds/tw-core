package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;

/**
 * A Query to check that a node has at least one edge of each type listed in the archetype
 * use eg for relations which must have a toCategory and fromCategory edges, with 
 * multiplicities 1..* for each
 * @author gignoux - 3 févr. 2017
 *
 */
public class EdgeAtLeastOneOfEachQuery extends Query {
	
	private ObjectTable<Object> valueSet = null;
	
	/**
	 * build the query from a list of values present in the Archetype
	 * @param values
	 */	
	@SuppressWarnings("unchecked")
	public EdgeAtLeastOneOfEachQuery(ObjectTable<?> values) {
		super();
		valueSet = (ObjectTable<Object>)values;
	}


	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		Node localItem = (Node) input;
		satisfied = true;
		for (int i=0; i<valueSet.size(); i++) {
			String edgeLabel = (String) valueSet.getWithFlatIndex(i);
			boolean found = false;
			for (Edge e:localItem.edges(Direction.OUT)) {
				if (e.classId().equals(edgeLabel)) {
					found = true;
					break;
				}
			}
			satisfied = satisfied & found;
		}
		return this;
	}

	public String toString() {
		return "[" + stateString() + " There must be at least one edge of each type: " + valueSet.toString() + "]";
	}

}
