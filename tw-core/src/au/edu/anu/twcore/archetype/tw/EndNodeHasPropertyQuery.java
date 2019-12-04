package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;

/**
 * Checks that an end node has a specific property with a non null value
 * 
 * @author Jacques Gignoux - 6 nov. 2019
 *
 */
public class EndNodeHasPropertyQuery extends Query {

	String propName = null;
	ReadOnlyDataHolder rodh = null;
	
	public EndNodeHasPropertyQuery(String propname) {
		super();
		propName = propname;
	}

	@Override
	public Query process(Object input) { // input is an Edge
		defaultProcess(input);
		rodh = (ReadOnlyDataHolder)((Edge)input).endNode();
		if (rodh.properties().hasProperty(propName))
			if (rodh.properties().getPropertyValue(propName)!=null)
				satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + "end node '" + rodh 
			+ "' must have the '"+propName+"' property.]";
	}

}
