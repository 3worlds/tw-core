package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.dynamics.Produce;
import au.edu.anu.twcore.ecosystem.dynamics.Recruit;
import au.edu.anu.twcore.ecosystem.structure.Category;
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
// checked ok 24/9/2019
public class ValidLifeCycleProcessQuery extends Query {
	
	private String message = null;

	public ValidLifeCycleProcessQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a produce node
		defaultProcess(input);
		TwFunctionTypes requiredFunc = null;
		String s = null;
		if (input instanceof Produce) {
			requiredFunc = TwFunctionTypes.CreateOtherDecision; // category function, not relation
			s = "produce";
		}
		else if (input instanceof Recruit) {
			requiredFunc = TwFunctionTypes.ChangeCategoryDecision; // category function, not relation
			s = "recruit";
		}
		TreeGraphNode pnode = (TreeGraphNode) input;
		ProcessNode proc = (ProcessNode) get(pnode.edges(Direction.OUT),
			selectOne(hasTheLabel(E_EFFECTEDBY.label())),
			endNode());
		// 1 make sure the process categories contain the produce node one
		List<Node> apps = (List<Node>) get(proc.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
			edgeListEndNodes());
		Category fromprod = (Category) get(pnode.edges(Direction.OUT),
			selectOne(hasTheLabel(E_FROMCATEGORY.label())),
			endNode());
		if (apps.contains(fromprod))
			satisfied = true;
		else
			message = s+ " node fromCategory '"+fromprod.id()+"' not found in process '"+proc.id()+"'";
		// 2 make sure the process has a function of the proper type
		List<FunctionNode> funcs = (List<FunctionNode>) get(proc.getChildren(),
			selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
		for (FunctionNode func:funcs)
			if (func.properties().getPropertyValue(P_FUNCTIONTYPE.key())
				.equals(requiredFunc)) {
				satisfied &= true;
				break;
		}
		if ((message==null) && (!satisfied)) // means we didnt fall into the previous trap 
			message = "missing '"+requiredFunc+"' function type in process '"+proc.id()+"'";
		if (message==null)
			message = "checking "+s+" node category and function type";
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + message+"]";
	}

}
