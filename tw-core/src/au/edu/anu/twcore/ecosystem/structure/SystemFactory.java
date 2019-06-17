package au.edu.anu.twcore.ecosystem.structure;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.SystemComponent;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Factory;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "ecosystem/structure/component" node label in the 3Worlds configuration tree.
 * Factory for system components, ie the simulated items.
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class SystemFactory 
		extends InitialisableNode 
		implements Factory<SystemComponent> {

	public SystemFactory(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public SystemFactory(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_COMPONENT.initRank();
	}

	@Override
	public SystemComponent newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
