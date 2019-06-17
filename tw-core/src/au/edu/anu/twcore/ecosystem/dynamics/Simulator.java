package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "ecosystem/dynamics" node label in the 3Worlds configuration tree.
 * Has no properties. This <em>is</em> the simulator.
 * 
 * TODO: this class is a first version, one must add the simulation capability later, probably
 * through another interface
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Simulator extends InitialisableNode {

	public Simulator(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public Simulator(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_DYNAMICS.initRank();
	}

}
