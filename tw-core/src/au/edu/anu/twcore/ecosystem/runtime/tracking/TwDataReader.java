package au.edu.anu.twcore.ecosystem.runtime.tracking;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.IndexedDataLabel;
import au.edu.anu.twcore.data.runtime.OutputTwData;
import au.edu.anu.twcore.data.runtime.TwData;

/**
 * Two methods to recursively extract data from a TwData hierarchy and store them in any message
 * that implements the outpuTwData interface
 * 
 * @author Jacques Gignoux - 3 mars 2021
 *
 */
public class TwDataReader {
	
	// this to prevent any instantiation
	private TwDataReader() {}
	
	/**
	 * This will get any data in a twData hierarchy and put it in any OutputTwData message.
	 * Recursive.
	 * 
	 * @param root
	 * @param lab
	 * @param tsd
	 */
	public static void getValue(TwData root, DataLabel lab, OutputTwData tsd) {
		getRecValue(0,root,lab,tsd);
	}

	// cross-recursive with below
	private static void getTableValue(int depth, Table table, int[] index, DataLabel lab, OutputTwData tsd) {
		if (table instanceof ObjectTable<?>) {
			ObjectTable<?> t = (ObjectTable<?>) table;
			TwData next = (TwData) t.getByInt(index);
			getRecValue(depth, next, lab, tsd);
		} else { // this is a table of primitive types and we are at the end of the label
			if (table instanceof DoubleTable)
				tsd.setValue(lab, ((DoubleTable) table).getByInt(index));
			else if (table instanceof FloatTable)
				tsd.setValue(lab, ((FloatTable) table).getByInt(index));
			else if (table instanceof IntTable)
				tsd.setValue(lab, ((IntTable) table).getByInt(index));
			else if (table instanceof LongTable)
				tsd.setValue(lab, ((LongTable) table).getByInt(index));
			else if (table instanceof BooleanTable)
				tsd.setValue(lab, ((BooleanTable) table).getByInt(index));
			else if (table instanceof ShortTable)
				tsd.setValue(lab, ((ShortTable) table).getByInt(index));
			else if (table instanceof ByteTable)
				tsd.setValue(lab, ((ByteTable) table).getByInt(index));
			else if (table instanceof StringTable)
				tsd.setValue(lab, ((StringTable) table).getByInt(index));
		}
	}
	// cross-recursive with above
	private static void getRecValue(int depth, TwData root, DataLabel lab, OutputTwData tsd) {
		String key = lab.get(depth);
		if (key.contains("["))
			key = key.substring(0, key.indexOf("["));
		if (root.hasProperty(key)) {
			Object next = root.getPropertyValue(key);
			if (next instanceof Table) {
				getTableValue(depth + 1, (Table) next, ((IndexedDataLabel) lab).getIndex(depth), lab, tsd);
			} else { // this is a primitive type and we should be at the end of the label
				if (next instanceof Double)
					tsd.setValue(lab, (double) next);
				else if (next instanceof Float)
					tsd.setValue(lab, (float) next);
				else if (next instanceof Integer)
					tsd.setValue(lab, (int) next);
				else if (next instanceof Long)
					tsd.setValue(lab, (long) next);
				else if (next instanceof Boolean)
					tsd.setValue(lab, (boolean) next);
				else if (next instanceof Short)
					tsd.setValue(lab, (short) next);
				else if (next instanceof Byte)
					tsd.setValue(lab, (byte) next);
				else if (next instanceof String)
					tsd.setValue(lab, (String) next);
			}
		}
	}

}
