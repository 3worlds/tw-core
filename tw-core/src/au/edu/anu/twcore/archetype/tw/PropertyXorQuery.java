package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataElement;

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

	public PropertyXorQuery(ObjectTable<?> ot) {
		name1 = (String) ot.getWithFlatIndex(0);
		name2 = (String) ot.getWithFlatIndex(1);
	}

	@Override
	public Query process(Object input) { // input is a node, treenode or edge
		defaultProcess(input);
		ReadOnlyDataElement e = (ReadOnlyDataElement) input;
		satisfied = e.properties().hasProperty(name1)^e.properties().hasProperty(name2);
		return this;
	}

	public String toString() {
		return "[" + this.getClass().getName() +" Must have either '"+name1+"' or '"+name2+ "' property]";
	}

}
