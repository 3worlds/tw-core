
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

// NB Not yet generated but can be
public enum UIContainerOrientation {

/* Container is split by a vertical spliter into two panels first (on left) second (on right)*/
	horizontal,

/* Container is split by a horizontal spliter into two panels first above second*/
	vertical,
;	
	public static String[] toStrings() {
		String[] result = new String[UIContainerOrientation.values().length];
		for (UIContainerOrientation s: UIContainerOrientation.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (UIContainerOrientation e: UIContainerOrientation.values())
			result.add(e.toString());
		return result;
	}

	public static UIContainerOrientation defaultValue() {
		return horizontal;
	}

	static {
		ValidPropertyTypes.recordPropertyType(UIContainerOrientation.class.getSimpleName(), 
		UIContainerOrientation.class.getName(),defaultValue());
	}

}

