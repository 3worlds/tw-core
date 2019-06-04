package au.edu.anu.twcore.ecosystem.runtime;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The second main runtime object, representing relations between System components
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class SystemRelation extends ALDataEdge {

	public SystemRelation(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory graph) {
		super(id, start, end, props, graph);
	}

}
