package au.edu.anu.twcore.data.runtime;

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.collections.tables.Table;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

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
		if (from!=null) { 
			// 1 for descendants of TwDataTable in generated code
			// no check on dimensions because these are hard coded in the constructor
			// so a check on class equality is sufficient
			if (from.getClass().equals(getClass())) {
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
			// 2 for other cases - typically, when data has been load as a ObjectTable<ReadOnlyPropertyList>
			// and must be copied to a TwDataTable
			else if (from instanceof ObjectTable<?>)
				// ObjectTable.contentType() always return Object.class until overwritten, so better
				// check first element of the table
				// problem of course if table is empty
				if (((ObjectTable<?>)from).getWithFlatIndex(0) instanceof ReadOnlyPropertyList) {
					// check dimensions
					if (sameDimensionsAs(from)) {
						for (int i=0; i< flatSize; i++) {
							TwData twd = getWithFlatIndex(i);
							ReadOnlyPropertyList fromtwd = (ReadOnlyPropertyList) 
								((ObjectTable<?>)from).getWithFlatIndex(i);
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
			}
		}
		return this;
	}

}
