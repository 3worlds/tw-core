package au.edu.anu.twcore.archetype.tw;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * A query to check that a TreeNode parent has a certain property value
 * 
 * @author Jacques Gignoux - 9 août 2019
 *
 */
public class ParentHasPropertyValueQuery extends Query {
	
	private String propertyName = null;
	private List<Object> expectedValues = new LinkedList<Object>();

	// constructors.... many cases
	private ParentHasPropertyValueQuery(String pname) {
		super();
		propertyName = pname;
	}
	// reminder: parameters may come in any order
	// Strings
	public ParentHasPropertyValueQuery(String pname, StringTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(StringTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// ints
	public ParentHasPropertyValueQuery(String pname, IntTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(IntTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// doubles
	public ParentHasPropertyValueQuery(String pname, DoubleTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(DoubleTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// longs
	public ParentHasPropertyValueQuery(String pname, LongTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(LongTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// shorts
	public ParentHasPropertyValueQuery(String pname, ShortTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(ShortTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// bytes
	public ParentHasPropertyValueQuery(String pname, ByteTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(ByteTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// floats
	public ParentHasPropertyValueQuery(String pname, FloatTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(FloatTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// booleans
	public ParentHasPropertyValueQuery(String pname, BooleanTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(BooleanTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// chars
	public ParentHasPropertyValueQuery(String pname, CharTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public ParentHasPropertyValueQuery(CharTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// todo: add other property types ?

	@Override
	public Query process(Object input) { // input is a TreeNode
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		TreeNode parent = localItem.getParent();
		if (parent!=null)
			if (parent instanceof ReadOnlyDataHolder) {
				ReadOnlyPropertyList props = ((ReadOnlyDataHolder) parent).properties();
				if (props.hasProperty(propertyName))
					for (Object o:expectedValues)
						if (o.equals(props.getPropertyValue(propertyName))) {
							satisfied = true;
							break;
						}
			}
		return this;
	}
	
	public String toString() {
		return "[" + stateString() + " |Parent property '"
			+ propertyName + "' must have value '" 
			+ expectedValues.toString() + "'|]";
	}


}
