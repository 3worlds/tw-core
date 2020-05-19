package au.edu.anu.twcore.predefined;

import au.edu.anu.twcore.InitialisableNode;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * The class matching the 'predefined' node in the specifiation tree.
 * Contains predefined classes only, ie this part of the graph is non-editable
 *
 * @author J. Gignoux - 19 mai 2020
 *
 */
public class Predefined extends InitialisableNode {

	// default constructor
	public Predefined(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Predefined(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_PREDEFINED.initRank();
	}

}
