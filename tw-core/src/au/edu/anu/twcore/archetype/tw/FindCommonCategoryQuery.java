package au.edu.anu.twcore.archetype.tw;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.List;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.ecosystem.dynamics.DataTrackerNode;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

/**
 * A Query to check that both nodes of an edge have a common parent of a
 * specified type
 * 
 * @author Jacques Gignoux - 6 nov. 2019
 *
 */
public class FindCommonCategoryQuery extends Query {

	private String trackName = "";

	public FindCommonCategoryQuery() {
		super();
	}

	private boolean matchTopRec(Record topRec, TreeNode parent) {
		while (parent != null) {
			if (parent.equals(topRec)) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}

	private Node process;

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is an edge between a datatracker (start) and a field or table (end)
		defaultProcess(input);
		DataTrackerNode start = (DataTrackerNode) ((Edge) input).startNode();
		TreeNode end = (TreeNode) ((Edge) input).endNode();
		trackName = end.id();
		process = start.getParent();
		if (process == null) {
			satisfied = true;
			return this;
		}
		List<Node> ln = (List<Node>) get(process.edges(Direction.OUT),
				selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())), edgeListEndNodes());
		if (ln.isEmpty()) {
			satisfied = true;
			return this;
		}
		if (ln.get(0) instanceof Category) {
			for (Node cat : ln) {
				Record topRec = (Record) get(cat.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_DRIVERS.label())),
						endNode());
				if (topRec != null)
					satisfied = matchTopRec(topRec, end);
				if (!satisfied) {
					topRec = (Record) get(cat.edges(Direction.OUT), selectZeroOrOne(hasTheLabel(E_DECORATORS.label())),
							endNode());
					if (topRec != null)
						satisfied = matchTopRec(topRec, end);
				}
			}
		}
		return this;
	}

	public String toString() {
		if (process == null)
			return "[" + stateString() + " ||Track variable '" + trackName
					+ "' does not belong to any of the DataTracker's process categories.||]";
		else
			return "[" + stateString() + " ||Track variable '" + trackName
					+ "' does not belong to any of the DataTracker's '" + process.classId() + ":" + process.id()
					+ "' categories.||]";
	}
}
