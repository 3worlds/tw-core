package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking a MultipleStoppingCondition to its component stopping conditionss
 * @author Jacques Gignoux - 14 juin 2019
 *
 */
public class ConditionEdge extends ALEdge {

	public ConditionEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
