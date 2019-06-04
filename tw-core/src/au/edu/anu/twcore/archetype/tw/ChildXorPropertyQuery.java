package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import au.edu.anu.rscs.aot.collections.tables.StringTable;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;


/**
 * @author Jacques Gignoux - 31/5/2019
 * 
 * Constraint: some nodes must have ONE of either a property or a child node
 */
public class ChildXorPropertyQuery extends Query {
	
	private String nodeLabel = null;
	private String propertyName = null;
	
	public ChildXorPropertyQuery(StringTable args) {
		nodeLabel = args.getWithFlatIndex(0);
		propertyName = args.getWithFlatIndex(1);
	}

	@Override
	public Query process(Object input) { // NB: input is the TreeNode on which the Query is called		
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		boolean propertyPresent = false;
		if (localItem instanceof ReadOnlyDataHolder)
			propertyPresent = (((ReadOnlyDataHolder) localItem).properties().hasProperty(propertyName));
		Node n = (Node) get(localItem,
			children(),
			selectZeroOrOne(hasTheLabel(nodeLabel)));
		boolean edgePresent = (n!=null);
		satisfied = (propertyPresent^edgePresent);
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + " Node must have either property '" + propertyName.toString() + "' or edge to '"+nodeLabel+"']";
	}

}
