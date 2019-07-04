package au.edu.anu.twcore.archetype;

import java.util.HashSet;
import java.util.Set;

import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

public class PrimaryTreeLabels {
	private static Set<String> labelSet = new HashSet<>();
	static {
		labelSet.add(ConfigurationNodeLabels.N_SYSTEM.label());
		labelSet.add(ConfigurationNodeLabels.N_DATADEFINITION.label());
		labelSet.add(ConfigurationNodeLabels.N_EXPERIMENT.label());
		labelSet.add(ConfigurationNodeLabels.N_UI.label());		
	}
	public static boolean contains(String label) {
		return labelSet.contains(label);
	}

}
