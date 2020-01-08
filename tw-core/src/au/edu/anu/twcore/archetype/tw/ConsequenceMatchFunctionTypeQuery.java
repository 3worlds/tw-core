package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * A Query to check that a consequence type is compatible with its master function type 
 * 
 * @author Jacques Gignoux - 8 Jan. 2020
 *
 */
public class ConsequenceMatchFunctionTypeQuery extends Query {
	
	private String consequenceType = null;
	private String functionType = null;

	public ConsequenceMatchFunctionTypeQuery() {
		
	}

	@Override
	public Query process(Object input) { // object is a FunctionNode
		defaultProcess(input);
		FunctionNode fn = (FunctionNode) input;
		if (fn.getParent() instanceof FunctionNode) {
			TwFunctionTypes csqtype = (TwFunctionTypes) fn.properties().getPropertyValue(P_FUNCTIONTYPE.key());
			FunctionNode pn = (FunctionNode) fn.getParent();
			TwFunctionTypes ftype = (TwFunctionTypes) pn.properties().getPropertyValue(P_FUNCTIONTYPE.key());
			satisfied = TwFunction.consequenceTypes(ftype).contains(csqtype);
			consequenceType = csqtype.name();
			functionType = ftype.name();
		}
		return this;
	}

	public String toString() {
		return "[" + stateString() + "Consequence type '" + consequenceType + 
				"' incompatible with a " + functionType + " function.";
	}

}
