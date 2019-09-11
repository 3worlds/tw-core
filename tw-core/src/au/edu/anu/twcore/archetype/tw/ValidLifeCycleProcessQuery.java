package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.dynamics.Produce;
import au.edu.anu.twcore.ecosystem.dynamics.Recruit;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import java.util.List;

/**
 * checks that a process associated to a produce or recruit node (in a life cycle) is valid, ie
 * acts on the required categories and has a function of the createOtherDecisionFunction 
 * or changeCategoryDecisionFunction class
 * 
 * @author Jacques Gignoux - 11 sept. 2019
 *
 */
// checked ok 11/9/2019
public class ValidLifeCycleProcessQuery extends Query {

	public ValidLifeCycleProcessQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a produce node
		TwFunctionTypes requiredFunc = null;
		if (input instanceof Produce) {
			requiredFunc = TwFunctionTypes.CreateOtherDecision;
		}
		else if (input instanceof Recruit)
			requiredFunc = TwFunctionTypes.ChangeCategoryDecision;
		TreeGraphNode pnode = (TreeGraphNode) input;
		ProcessNode proc = (ProcessNode) get(pnode.edges(Direction.OUT),
			selectOne(hasTheLabel(E_EFFECTEDBY.label())),
			endNode());
		List<Node> apps = (List<Node>) get(proc.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
			edgeListEndNodes());
		if (apps.size()==1) {
			Node n = apps.get(0);
			if (n instanceof RelationType) {
				RelationType rel = (RelationType) n;
				// 1 make sure the process to and from categories contain the produce node ones
				List<Category> fromrel = (List<Category>) get(rel.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())),
					edgeListEndNodes());
				List<Category> torel = (List<Category>) get(rel.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
					edgeListEndNodes());
				Category fromprod = (Category) get(pnode.edges(Direction.OUT),
					selectOne(hasTheLabel(E_FROMCATEGORY.label())),
					endNode());
				Category toprod = (Category) get(pnode.edges(Direction.OUT),
					selectOne(hasTheLabel(E_TOCATEGORY.label())),
					endNode());
				// 2 make sure the process has a function of the proper type
				List<FunctionNode> funcs = (List<FunctionNode>) get(proc.getChildren(),
					selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
				boolean ok = false;
				for (FunctionNode func:funcs)
					if (func.properties().getPropertyValue(P_FUNCTIONTYPE.key())
						.equals(requiredFunc)) {
						ok=true;
						break;
					}
				satisfied = (ok && fromrel.contains(fromprod) && torel.contains(toprod));
			}
			else satisfied = false; // it's a component process, we need a relation process
		}
		else satisfied = false; // its a mistake or a component process
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + " Invalid process (relation or function type) for lifeCycle produce node]";
	}

}
