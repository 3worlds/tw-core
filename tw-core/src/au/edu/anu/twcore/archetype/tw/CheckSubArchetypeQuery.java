/**
 * 
 */
package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.archetype.Archetypes;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.Tree;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.SimpleTree;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * A Query to be processed while in an archetype - use it to check whole
 * subtrees of an archetype conditional on the presence / value of some property
 * for example: When specifying EcologicalProcess in the 3w archetype, one has
 * to set a class property matching a proper class name in the code. Depending
 * on this class, a whole subtree of edges and nodes, and specific properties
 * must be checked. There is no way to simply express this conditionality in the
 * current archetype syntax. SO I developed this Query to do that: based on the
 * value of some property, it will load a sub-archetype and test the current
 * AotNode and all its attached sub-tree against this sub-archetype.
 * 
 * This is a bit cheating with the Query concept -- maybe a better
 * implementation is required within the Archetypes syntax. But maybe not. I
 * think it's important to keep archetypes simple.
 * 
 * @author gignoux - 22 nov. 2016
 *
 */
public class CheckSubArchetypeQuery extends Query {

	private String pKey = null;
	private String pValue = null;
	private String fileName = null;

	/**
	 * @param parameters parameters[0] = name of the property on which this
	 *                   archetype is conditioned parameters[1] = value of this
	 *                   property parameters[2] = name of the sub-archetype file to
	 *                   check the node subtree against.
	 */
	public CheckSubArchetypeQuery(StringTable parameters) {
		super();
		pKey = parameters.getWithFlatIndex(0);
		pValue = parameters.getWithFlatIndex(1);
		fileName = parameters.getWithFlatIndex(2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.edu.anu.rscs.aot.queries.Query#process(java.lang.Object)
	 */
	@Override
	public Query process(Object input) {
		// Once these sub-archetypes have been checked they should not
		// be checked again!
		defaultProcess(input);
		ReadOnlyDataHolder localItem =  (ReadOnlyDataHolder) input;
		TreeNode node = (TreeNode) input;
		satisfied = true;
		Object givenpValue = localItem.properties().getPropertyValue(pKey);
		if (pValue.equals(givenpValue.toString())) {
			Tree<?> tree = (Tree<?>) GraphImporter.importGraph(fileName,getClass());
			// maybe this is a flaw to use this factory ?
			Tree<TreeNode> treeToCheck = new SimpleTree<TreeNode>(node.factory());
			for (TreeNode tn:node.subTree())
				treeToCheck.addNode((TreeNode) tn);
			Archetypes checker = new Archetypes();
			// Check the 3worlds archetype is ok
			if (checker.isArchetype(tree)) {
				checker.check(treeToCheck,tree);
				if (checker.errorList()==null)
					satisfied = true;
				else {
					satisfied = false;
					result = checker.errorList();
				}
			}
			else
				if (tree.root()!=null)
					throw new TwcoreException("Sub-archetype '"+tree.root().toShortString()+"' is not a valid archetype");
				else
					throw new TwcoreException("Sub-archetype '"+tree.toShortString()+"' is not a valid archetype");
		}
		return this;
	}
}
