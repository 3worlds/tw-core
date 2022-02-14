package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.TreeNode;

/**
 * Checks that a node has exactly one child with one of the labels specified in the constructor
 * 
 * @author gignoux
 *
 */
public class ExclusiveChildQuery extends RequiredLabelQuery {

	public ExclusiveChildQuery(String... lab) {
		super(lab);
	}

	public ExclusiveChildQuery(StringTable el) {
		super(el);
	}

	@Override
	public Queryable submit(Object input) {  // input is a treenode
		initInput(input);
		if (input instanceof TreeNode) {
			int count = countLabels(((TreeNode)input).getChildren());
			if (count==0) {
				errorMsg = "Node '"+input.toString()+"' must have exactly one child of one of the types "+requiredLabels.toString()
					+", but none found";
				actionMsg = "Add one child of types "+requiredLabels.toString()+ " to node '"+input.toString()+"'";
			}
			else if (count>1) {
				errorMsg =  "Node '"+input.toString()+"' must have exactly one child of one of the types "+requiredLabels.toString()
					+", but "+ count+" found";
				actionMsg = "Keep only one child of types "+requiredLabels.toString()+ " in node '"+input.toString()+"'";
			}
			else if (count<0) {
				errorMsg = "Count is negative - How did you manage to do that?";
				actionMsg = "You should get some pills, a lot of rest, and come back to work later.";
			}
		}
		return this;
	}

}
