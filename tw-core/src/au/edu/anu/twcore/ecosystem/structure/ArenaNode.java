package au.edu.anu.twcore.ecosystem.structure;

import au.edu.anu.twcore.InitialisableNode;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_ARENA;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public class ArenaNode extends InitialisableNode {

	public ArenaNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public ArenaNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public int initRank() {
		return N_ARENA.initRank();
	}

}
