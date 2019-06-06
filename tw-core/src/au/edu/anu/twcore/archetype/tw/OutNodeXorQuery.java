package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;

/**
 * Checks that an out node has either of two labels.
 *  
 * @author Jacques Gignoux - 6 juin 2019
 * Constraint: either 1..* nodes with label1 or 1..* nodes with label2
 * 
 */
public class OutNodeXorQuery extends Query {

	private String nodeLabel1 = null;
	private String nodeLabel2 = null;
	
	public OutNodeXorQuery(String nodeLabel1, String nodeLabel2) {
		this.nodeLabel1 = nodeLabel1;
		this.nodeLabel2 = nodeLabel2;
	}
	
	public OutNodeXorQuery(ObjectTable<?> table) {
		super();
		nodeLabel1 = (String) table.getWithFlatIndex(0);
		nodeLabel2 = (String) table.getWithFlatIndex(1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a node
		defaultProcess(input);
		Node localItem = (Node) input;
		List<Node> nl1 = (List<Node>) get(localItem, 
			edges(Direction.OUT),
			edgeListEndNodes(),
			selectZeroOrMany(hasTheLabel(nodeLabel1)));
		List<Node> nl2 = (List<Node>) get(localItem,
			edges(Direction.OUT),
			edgeListEndNodes(),
			selectZeroOrMany(hasTheLabel(nodeLabel2)));
		satisfied = (nl1.size()>0)^(nl2.size()>0);
		return this;
	}

	public String toString() {
		return "[" + stateString() + " There must be at least one out node with either label '" + nodeLabel1 + "' or '"+nodeLabel2+"']";
	}

}
