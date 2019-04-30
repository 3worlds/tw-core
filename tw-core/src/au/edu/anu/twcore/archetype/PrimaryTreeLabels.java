package au.edu.anu.twcore.archetype;

import java.util.HashSet;
import java.util.Set;

import fr.cnrs.iees.twcore.constants.Configuration;

public class PrimaryTreeLabels implements Configuration {
	private static Set<String> labelSet = new HashSet<>();
	static {
		labelSet.add(N_SYSTEM);
		labelSet.add(N_DATADEFINITION);
		labelSet.add(N_DATAIO);
		labelSet.add(N_EXPERIMENT);
		labelSet.add(N_UI);		
	}
	public static boolean contains(String label) {
		return labelSet.contains(label);
	}

}
