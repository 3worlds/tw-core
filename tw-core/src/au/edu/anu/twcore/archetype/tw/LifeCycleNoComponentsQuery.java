package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.rscs.aot.queries.CoreQueries.endNode;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrOne;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_CYCLE;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Component;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;

/**
 * In InitialState, a group attached to a life cycle cannot have components as chlidren. They
 * must be contained in subgroups. This Query checks there are no components in this 
 * group's children if it's a lifecycle.
 * 
 * @author Jacques Gignoux - 10 janv. 2020
 *
 */
public class LifeCycleNoComponentsQuery extends Query {

	public LifeCycleNoComponentsQuery() { }

	@Override
	public Query process(Object input) { // input is a Group Node with an out edge to a life cycle
		defaultProcess(input);
		Group localItem = (Group) input;
		LifeCycle lc = (LifeCycle) get(localItem.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
			endNode());
		satisfied = true;
		if (lc!=null)
			for (TreeNode tn:localItem.getChildren())
				if (tn instanceof Component) {
					satisfied = false;					
					return this;
				}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString()
			+ "A life cycle group cannot have components."
			+ "]";
	}

}
