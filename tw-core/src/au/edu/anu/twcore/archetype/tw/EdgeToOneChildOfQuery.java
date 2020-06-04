package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.io.parsing.impl.NodeReference;

/**
 * Checks that an out edge points to one and only one child of a given node
 *
 * @author J. Gignoux - 21 mai 2020
 *
 */
public class EdgeToOneChildOfQuery extends Query {

	// the reference of the node in which to look for possible children
	private String nodeRef = null;

	public EdgeToOneChildOfQuery(String reference) {
		super();
		nodeRef = reference;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a node with out edges
		defaultProcess(input);
		Node localItem = (Node) input;
		// get all the nodes
		TreeNode rootNode = (TreeNode) localItem;
		while (rootNode.getParent()!=null)
			rootNode = rootNode.getParent();
		Iterable<TreeGraphNode> searchList = (Iterable<TreeGraphNode>) rootNode.subTree();
		// get the node which matches the reference passed as argument to the query constructor
		TreeNode cset = null;
		for (TreeNode catset:searchList)
			if (NodeReference.matchesRef(catset,nodeRef)) {
				cset=catset;
				break;
		}
		// searches in all edges if their end node is one of the children of the previous node
		boolean foundOne = false;
		if (cset!=null)
			for (Edge e:localItem.edges(Direction.OUT)) {
				TreeNode targetNode = (TreeNode) e.endNode();
				// search for the node with the proper reference in the whole tree
				for (TreeNode cat:cset.getChildren())
					if ((targetNode.id().equals(cat.id()) && (targetNode.classId().equals(cat.classId())))) {
						foundOne |= true;
						break;
				}
		}
		satisfied = foundOne;
		return this;
	}

	public String toString() {
		return stateString() + "out node must be one of the children of '" + nodeRef + "'.";
	}

}
