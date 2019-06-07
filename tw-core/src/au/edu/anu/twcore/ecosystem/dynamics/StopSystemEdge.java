package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking ValueStoppingCondition to a System component
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class StopSystemEdge extends ALEdge {

	public StopSystemEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
