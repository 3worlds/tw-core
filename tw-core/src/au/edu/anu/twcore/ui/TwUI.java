package au.edu.anu.twcore.ui;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "userInterface" node label in the 3Worlds configuration tree.
 * Has properties.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class TwUI extends TreeGraphDataNode implements Initialisable {

	public TwUI(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public TwUI(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_UI.initRank();
	}

}
