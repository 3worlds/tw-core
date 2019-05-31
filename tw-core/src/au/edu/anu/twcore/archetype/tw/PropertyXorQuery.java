package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;

/**
 * A Query to test that a node, edge or treenode has either of two properties, but not both
 * @author gignoux - 22 nov. 2016
 *
 */
public class PropertyXorQuery extends Query {

	private String name1 = null;
	private String name2 = null;
	
	public PropertyXorQuery(String name1, String name2) {
		this.name1 = name1;
		this.name2 = name2;
	}

	public PropertyXorQuery(StringTable ot) {
		name1 = ot.getWithFlatIndex(0);
		name2 = ot.getWithFlatIndex(1);
	}

	@Override
	public Query process(Object input) { // input is a node, treenode or edge
		defaultProcess(input);
		ReadOnlyDataHolder e = (ReadOnlyDataHolder) input;
		satisfied = e.properties().hasProperty(name1)^e.properties().hasProperty(name2);
		return this;
	}

	public String toString() {
		return "[" + this.getClass().getName() +" Must have either '"+name1+"' or '"+name2+ "' property]";
	}

}
