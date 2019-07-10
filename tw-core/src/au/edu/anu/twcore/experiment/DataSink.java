package au.edu.anu.twcore.experiment;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_DATASINK;

import au.edu.anu.twcore.InitialisableNode;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * 
 * @author Jacques Gignoux - 10 juil. 2019
 *
 */
public class DataSink extends InitialisableNode {

	public DataSink(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DataSink(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_DATASINK.initRank();
	}

}
