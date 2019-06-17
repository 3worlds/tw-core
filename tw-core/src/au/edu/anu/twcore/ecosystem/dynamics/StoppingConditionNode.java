package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * Class matching the "ecosystem/dynamics/stoppingCondition" node label in the 3Worlds configuration tree.
 * Has no properties. This <em>is</em> the simulator.
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class StoppingConditionNode 
		extends InitialisableNode 
		implements Singleton<StoppingCondition> {

	public StoppingConditionNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public StoppingConditionNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_STOPPINGCONDITION.initRank();
	}

	@Override
	public StoppingCondition getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
