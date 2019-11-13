package au.edu.anu.twcore.ui;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * 
 * @author Jacques Gignoux - 13 nov. 2019
 *
 */
public class TrackPopulationEdge extends ALDataEdge {

	public TrackPopulationEdge(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory graph) {
		super(id, start, end, props, graph);
	}

	public TrackPopulationEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, new ExtendablePropertyListImpl(), graph);
	}

}
