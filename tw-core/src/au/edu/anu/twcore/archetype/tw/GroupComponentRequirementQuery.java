package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.Collection;


/**
 * Checks that a group either has in 'instanceOf' edges or one 'groupOf' edge
 *
 * @author J. Gignoux - 22 déc. 2020
 *
 */
public class GroupComponentRequirementQuery extends Query {

	public GroupComponentRequirementQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a Group node
		defaultProcess(input);
		Group group = (Group) input;
		satisfied = true;
		Edge groupof = (Edge) get(group.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_GROUPOF.label())));
		Collection<Edge> instofs = (Collection<Edge>) get(group.edges(Direction.IN),
			selectZeroOrMany(hasTheLabel(E_INSTANCEOF.label())));
		satisfied = (groupof==null) ^ (instofs.isEmpty());
		return this;
	}

	public String toString() {
		return "[" + stateString() + "If no Component is instance of Group, Group must have a groupOf link to a ComponentType]";
	}

}
