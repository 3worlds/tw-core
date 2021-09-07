package au.edu.anu.twcore.data.runtime;

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.collections.tables.Table;

/**
 * A specialized descendant of {@link ObjectTable} for 3Worlds generated data structures. All user-defined
 * nested data structures are made of this class and {@link TwData}.
 * 
 * @author Jacques Gignoux - 7 sept. 2021
 *
 * @param <T> a descendant of {@code TwData}
 */
public class TwDataTable<T extends TwData> extends ObjectTable<T> {

	public TwDataTable(Dimensioner... dimensions) {
		super(dimensions);
	}
	
	/**
	 * This implementation makes sure only leaf-values (i.e. primitive and String) are copied. To be used
	 * in generated code, in complement to {@code clone()}.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TwDataTable<T> copy(Table from) {
		if ((from!=null) && (from.getClass().equals(getClass()))) {
			TwDataTable<T> twt = (TwDataTable<T>) from;
			for (int i=0; i< flatSize; i++) {
				TwData twd = getWithFlatIndex(i);
				TwData fromtwd = twt.getWithFlatIndex(i);
				for (String key:twd.getKeysAsSet())
					// primitive types: copy value as is
					if (twd.getPropertyClass(key).isPrimitive())
						twd.setProperty(key, fromtwd.getPropertyValue(key));
					// string: copy value as is
					else if (twd.getPropertyClass(key).equals(String.class))
						twd.setProperty(key, fromtwd.getPropertyValue(key));
					// else: this must be a table --> use copy method (ie recurse)
					else if (Table.class.isAssignableFrom(twd.getPropertyClass(key)))
						((Table)twd.getPropertyValue(key)).copy((Table)fromtwd.getPropertyValue(key));
			}		
		}
		return this;
	}

}
