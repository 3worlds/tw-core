package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;


/**
 * In InitialState, a group attached to a life cycle must have subgroups attached to each of
 * the categories contained in its categorySet. This Query checks this constraint.
 * 
 * @author Jacques Gignoux - 10 janv. 2020
 *
 */
public class LifeCycleSubGroupsQuery extends Query {

	public LifeCycleSubGroupsQuery() { }

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a Group Node with an out edge to a life cycle
		defaultProcess(input);
		Group localItem = (Group) input;
		LifeCycle lc = (LifeCycle) get(localItem.edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_CYCLE.label())),
			endNode());
		if (lc==null)
			satisfied = true;
		else {
			// categories of the life cycle
			Collection<Category> lccats = (Collection<Category>) get(lc.edges(Direction.OUT),
				selectOne(hasTheLabel(E_APPLIESTO.label())),
				endNode(),
				children());
			int ncats = lccats.size();
			Set<Category> foundcats = new HashSet<>();
			// categories of the input node's children, which should be only groups
			for (TreeNode n:localItem.getChildren()) {
				if (n instanceof Group) {
					Group g = (Group) n;
					Collection<Category> sgcats = (Collection<Category>) get(g.edges(Direction.OUT),
						selectOne(hasTheLabel(E_GROUPOF.label())),
						endNode(),
						outEdges(),
						selectOneOrMany(hasTheLabel(E_BELONGSTO.label())),
						edgeListEndNodes());
					// checks that at least one group category is found in the life cycle's categories
					for (Category cg:sgcats) {
						if (lccats.contains(cg))
							foundcats.add(cg);
					}
				}
			}
			satisfied = (foundcats.size()==ncats);
		}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString()
			+ "A life cycle group must have at least one child group "
			+ "belonging to each category of its categorySet."
			+ "]";
	}

}
