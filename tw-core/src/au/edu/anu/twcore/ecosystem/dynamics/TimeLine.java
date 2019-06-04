package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Time line common to all time models within a simulation. Internally time is
 * represented as longs, simulation start occurs at t=0 and one tick matches one
 * timeGrain.
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class TimeLine extends TreeGraphDataNode implements Initialisable {

	public TimeLine(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public TimeLine(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_TIMELINE.initRank();
	}

}
