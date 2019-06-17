package au.edu.anu.twcore.data;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "field" node label in the 3Worlds configuration tree.
 * Has the "type" property.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class Field extends InitialisableNode {

	public Field(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_FIELD.initRank();
	}

	public String name() {
		return classId();
	}
	
	public String type() {
		return (String) properties().getPropertyValue(P_FIELD_TYPE.key());
	}
	
}
