package au.edu.anu.twcore.archetype.tw.old;

import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_CYCLE;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_GROUPTYPE;

import java.util.Collection;

import au.edu.anu.rscs.aot.old.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LifeCycle;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.Direction;

/**
 * Checks that a lifeCycle instance has exactly one Group of each of its LifeCycleType
 * GroupTypes (repeat ten times and then ask).
 *
 * @author J. Gignoux - 22 déc. 2020
 *
 */
@Deprecated
public class GroupInstanceRequirementQuery extends Query {

	public GroupInstanceRequirementQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a LifeCycle node
		defaultProcess(input);
		LifeCycle lifeCycle = (LifeCycle) input;
		LifeCycleType lct = (LifeCycleType) lifeCycle.getParent();
		Collection<GroupType> gts = (Collection<GroupType>) get(lct.getChildren(),
			selectZeroOrMany(hasTheLabel(N_GROUPTYPE.label())));
		Collection<Group> gs = (Collection<Group>) get(lifeCycle.edges(Direction.IN),
			selectZeroOrMany(hasTheLabel(E_CYCLE.label())),
			edgeListStartNodes());
		satisfied = true;
		// lists must be of the same length
		if (gs.size()!=gts.size())
			satisfied = false;
		// for each group, check its grouptype is in the grouptype list by removing it
		// from the list
		for (Group g:gs)
			satisfied &= gts.remove(g.getParent());
		// if there was no error (ie exactly one group per grouptype) then the list should be empty:
		satisfied &= gts.isEmpty();
		return this;
	}

	public String toString() {
		return "[" + stateString() + "LifeCycle must have exactly one instance of Group per GroupType of its LifeCycleType]";
	}

}
