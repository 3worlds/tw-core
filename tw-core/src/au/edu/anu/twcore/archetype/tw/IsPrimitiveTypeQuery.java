package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.graph.property.Property;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

/**
 * @author Jacques Gignoux - 5/9/2016
 * Constraint on type properties: content must be a PrimitiveType value
 * 
 * NB I looked for this Query in the aot packages but couldnt find one, so here it is.
 * maybe it should be moved back to aot as it may be useful there
 * In tw, primitive is replaced by enum fr.ens.biologie.threeWorlds.resources.core.constants.DataElementType
 * so its no longer required unless the hasProperty type is a String
 */
public class IsPrimitiveTypeQuery extends Query {
	
	public IsPrimitiveTypeQuery() {
		super();
	}

	@Override
	public Query process(Object input) { // input is a prop here
		defaultProcess(input);
		Property localItem = (Property) input;
		String name = (String)localItem.getValue();
		satisfied = ValidPropertyTypes.isPrimitiveType(name);
		return this;
	}

	public String toString() {
		return "[" + stateString() + " Type must be primitive]";
	}

}
