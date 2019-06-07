package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking recruit or produce transition to processes
 * 
 * @author Jacques Gignoux - 6 juin 2019
 *
 */

public class EffectedByEdge extends ALEdge {

	public EffectedByEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
