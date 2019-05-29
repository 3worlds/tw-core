/**
 * 
 */
package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.archetype.Archetypes;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.Tree;
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
		pKey = (String) parameters.getWithFlatIndex(0);
		pValue = (String) parameters.getWithFlatIndex(1);
		fileName = (String) parameters.getWithFlatIndex(2);
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
		satisfied = true;
		String givenpValue = (String) localItem.properties().getPropertyValue(pKey);
		if (pValue.equals(givenpValue)) {
//			File file = Resources.getPackagedFile("au.edu.anu.twcore.archetype.tw." + fileName);
//			OmugiGraphImporter importer = new OmugiGraphImporter(file);
//		// TODO untested - unsure of use case at the moment.
//			// At the moment loading a sub-archetype is different from importing (importResource statement in utg files)
//			Tree<?> tree = (Tree<?>) importer.getGraph();
			
			Tree<?> tree = (Tree<?>) GraphImporter.importGraph(fileName,getClass());
			
			Archetypes checker = new Archetypes();
			// Check the 3worlds archetype is ok
			if (checker.isArchetype(tree))
				satisfied = true;
			else {
				satisfied = false;
			}
		}
		return this;
	}
}
