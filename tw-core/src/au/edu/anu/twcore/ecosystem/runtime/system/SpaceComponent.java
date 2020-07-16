package au.edu.anu.twcore.ecosystem.runtime.system;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.ALDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * A class to store the data attached to a space so that they can be tracked and displayed
 * @author J. Gignoux - 15 juil. 2020
 *
 */
public class SpaceComponent extends ALDataNode implements DataElement {

	public SpaceComponent(Identity id, SimplePropertyList props, GraphFactory factory) {
		super(id, props, factory);
	}


}
