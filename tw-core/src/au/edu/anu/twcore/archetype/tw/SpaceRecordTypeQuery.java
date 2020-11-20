package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.impl.TreeGraphNode;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.List;

/**
 * A Query to make sure that fields used as space coordinates are either in a
 * driver or in a constant record
 *
 * @author J. Gignoux - 20 nov. 2020
 *
 */
public class SpaceRecordTypeQuery extends Query {

	public SpaceRecordTypeQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a SpaceNode
		defaultProcess(input);
		SpaceNode space = (SpaceNode) input;
		List<TreeGraphNode> fields = (List<TreeGraphNode>) get(space.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_COORDMAPPING.label())),
			edgeListEndNodes());
		satisfied = true;
		if (!fields.isEmpty()) {
			TreeGraphNode rec = (TreeGraphNode) fields.get(0).getParent();
			List<Edge> ln = (List<Edge>) get(rec.edges(Direction.IN),
				selectZeroOrMany(orQuery(
					hasTheLabel(E_DRIVERS.label()),
					hasTheLabel(E_CONSTANTS.label()))));
			if (ln.isEmpty())
				satisfied = false;
		}
		return this;
	}

	public String toString() {
		String msg = "coordinate fields must belong to a record used as drivers or constants";
		return stateString() + msg;
	}

}
