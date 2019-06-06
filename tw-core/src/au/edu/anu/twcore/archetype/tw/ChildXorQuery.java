package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.TreeNode;

/**
 * Checks that a CHILD treenode has either of two labels. 
 * @author Jacques Gignoux - 5/9/2016
 * Constraint: either 1..* nodes with label1 or 1..* nodes with label2
 */
public class ChildXorQuery extends Query {

	private String nodeLabel1 = null;
	private String nodeLabel2 = null;
	
	public ChildXorQuery(String nodeLabel1, String nodeLabel2) {
		this.nodeLabel1 = nodeLabel1;
		this.nodeLabel2 = nodeLabel2;
	}
	
	public ChildXorQuery(ObjectTable<?> table) {
		super();
		nodeLabel1 = (String) table.getWithFlatIndex(0);
		nodeLabel2 = (String) table.getWithFlatIndex(1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a node
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		List<TreeNode> nl1 = (List<TreeNode>) get(localItem, 
			children(),
			selectZeroOrMany(hasTheLabel(nodeLabel1)));
		List<TreeNode> nl2 = (List<TreeNode>) get(localItem,
			children(),			
			selectZeroOrMany(hasTheLabel(nodeLabel2)));
		satisfied = (nl1.size()>0)^(nl2.size()>0);
		return this;
	}

	public String toString() {
		return "[" + stateString() + " There must be at least one child node with either label '" + nodeLabel1 + "' or '"+nodeLabel2+"']";
	}

}
