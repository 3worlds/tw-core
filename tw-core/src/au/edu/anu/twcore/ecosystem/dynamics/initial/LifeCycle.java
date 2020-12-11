package au.edu.anu.twcore.ecosystem.dynamics.initial;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycle
		extends InitialisableNode
		implements Sealable, LimitedEdition<LifeCycleComponent> {

	public LifeCycle(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public LifeCycle(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public int initRank() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Sealable seal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSealed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LifeCycleComponent getInstance(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
