package au.edu.anu.twcore.ecosystem.dynamics.initial;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.InitialisableNode;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * A class matching the "ecosystem/dynamics/.../variableValues" node of the 3W configuration tree.
 * 
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class VariableValues extends InitialisableNode {

	// default constructor
	public VariableValues(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public VariableValues(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_VARIABLEVALUES.initRank();
	}

}
