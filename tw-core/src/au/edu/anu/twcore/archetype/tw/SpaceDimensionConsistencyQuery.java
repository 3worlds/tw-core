package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.twcore.constants.SpaceType;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_COORDMAPPING;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_SPACETYPE;
import java.util.List;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

/**
 * Check that the number of edge coordinates of a space exactly matches its dimension.
 *
 * @author J. Gignoux - 18 nov. 2020
 *
 */
public class SpaceDimensionConsistencyQuery extends Query {

	int dimension = 0;

	public SpaceDimensionConsistencyQuery() {
		super();
	}

	/**
	 * {@inheritDoc}
	 *
	 *  <p>The expected input is a {@linkplain SpaceNode}.</p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a SpaceNode
		SpaceNode spn = (SpaceNode) input;
		SpaceType spt = (SpaceType) spn.properties().getPropertyValue(P_SPACETYPE.key());
		dimension = spt.dimensions();
		List<Edge> coordEdges = (List<Edge>) get(spn.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_COORDMAPPING.label())));
		satisfied = (coordEdges.size()==dimension);
		return this;
	}

	public String toString() {
		String msg = "space must have " + dimension + " "+ E_COORDMAPPING.label() + " edges";
		return stateString() + msg;
	}

}
