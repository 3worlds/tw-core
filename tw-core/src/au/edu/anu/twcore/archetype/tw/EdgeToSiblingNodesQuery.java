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
		/**
		 * Avoid triggering a lower level query by *not* using oneOrMany: use zeroOrMany
		 * instead. Otherwise the user gets msgs that are impossible to interpret.
		 */
		// this gets all the nodes of the edge list which have node at their other end
		List<TreeGraphNode> fields = (List<TreeGraphNode>) get(node.edges(), selectZeroOrMany(hasTheLabel(label)),
				edgeListOtherNodes(node));
		if (fields.isEmpty())// level it to the SpaceDimensionsConistancyQuery to flag the problem
			satisfied = true;
		else if (fields.size() >= 1) {
			satisfied = true;
			/**
			 * Parent may be null but thats ok. If all parents are null (i.e. during MM
			 * editing) then this query can't make a decision so returning satisfied is ok
			 * until the parent links are re-established.
			 */
			TreeGraphNode theParent = (TreeGraphNode) fields.get(0).getParent();
			for (TreeGraphNode f : fields)
				if (f.getParent() != theParent)
					satisfied = false;
		}
		return this;
	}

	public String toString() {
		String msg = label + " edges must refer to sibling nodes, i.e. nodes with the same parent";
		return stateString() + msg;
	}

}
