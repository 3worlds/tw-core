package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking category to decorators record
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class DecoratorEdge extends ALEdge {

	public DecoratorEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
