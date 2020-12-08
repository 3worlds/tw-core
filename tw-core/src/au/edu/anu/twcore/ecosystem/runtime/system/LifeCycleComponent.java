package au.edu.anu.twcore.ecosystem.runtime.system;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleComponent extends HierarchicalComponent {

	public LifeCycleComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	@Override
	public LifeCycleFactory elementFactory() {
		return (LifeCycleFactory) membership();
	}

	public String name() {
		return content().id();
	}

}
