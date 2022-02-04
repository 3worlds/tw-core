package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.TreeNode;

/**
 * Checks that a node is the only one of its kind in its parent's children for a particular
 * parent type (enables to eimplement conditional multiplicity of child in parent)
 * 
 * @author Jacques Gignoux - 4 févr. 2022
 *
 */
public class UniqueSiblingQuery extends QueryAdaptor {
	
	private String parentType = null;
	
	public UniqueSiblingQuery(String parentType) {
		super();
		this.parentType = parentType.trim();
	}
	
	public UniqueSiblingQuery() {
		super();
	}

	@Override
	public Queryable submit(Object input) { // input is the node to check
		initInput(input);
		if (input instanceof TreeNode) {
			TreeNode child = (TreeNode) input;
			TreeNode parent = child.getParent();
			if ((parentType==null) || (parent.classId().equals(parentType))) {
				int count=0;
				for (TreeNode tn: parent.getChildren())
					if (tn.classId().equals(child.classId()))
						count++;
				if (count>1) {
					actionMsg = "Leave only one child of class '"+child.classId()+
						"' for node '"+parent.toString()+"'";
					errorMsg = "Node '"+parent.toString()+
						"' cannot have more than 1 child of class '"+child.classId()+"'";
				}
			}
		}
		return this;
	}

}
