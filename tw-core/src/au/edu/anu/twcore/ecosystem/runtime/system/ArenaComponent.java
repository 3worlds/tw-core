package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.Resettable;

/**
 * The component matching the whole system
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ArenaComponent
		extends HierarchicalComponent
		implements Resettable {

	public ArenaComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// not very clean, but that's where the info is
	@Override
	public SetInitialStateFunction initialiser() {
		return ((ArenaFactory)membership()).setinit;
	}

	// Particular to arena as it is a singleton instance
	@Override
	public void preProcess() {
		// re-copy initial constants and drivers
		ArenaFactory fact = (ArenaFactory) membership();
		if (currentState()!=null)
			currentState().setProperties(fact.driverTemplate);
		if (constants()!=null)
			constants().setProperties(fact.lifetimeConstantTemplate);
		// re-run setInitialState method
		if (initialiser()!=null) {
			if (constants()!=null)
				constants().writeEnable();
			if (currentState()!=null)
				currentState().writeEnable();
			initialiser().setInitialState(null, null, null, null, this, null);
			if (constants()!=null)
				constants().writeDisable();
			if (currentState()!=null)
				currentState().writeDisable();
			initialiser().startEventQueues();
		}
	}

}
