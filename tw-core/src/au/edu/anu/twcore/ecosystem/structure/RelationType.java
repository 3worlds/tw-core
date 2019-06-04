package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Initialisable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.ecosystem.runtime.SystemRelation;

/**
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class RelationType 
		extends TreeGraphDataNode 
		implements Initialisable, Factory<SystemRelation> {

	public RelationType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public RelationType(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_RELATIONTYPE.initRank();
	}

	@Override
	public SystemRelation newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
