package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Class matching the "experiment/treatment" node label in the 3Worlds configuration tree.
 * Has the "replicates" property.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class Treatment extends TreeGraphDataNode implements Initialisable {

	public Treatment(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public Treatment(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_TREATMENT.initRank();
	}

	public int replicates() {
		return (int) properties().getPropertyValue(P_TREATMENT_REPLICATES.key());
	}
	
	public String name() {
		return classId();
	}
	
}
