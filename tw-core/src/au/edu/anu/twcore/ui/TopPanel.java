package au.edu.anu.twcore.ui;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * A class matching the "userInterface/top" node of the 3WOrlds configuration
 * 
 * @author Jacques Gignoux - 14 juin 2019
 *
 */
public class TopPanel extends InitialisableNode {

	// default constructor
	public TopPanel(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	// constructor with no properties
	public TopPanel(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_UITOP.initRank();
	}

}
