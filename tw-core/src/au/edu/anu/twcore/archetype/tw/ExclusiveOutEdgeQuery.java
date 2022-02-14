package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;

/**
 * Checks that a node has exactly one out-edge with one of the labels specified in the constructor
 * 
 * @author gignoux
 *
 */
public class ExclusiveOutEdgeQuery extends RequiredLabelQuery {

	public ExclusiveOutEdgeQuery(String... lab) {
		super(lab);
	}

	public ExclusiveOutEdgeQuery(StringTable el) {
		super(el);
	}

	@Override
	public Queryable submit(Object input) {  // input is a node
		initInput(input);
		if (input instanceof Node) {
			int count = countLabels(((Node)input).edges(Direction.OUT));
			if (count==0) {
				errorMsg = "Node '"+input.toString()+"' must have exactly one out-edge of one of the types "+requiredLabels.toString()
					+", but none found";
				actionMsg = "Add one out-edge of types "+requiredLabels.toString()+ " to node '"+input.toString()+"'";
			}
			else if (count>1) {
				errorMsg =  "Node '"+input.toString()+"' must have exactly one out-edge of one of the types "+requiredLabels.toString()
					+", but "+ count+" found";
				actionMsg = "Keep only one out-edge of types "+requiredLabels.toString()+ " in node '"+input.toString()+"'";
			}
			else if (count<0) {
				errorMsg = "Count is negative - How did you manage to do that?";
				actionMsg = "You should get some pills, a lot of rest, and come back to work later.";
			}
		}
		return this;
	}

}
