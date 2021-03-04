package au.edu.anu.twcore.archetype.tw.old;

import java.util.List;

import au.edu.anu.rscs.aot.old.queries.Query;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.get;

/**
 * A Query to check that edges to/from a particular node having the same label
 * point to nodes which are all children of the same node (ie siblings).
 *
 * @author J. Gignoux - 18 nov. 2020
 *
 */
@Deprecated
public class EdgeToSiblingNodesQuery extends Query {

	private String label;

	/**
	 * Constructor for use in archetype <strong>.ugt</strong> files.
	 * @param label the label (as returned by {@linkplain fr.cnrs.iees.graph.Edge#classId})
	 * of the edges that are to be tested
	 */
	public EdgeToSiblingNodesQuery(String label) {
		super();
		this.label = label;
	}

	/**
	 * {@inheritDoc}
	 *
	 *  <p>The expected input is a {@linkplain TreeGraphNode} with IN or OUT
	 *  edges having the <em>label</em> passed to the constructor.</p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a TreeGraphNode
		defaultProcess(input);
		TreeGraphNode node = (TreeGraphNode) input;
		List<TreeGraphNode> fields = (List<TreeGraphNode>) get(node.edges(),
			selectZeroOrMany(hasTheLabel(label)),
			edgeListOtherNodes(node));
		satisfied = true;
		if (fields.size() >= 1) {
			/**
			 * Parent may be null but thats ok. If all parents are null (i.e. during MM
			 * editing) then this query can't make a decision so returning satisfied is ok
			 * until the parent links are re-established.
			 */
			TreeGraphNode theParent = (TreeGraphNode) fields.get(0).getParent();
			for (TreeGraphNode f : fields)
				if ((f.getParent()!=null) && (f.getParent()!=theParent))
					satisfied = false;
		}
		return this;
	}

	public String toString() {
		String msg = label + " edges must refer to sibling nodes, i.e. nodes with the same parent";
		return stateString() + msg;
	}

}
