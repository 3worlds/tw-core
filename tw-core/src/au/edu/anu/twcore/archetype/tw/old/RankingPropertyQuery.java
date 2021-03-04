package au.edu.anu.twcore.archetype.tw.old;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.old.queries.Query;
import fr.cnrs.iees.graph.DataHolder;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_CHILD;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Check that a series of Edges or Nodes which have a common number property have
 * values that can be strictly ranked (no check that the values are evenly spread, only that
 * there are no two equal values).
 *
 * @author J. Gignoux - 19 nov. 2020
 *
 */
@Deprecated
public class RankingPropertyQuery extends Query {

	private String edgeLabel = "";
	private String propName = "";

	/**
	 *
	 * @param args 2-dim StringTable [0] = edge label, [1] = rank property name
	 */
	public RankingPropertyQuery(StringTable args) {
		super();
		if (args.size()==2) {
			edgeLabel = args.getWithFlatIndex(0);
			propName = args.getWithFlatIndex(1);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 *  <p>The expected input is a {@linkplain TreeGraphNode}.</p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a TreeGraphNode
		defaultProcess(input);
		TreeGraphNode node = (TreeGraphNode) input;
		Collection<DataHolder> elements = null;
		if (edgeLabel.equals(E_CHILD.label()))
			elements = (Collection<DataHolder>) node.getChildren();
		else
			elements = (Collection<DataHolder>) get(node.edges(),
				selectZeroOrMany(hasTheLabel(edgeLabel)));
		satisfied = true;
		SortedSet<Number> ranks = new TreeSet<>();
		if (elements!=null) {
			for (DataHolder dh:elements)
				if (dh.properties().hasProperty(propName)) {
					Number rk = (Number) dh.properties().getPropertyValue(propName);
					ranks.add(rk);
				}
			// if two ranks are equal, then the set should be smaller than the collection
			if (ranks.size()<elements.size())
				satisfied = false;
		}
		return this;
	}

	public String toString() {
		String msg = propName;
		if (edgeLabel.equals(E_CHILD.label()))
			msg += " values of child nodes must be unique";
		else
			msg += " values of "+edgeLabel+" edges be unique";
		return stateString() + msg;
	}

}
