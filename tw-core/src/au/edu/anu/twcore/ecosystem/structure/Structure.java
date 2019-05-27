package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "ecosystem/structure" node label in the 3Worlds configuration tree.
 * Has no properties.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Structure extends TreeGraphNode implements Initialisable {

	public Structure(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_STRUCTURE.initRank();
	}

}
