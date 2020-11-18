package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 *
 * @author J. Gignoux - 18 nov. 2020
 *
 */
public class CoordinateMappingEdge extends ALDataEdge {

	public CoordinateMappingEdge(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory graph) {
		super(id, start, end, props, graph);
	}

	public CoordinateMappingEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end,  new ExtendablePropertyListImpl(), graph);
	}


}
