package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "experiment/treatment/modelChange" node label in the 3Worlds configuration tree.
 * Has the "parameter" and "replaceWith" properties.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class ModelChange extends InitialisableNode {

	public ModelChange(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public ModelChange(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_MODELCHANGE.initRank();
	}

	public String parameter() {
		return (String) properties().getPropertyValue(P_MODELCHANGE_PARAMETER.key());
	}
	
	public DoubleTable replaceWith() {
		return (DoubleTable) properties().getPropertyValue(P_MODELCHANGE_REPLACEWITH.key());
	}
}
