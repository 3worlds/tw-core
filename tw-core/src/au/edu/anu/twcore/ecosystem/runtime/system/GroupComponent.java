package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.ecosystem.runtime.containers.Containing;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class GroupComponent
		extends HierarchicalComponent
		implements Containing<ComponentContainer> {

	private ComponentContainer content = null;

	public GroupComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	/**
	 * CAUTION: can be set only once, ideally just after construction
	 */
	@Override
	public void setContent(ComponentContainer container) {
		if (content==null)
			content = container;
	}

	@Override
	public ComponentContainer content() {
		return content;
	}


}
