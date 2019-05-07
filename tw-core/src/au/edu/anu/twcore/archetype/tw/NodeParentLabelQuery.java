package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Edge;

/**
 * @author Jacques Gignoux - 7/9/2016
 * Constraint on a an edge's end node's parent label
 *
 */
public class NodeParentLabelQuery extends ParentLabelQuery {

	public NodeParentLabelQuery(ObjectTable<?> ot) {
		super(ot);
	}

	public NodeParentLabelQuery(String s) {
		super(s);
	}

	
	@Override
	public Query process(Object input) { // input is edge pointing to node to be tested
		Edge localItem = (Edge) input;
		return super.process(localItem.endNode());
	}
	

}
