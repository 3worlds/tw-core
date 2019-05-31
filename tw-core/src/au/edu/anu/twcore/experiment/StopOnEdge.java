package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge linking timePeriod to a stoppingCondition
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class StopOnEdge extends ALEdge {

	public StopOnEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
