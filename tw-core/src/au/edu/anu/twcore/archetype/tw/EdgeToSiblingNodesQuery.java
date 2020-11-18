package au.edu.anu.twcore.archetype.tw;

import java.util.List;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;


/**
 * A Query to check that edges to/from a particular node having the same label
 * point to nodes which are all children of the same node (ie siblings).
 *
 * @author J. Gignoux - 18 nov. 2020
 *
 */
public class EdgeToSiblingNodesQuery extends Query {

	private String label;

	public EdgeToSiblingNodesQuery(String label) {
		super();
		this.label = label;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a SpaceNode
		defaultProcess(input);
		TreeGraphNode node = (TreeGraphNode) input;
		List<TreeGraphNode> fields = (List<TreeGraphNode>) get(node.edges(),
			selectOneOrMany(hasTheLabel(label)),
			// this gets all the nodes of the edge list which have node at their other end
			edgeListOtherNodes(node));
		if (fields.size()>=1) {
			satisfied = true;
			TreeGraphNode theParent = (TreeGraphNode) fields.get(0).getParent();
			for (TreeGraphNode f:fields)
				if (f.getParent()!=theParent)
					satisfied = false;
		}
		return this;
	}

	public String toString() {
		String msg = label + " edges must refer to sibling nodes, ie nodes with the same parent";
		return stateString() + msg;
	}


}
