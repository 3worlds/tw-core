package au.edu.anu.twcore.root;

import java.util.HashMap;
import java.util.Map;

import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

/**
 * A factory for 3Worlds specifications - has predefined labels matching nodes
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class TwConfigFactory extends TreeGraphFactory {

	private static Map<String,String> twLabels = new HashMap<>();
	
	public TwConfigFactory() {
		super("3worlds",twLabels);
	}

	public TwConfigFactory(String scopeName) {
		this();
	}

	public TwConfigFactory(String scopeName, Map<String, String> labels) {
		this();
	}

	static {
		for (ConfigurationNodeLabels key:ConfigurationNodeLabels.values())
			twLabels.put(key.label(), key.type().getName());
	}
}
