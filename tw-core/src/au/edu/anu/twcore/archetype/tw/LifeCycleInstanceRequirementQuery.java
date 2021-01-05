package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LifeCycle;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.Direction;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_CYCLE;

/**
 * Check that a Group instance has an edge to a lifeCycle if its groupType is under a LifeCycleType,
 * and also that the lifeCycle is under the same LifeCycleType
 *
 * @author J. Gignoux - 22 déc. 2020
 *
 */
public class LifeCycleInstanceRequirementQuery extends Query {

	public LifeCycleInstanceRequirementQuery() {
		super();
	}

	@Override
	public Query process(Object input) { // input is a Group node
		defaultProcess(input);
		Group group = (Group) input;
		GroupType groupType = (GroupType) group.getParent();
		// if groupType is under a lifeCycle, then group must have an edge to a LifeCycle
		if (groupType.getParent() instanceof LifeCycleType) {
			LifeCycle lc = (LifeCycle) get(group.edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
				endNode());
			// no 'cycle' edge found
			if (lc==null)
				satisfied = false;
			// a 'cyce' edge found, check it is under the same lifeCycle as groupType
			else {
				satisfied = (lc.getParent()==groupType.getParent());
			}
		}
		else
			satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + "Group must have a \"cycle\" edge to a LifeCycle]";
	}


}
