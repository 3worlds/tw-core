package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "initialiser" node label in the 3Worlds configuration tree.
 * Has no properties.
* 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class Initialiser extends TreeGraphNode implements Initialisable {

	public Initialiser(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_INITIALISER.initRank();
	}

}
