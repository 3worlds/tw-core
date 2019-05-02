/**
 * 
 */
package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.archetype.Archetypes;
import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.graph.AotGraph;
import au.edu.anu.rscs.aot.graph.AotNode;
import au.edu.anu.rscs.aot.queries.Query;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static  au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.logging.Logger;


/**
 * A Query to be processed while in an archetype - use it to check whole subtrees of 
 * an archetype conditional on the presence / value of some property
 * for example:
 * When specifying EcologicalProcess in the 3w archetype, one has to set a class
 * property matching a proper class name in the code. Depending on this class, a whole
 * subtree of edges and nodes, and specific properties must be checked. There is no
 * way to simply express this conditionality in the current archetype syntax. SO I developed
 * this Query to do that: based on the value of some property, it will load a sub-archetype and
 * test the current AotNode and all its attached sub-tree against this sub-archetype.
 * 
 * This is a bit cheating with the Query concept -- maybe a better implementation is
 * required within the Archetypes syntax. But maybe not. I think it's important to keep 
 * archetypes simple.
 * 
 * @author gignoux - 22 nov. 2016
 *
 */
public class CheckSubArchetypeQuery extends Query {
	
	private String pKey = null;
	private String pValue = null;
	private String fileName = null;
	private Logger log = Logger.getLogger(CheckSubArchetypeQuery.class.getName());
	
	/**
	 * @param parameters
	 * parameters[0] = name of the property on which this archetype is conditioned
	 * parameters[1] = value of this property
	 * parameters[2] = name of the sub-archetype file to check the node subtree against.
	 */
	public CheckSubArchetypeQuery(ObjectTable<Object> parameters) {
		super();
		pKey = (String) parameters.getWithFlatIndex(0);
		pValue = (String) parameters.getWithFlatIndex(1);
		fileName = (String) parameters.getWithFlatIndex(2);
	}

	/* (non-Javadoc)
	 * @see au.edu.anu.rscs.aot.queries.Query#process(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {
		defaultProcess(input);
		AotNode localItem = (AotNode) input;
		satisfied = true;
			
		if (pValue.equals(localItem.getPropertyValue(pKey))) {
			AotReader archetypeReader = Resource.getReader(fileName,"fr.ens.biologie.threeWorlds.ui.configuration.archetype3w");
			Tokenizer tokenizer = new Tokenizer(archetypeReader);
			UniversalParser parser = new UniversalParser(tokenizer);
			AotGraph archetype = parser.getGraph();
			archetype.castNodes();
			archetype.initialise();
			Archetypes checker = new Archetypes();
			// 1 check the 3worlds archetype is ok
			if (checker.isArchetype(archetype)){
				// 2 check the graph complies to the 3worlds archetype
				AotGraph nl = new AotGraph();
				nl.addNode(localItem);
				nl.addNodes((AotList<AotNode>)get(localItem,childTree()));				
				if (checker.complies(nl, archetype))
				  satisfied = true;
				else
				  satisfied = false;
			}
			else {
				// NB this should only be entered when we fiddle with the 3Worlds archetype, ie never after 3w is released
				log.error("Sub-archetype "+fileName+"has errors - unable to check configuration file - please contact J. Gignoux to fix these");
				satisfied = false;
			}
		}
		return this;
	}

}
