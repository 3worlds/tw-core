package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The component matching the whole system
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ArenaComponent
		extends HierarchicalComponent {

	public ArenaComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// not very clean, but that's where the info is
	@Override
	public SetInitialStateFunction initialiser() {
		return ((ArenaFactory)membership()).setinit;
	}

}
