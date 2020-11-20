package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.twcore.constants.DataElementType;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_COORDMAPPING;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_FIELD_TYPE;
import java.util.List;

/**
 * Check that a field pointed at by a space coordinate edge is of a numeric type
 *
 * @author J. Gignoux - 19 nov. 2020
 *
 */
public class SpaceCoordinateTypeQuery extends Query {

	public SpaceCoordinateTypeQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a SpaceNode
		defaultProcess(input);
		SpaceNode space = (SpaceNode) input;
		List<DataHolder> fields = (List<DataHolder>) get(space.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_COORDMAPPING.label())),
			edgeListEndNodes());
//		EnumSet<DataElementType> numberTypes = EnumSet.of(Double,Integer,Long,Float,Short,Byte);
		satisfied = true;
		for (DataHolder f:fields) {
			if (f.properties().hasProperty(P_FIELD_TYPE.key())) {
				DataElementType ftype = (DataElementType) f.properties().getPropertyValue(P_FIELD_TYPE.key());
//				if (!numberTypes.contains(ftype))
				if (!ftype.isNumeric())
					satisfied = false;
			}
		}
		return this;
	}

	public String toString() {
		String msg = "coordinate fields must be numeric";
		return stateString() + msg;
	}

}
