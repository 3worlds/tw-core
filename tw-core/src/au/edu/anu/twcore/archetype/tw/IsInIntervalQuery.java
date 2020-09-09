package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.queries.Query;
import fr.ens.biologie.generic.utils.Interval;

/**
 * 
 * @author Jacques Gignoux - 9 sept. 2020
 *
 */
public class IsInIntervalQuery extends Query {

	private Interval interval;
	private Property localItem;
	
	public IsInIntervalQuery(Interval interval) {
		super();
		this.interval = interval;
	}

	@Override
	public Query process(Object input) { // input is a property containing a number
		defaultProcess(input);
		localItem = (Property)input;
		double value = ((Number) localItem.getValue()).doubleValue();
		satisfied = interval.contains(value);
		return this;
	}

	public String toString() {
		//NB will crash if process has not been run
		return "[" + stateString() + "Property "+localItem.getKey()+"="+localItem.getValue()+"' must be within " + interval + " ]";
	}

}
