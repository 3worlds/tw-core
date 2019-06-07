package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "ecosystem/dynamics/lifeCycle" node label in the 
 * 3Worlds configuration tree. Has no properties.
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class LifeCycle extends TreeGraphNode implements Initialisable {

	public LifeCycle(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_LIFECYCLE.initRank();
	}

}
