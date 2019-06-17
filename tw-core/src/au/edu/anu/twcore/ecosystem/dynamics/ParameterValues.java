package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * A class matching the "ecosystem/dynamics/parameterValues" node of the 3W configuration tree.
 * 
 * @author Jacques Gignoux - 17 juin 2019
 *
 */
public class ParameterValues extends TreeGraphNode implements Initialisable {

	public ParameterValues(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_PARAMETERVALUES.initRank();
	}

}
