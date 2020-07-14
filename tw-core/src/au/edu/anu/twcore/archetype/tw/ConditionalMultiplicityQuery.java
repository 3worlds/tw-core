package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.Collection;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;


/**
 * Checks that a multiplicity is 0 if a certain condition is met, 1 otherwise.
 * At the moment only applicable to properties and child nodes, but could be
 * adapted to other cases by adding parameters or putting sytax in.
 *
 * @author J. Gignoux - 14 juil. 2020
 *
 */
public class ConditionalMultiplicityQuery extends Query {

	private String property = null;
	private String nodeLabel = null;
	private int nMin = 0;

	public ConditionalMultiplicityQuery(StringTable args, Integer nMin) {
		super();
		property = args.getWithFlatIndex(0);
		nodeLabel = args.getWithFlatIndex(1);
		this.nMin = nMin;
	}

	public ConditionalMultiplicityQuery(Integer nMin, StringTable args) {
		this(args,nMin);
	}

	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		ReadOnlyDataHolder rodh = (ReadOnlyDataHolder) input;
		Collection<?> l = (Collection<?>) get(localItem.getChildren(),
			selectZeroOrMany(hasTheLabel(nodeLabel)));
		satisfied = ((l.size()>=nMin) && (rodh.properties().hasProperty(property))) ||
			(l.size()<nMin);
//		satisfied = ((l.size()>=nMin) && (rodh.properties().hasProperty(property))) ||
//				((l.size()<nMin) && (!rodh.properties().hasProperty(property)));
		return this;
	}

}
