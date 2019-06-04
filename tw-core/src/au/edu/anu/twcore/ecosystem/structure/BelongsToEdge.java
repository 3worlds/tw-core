package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
* Edge for linking system components to categories
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class BelongsToEdge extends ALEdge {

	public BelongsToEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
