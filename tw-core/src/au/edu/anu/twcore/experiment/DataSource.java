package au.edu.anu.twcore.experiment;

import au.edu.anu.twcore.InitialisableNode;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * 
 * @author Jacques Gignoux - 10 juil. 2019
 *
 */
public class DataSource extends InitialisableNode {

	public DataSource(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DataSource(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_DATASOURCE.initRank();
	}

}
