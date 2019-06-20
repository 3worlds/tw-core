package au.edu.anu.twcore.ecosystem.structure.system;

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

	/** indexes to access state variable table */
	protected static int CURRENT = 1;
	protected static int NEXT = CURRENT - 1;
	protected static int PAST0 = CURRENT + 1;
	
	protected SystemComponent(Identity id, SimplePropertyList props, GraphFactory factory) {
		super(id, props, factory);
	}

}
