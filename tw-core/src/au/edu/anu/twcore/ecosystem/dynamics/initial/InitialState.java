package au.edu.anu.twcore.ecosystem.dynamics.initial;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * A class matching the "ecosystem/dynamics/initialState" node of the 3w configuration
 * @author Jacques Gignoux - 17 juin 2019
 *
 */
public class InitialState extends InitialisableNode {

	// default constructor
	public InitialState(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public InitialState(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_INITIALSTATE.initRank();
	}

}
