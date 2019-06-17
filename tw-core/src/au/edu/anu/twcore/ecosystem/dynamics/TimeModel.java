package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel" node label in the 3Worlds configuration tree.
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class TimeModel extends InitialisableNode implements Singleton<Timer> {

	public TimeModel(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public TimeModel(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_TIMEMODEL.initRank();
	}

	@Override
	public Timer getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
