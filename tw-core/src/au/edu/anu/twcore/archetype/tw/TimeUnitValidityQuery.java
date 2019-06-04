package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.twcore.constants.TimeScaleType;
import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * A Query to check that time units are valid according to a time scale type
 * Applies to a property of a Node which is or has a link to a TimeLine node
 * (with a scale property)
 * 
 * @author gignoux
 *
 */
public class TimeUnitValidityQuery extends Query {

	private TimeScaleType refScale = null;
	private String pscale = null;
	private String pname = null;

	public TimeUnitValidityQuery(StringTable parameters) {
		super();
		pname = parameters.getWithFlatIndex(0); // name of the time property
		pscale = parameters.getWithFlatIndex(1); // name of the time scale prop
	}

	@Override
	public Query process(Object input) { // input is a Node with 2 properties, one of them has the time scale
		defaultProcess(input);
		ReadOnlyDataHolder localItem = (ReadOnlyDataHolder) input;
		TreeNode localNode = (TreeNode) input;
		// search for a property named pscale, which has the time scale type
		refScale = (TimeScaleType) localItem.properties().getPropertyValue(pscale);
		// If null, this query should remain silent;
		if (refScale == null) {
			ReadOnlyDataHolder p = (ReadOnlyDataHolder) localNode.getParent();
			if (p != null) 
				refScale = (TimeScaleType) p.properties().getPropertyValue(pscale);
		}
		Property prop = null;
		prop = localItem.properties().getProperty(pname);
		if (prop == null)
			// satisfied = false;
			satisfied = true;
		else if (refScale == null)
			satisfied = true;
		else {
			TimeUnits tu = (TimeUnits) prop.getValue();
			if (tu == null)
				tu = TimeUnits.UNSPECIFIED;
			satisfied = TimeScaleType.validTimeUnits(refScale).contains(tu);
		}
		return this;
	}

	public String toString() {
		return "[" + stateString() + " Property value for " + pname + " must be one of {"
				+ TimeScaleType.validTimeUnits(refScale).toString() + "}]";
	}

}
