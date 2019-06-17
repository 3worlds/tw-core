package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/process/function" node label in the 
 * 3Worlds configuration tree. Has the user class name property or a way to generate this class
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class FunctionNode 
		extends InitialisableNode 
		implements Singleton<TwFunction> {

	public FunctionNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public FunctionNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_FUNCTION.initRank();
	}

	@Override
	public TwFunction getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
