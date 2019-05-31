package au.edu.anu.twcore.data;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class Record extends TreeGraphNode implements Initialisable {

	public Record(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_RECORD.initRank();
	}

}
