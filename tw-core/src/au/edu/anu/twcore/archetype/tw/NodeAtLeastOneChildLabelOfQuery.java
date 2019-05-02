package au.edu.anu.twcore.archetype.tw;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.TreeNode;

/**
 * @author Ian Davies
 * @date 17 May 2018
 */
/*-
 * Input is node. 
 * Check that node has at least one child with a label in the
 * list.
 */
public class NodeAtLeastOneChildLabelOfQuery extends Query {
	private List<String> labels = new LinkedList<String>();

	public NodeAtLeastOneChildLabelOfQuery(ObjectTable<?> table) {
		super();
		for (int i = 0; i < table.size(); i++)
			labels.add((String) table.getWithFlatIndex(i));
	}

	@Override
	public Query process(Object input) {
		defaultProcess(input);
		TreeNode node = (TreeNode) input;
		Iterable<? extends TreeNode> children = node.getChildren();
		satisfied = false;
		for (String label : labels) {
			for (TreeNode child : children) {
				if (child.classId().equals(label)) {
					satisfied = true;
					break;
				}
			}
		}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + " Must have at least one child labelled '" + 
			labels.toString() + "']";
	}

}
