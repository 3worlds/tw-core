package au.edu.anu.twcore.ecosystem.runtime;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.ALDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The main runtime object in 3worlds, representing "individuals" or "agents" or "system
 * components".
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class SystemComponent extends ALDataNode {

	public SystemComponent(Identity id, SimplePropertyList props, GraphFactory factory) {
		super(id, props, factory);
	}

}
