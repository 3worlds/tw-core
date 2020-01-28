package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Use this edge to relate a Process to a Space
 * 
 * @author Jacques Gignoux - 27 janv. 2020
 *
 */
public class ProcessSpaceEdge extends ALEdge {

	public ProcessSpaceEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
