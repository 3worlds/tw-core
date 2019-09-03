
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

// NB Not yet generated but can be
public enum UIOrder {

	/* First container/widget in a container */
	first,

	/* Second container/widget in a container */
	second,;
	public static String[] toStrings() {
		String[] result = new String[UIOrder.values().length];
		for (UIOrder s : UIOrder.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (UIOrder e : UIOrder.values())
			result.add(e.toString());
		return result;
	}

	public static UIOrder defaultValue() {
		return first;// well depends one other??
	}

	static {
		ValidPropertyTypes.recordPropertyType(UIOrder.class.getSimpleName(), UIOrder.class.getName(), defaultValue());
	}

}
