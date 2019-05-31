package au.edu.anu.twcore.data;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking tables to dimensioners
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class SizedByEdge extends ALEdge {

	public SizedByEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
