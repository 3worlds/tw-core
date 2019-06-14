package au.edu.anu.twcore.ui;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * A class matching the "userInterface/top" node of the 3WOrlds configuration
 * 
 * @author Jacques Gignoux - 14 juin 2019
 *
 */
public class TopPanel extends TreeGraphNode implements Initialisable {

	public TopPanel(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_UITOP.initRank();
	}

}
