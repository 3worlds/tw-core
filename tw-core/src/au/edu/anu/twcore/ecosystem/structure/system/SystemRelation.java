package au.edu.anu.twcore.ecosystem.structure.system;

import au.edu.anu.twcore.ecosystem.runtime.Related;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The second main runtime object, representing relations between System components.
 * No properties at the moment.
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class SystemRelation extends ALDataEdge {
	
	private Related relation = null;

	public SystemRelation(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory graph) {
		super(id, start, end, props, graph);
	}
	
	public void setRelated(Related rel) {
		if (relation==null)
			relation = rel;
	}
	
	public Related membership() {
		return relation;
	}

}
