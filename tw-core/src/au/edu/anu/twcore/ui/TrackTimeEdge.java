package au.edu.anu.twcore.ui;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking a TimeDisplayWidget to its prey, ie usually a simulator.
 * 
 * @author Jacques Gignoux - 5 sept. 2019
 *
 */
public class TrackTimeEdge extends ALEdge {

	public TrackTimeEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
