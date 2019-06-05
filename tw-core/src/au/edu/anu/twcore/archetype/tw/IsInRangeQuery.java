package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.ByteTable;
import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.rscs.aot.collections.tables.FloatTable;
import au.edu.anu.rscs.aot.collections.tables.IntTable;
import au.edu.anu.rscs.aot.collections.tables.LongTable;
import au.edu.anu.rscs.aot.collections.tables.ShortTable;
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
	 * Use this constructors if values are provided as a table of 2 values, min first
	 * @param interval
	 */
	public IsInRangeQuery(IntTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}
	public IsInRangeQuery(LongTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}
	public IsInRangeQuery(ShortTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}
	public IsInRangeQuery(FloatTable interval) {
		super();
		min = ((Number) interval.getWithFlatIndex(0)).doubleValue();
		max = ((Number) interval.getWithFlatIndex(1)).doubleValue();
	}
	public IsInRangeQuery(DoubleTable interval) {
		super();
		min = interval.getWithFlatIndex(0);
		max = interval.getWithFlatIndex(1);
	}
	public IsInRangeQuery(ByteTable interval) {
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
