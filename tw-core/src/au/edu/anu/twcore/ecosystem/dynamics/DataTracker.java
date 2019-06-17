package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/process/dataTracker" node label in the 
 * 3Worlds configuration tree. Had many properties but needs refactoring.
 *  
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class DataTracker extends InitialisableNode {

	public DataTracker(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DataTracker(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_DATATRACKER.initRank();
	}

}
