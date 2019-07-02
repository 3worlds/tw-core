package au.edu.anu.twcore.ecosystem.dynamics.initial;

import au.edu.anu.twcore.InitialisableNode;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * A class matching the "ecosystem/dynamics/initialState/group" node of the 3w configuration
 * 
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class Group extends InitialisableNode {

	// default constructor
	public Group(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Group(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_GROUP.initRank();
	}

}
