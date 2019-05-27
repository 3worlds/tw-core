package au.edu.anu.twcore.root;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

/**
 * A factory for 3Worlds specifications - has predefined labels matching nodes.
 * .makePropertyList returns an extendable property list.
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
	
	@Override
	public ExtendablePropertyList makePropertyList(Property... properties) {
		return new ExtendablePropertyListImpl(properties);
	}
	
	@Override
	public ExtendablePropertyList makePropertyList(String... propertyKeys) {
		return new ExtendablePropertyListImpl(propertyKeys);
	}


	// initialisation with the mapping of labels to nodes
	static {
		for (ConfigurationNodeLabels key:ConfigurationNodeLabels.values())
			twLabels.put(key.label(), key.type().getName());
	}	
}
