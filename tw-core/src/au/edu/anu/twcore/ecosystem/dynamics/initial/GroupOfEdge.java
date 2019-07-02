package au.edu.anu.twcore.ecosystem.dynamics.initial;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking group to component nodes in initialState
 * 
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class GroupOfEdge extends ALEdge {

	protected GroupOfEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
