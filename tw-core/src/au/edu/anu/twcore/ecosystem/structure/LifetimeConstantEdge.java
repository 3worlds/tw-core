package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking category t lifetime constants record
 *
 * @author Jacques Gignoux - 2 avril 2020
 *
 */
public class LifetimeConstantEdge extends ALEdge {

	public LifetimeConstantEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
