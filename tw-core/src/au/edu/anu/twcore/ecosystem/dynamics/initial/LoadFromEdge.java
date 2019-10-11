package au.edu.anu.twcore.ecosystem.dynamics.initial;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge linking initial state, group or individual node to dataSource 
 * 
 * @author Jacques Gignoux - 9 oct. 2019
 *
 */
public class LoadFromEdge extends ALEdge {

	public LoadFromEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}