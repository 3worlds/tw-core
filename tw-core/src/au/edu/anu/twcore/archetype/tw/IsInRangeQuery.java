package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.IntTable;
import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.queries.Query;

/**
 * @author Jacques Gignoux - 21/3/2018 Constraint on numeric properties: value
 *         must be within a given interval
 */
public class IsInRangeQuery extends Query {

	private double min;
	private double max;

	/**
	 * Use this constructor if values are provided as a table of 2 values, min first
	 * @param interval
	 */
	public IsInRangeQuery(IntTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}

	/**
	 * Use this constructor if two values are provided as two numbers (int or double)
	 * @param mini
	 * @param maxi
	 */
	public IsInRangeQuery(Number mini, Number maxi) {
		super();
		min = mini.doubleValue();
		max = maxi.doubleValue();
		if (min>max) {
			double d = min;
			min = max;
			max = d;
		}
	}

	@Override
	public Query process(Object input) { // input is a prop here
		defaultProcess(input);
		Property localItem = (Property) input;
		double value = ((Number) localItem.getValue()).doubleValue();
		satisfied = (value >= min) & (value <= max);
		return this;
	}

	public String toString() {
		return "[" + stateString() + " value must be within [" + min + "; " + max + "]]";
	}

}
