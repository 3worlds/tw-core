package au.edu.anu.twcore.ecosystem.runtime.system;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class GroupComponent
		extends HierarchicalComponent {

	public GroupComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

}
