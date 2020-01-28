package au.edu.anu.twcore.ecosystem.structure;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public class SpaceNode extends InitialisableNode implements LimitedEdition<Space> {

	public SpaceNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public SpaceNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public int initRank() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Space getInstance(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
