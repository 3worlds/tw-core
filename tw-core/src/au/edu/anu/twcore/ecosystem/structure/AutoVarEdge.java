package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge to link to automatic variable definitions
 *
 * @author J. Gignoux - 21 avr. 2020
 *
 */
public class AutoVarEdge extends ALEdge {

	public AutoVarEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
