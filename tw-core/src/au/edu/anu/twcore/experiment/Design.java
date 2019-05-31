package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.ExperimentDesignType;
import fr.ens.biologie.generic.Initialisable;
import fr.ens.biologie.generic.Resettable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Class matching the "experiment/design" node label in the 3Worlds configuration tree.
 * Has the "type" or "file" property.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class Design extends TreeGraphDataNode implements Initialisable, Resettable {

	private ExperimentDesignType type = null;
	private String fileName = null;
	
	public Design(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		reset();
	}

	public Design(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		// todo: load the design file
	}

	@Override
	public int initRank() {
		return N_DESIGN.initRank();
	}

	public ExperimentDesignType type() {
		return type;
	}
	
	public String file() {
		return fileName;
	}

	@Override
	public void reset() {
		if (properties().hasProperty(P_DESIGN_TYPE.key()))
			type = (ExperimentDesignType) properties().getPropertyValue(P_DESIGN_TYPE.key());
		else if (properties().hasProperty(P_DESIGN_FILE.key()))
			fileName = (String)properties().getPropertyValue(P_DESIGN_FILE.key());
	}
	
}
