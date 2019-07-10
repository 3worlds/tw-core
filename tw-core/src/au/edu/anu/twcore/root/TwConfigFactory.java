package au.edu.anu.twcore.root;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.PropertyListFactory;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.properties.impl.ReadOnlyPropertyListImpl;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

/**
 * A factory for 3Worlds specifications - has predefined labels matching nodes and edges.
 * .makePropertyList returns an extendable property list.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class TwConfigFactory extends TreeGraphFactory {

	private static Map<String,String> twLabels = new HashMap<>();
	
	private static PropertyListFactory plf = new PropertyListFactory () {
		@Override
		public ReadOnlyPropertyList makeReadOnlyPropertyList(Property... properties) {
			return new ReadOnlyPropertyListImpl(properties);
		}
		@Override
		public ExtendablePropertyList makePropertyList(Property... properties) {
			return new ExtendablePropertyListImpl(properties);
		}
		@Override
		public ExtendablePropertyList makePropertyList(String... propertyKeys) {
			return new ExtendablePropertyListImpl(propertyKeys);
		}
	};
	
	public TwConfigFactory() {
		super("3worlds",twLabels);
	}

	public TwConfigFactory(String scopeName) {
		this();
	}

	public TwConfigFactory(String scopeName, Map<String, String> labels) {
		this();
	}

	// initialisation with the mapping of labels to nodes and edges
	static {
		for (ConfigurationNodeLabels key:ConfigurationNodeLabels.values())
			twLabels.put(key.label(), key.type().getName());
		for (ConfigurationEdgeLabels key:ConfigurationEdgeLabels.values())
			twLabels.put(key.label(), key.type().getName());
	}	
	
	@Override
	public PropertyListFactory nodePropertyFactory() {
		return plf;
	}
}
