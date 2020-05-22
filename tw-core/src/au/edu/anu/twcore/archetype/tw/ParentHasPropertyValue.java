package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.TreeNode;

/**
 * A query to check that a TreeNode parent has a certain property value
 *
 * @author Jacques Gignoux - 9 août 2019
 *
 */
public class ParentHasPropertyValue extends NodeHasPropertyValueQuery {

	public ParentHasPropertyValue(String pname, StringTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(StringTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, IntTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(IntTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, DoubleTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(DoubleTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, LongTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(LongTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, ShortTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(ShortTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, ByteTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(ByteTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, FloatTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(FloatTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, BooleanTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(BooleanTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, CharTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(CharTable values, String pname) {
		super(values, pname);
	}

	@Override
	public Query process(Object input) {
		TreeNode localItem = (TreeNode) input;
		TreeNode parent = localItem.getParent();
		return super.process(parent);
	}

	public String toString() {
		return "[" + stateString() + "Parent property '"
			+ propertyName + "' must have value '"
			+ expectedValues.toString() + "'.]";
	}

}
