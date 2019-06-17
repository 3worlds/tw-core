package au.edu.anu.twcore.ecosystem;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "ecosystem" node label in the 3Worlds configuration tree.
 * Has properties.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Ecosystem extends InitialisableNode {

	public Ecosystem(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public Ecosystem(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_SYSTEM.initRank();
	}

}
