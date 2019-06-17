package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/Process" node label in the 
 * 3Worlds configuration tree. Has no properties. 
 * 
 * @author Jacques Gignoux - 6 juin 2019
 *
 */
public class ProcessNode 
		extends InitialisableNode 
		implements Singleton<TwProcess> {

	// default constructor
	public ProcessNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public ProcessNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_PROCESS.initRank();
	}

	@Override
	public TwProcess getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
