package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.ElementType;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.List;

/**
 * A class to check that processes applying to permanent objects do not have functions
 * only compatible with ephemeral objects
 *
 * @author J. Gignoux - 13 nov. 2020
 *
 */
public class LifespanFunctionCompatibilityQuery extends Query {

	private String message = "";

	public LifespanFunctionCompatibilityQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a ProcessNode
		defaultProcess(input);
		ProcessNode pn = (ProcessNode) input;
		// get all categories OR relationTypes this process applies to
		List<Node> apps = (List<Node>) get(pn.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
			edgeListEndNodes());
		// get all function types of this ProcessNode
		List<FunctionNode> lfn = (List<FunctionNode>) get(pn.getChildren(),
			selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
		satisfied = true;
		for (Node n:apps) {
			// if it's a categoryProcess,
			if (n instanceof Category) {
				// get all ComponentTypes that belong to these categories
				List<ElementType<?,?>> lct = (List<ElementType<?,?>>) get(n.edges(Direction.IN),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())),
					edgeListStartNodes());
				// for each component, checks that it belongs to categories ephemeral or permanent
				for (ElementType<?,?> ct:lct) {
					Category c = (Category) get(ct.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())),
						edgeListEndNodes(),
						selectZeroOrOne(orQuery(
							hasTheName(Category.ephemeral),
							hasTheName(Category.permanent))));
					if (c==null) {
						satisfied = false;
						message = "missing life span for ComponentType "+ct.id();
						break;
					}
					else {
						// check that lifespan is compatible with functions
						for(FunctionNode fn:lfn) {
							TwFunctionTypes ftype = (TwFunctionTypes) fn.properties()
								.getPropertyValue(P_FUNCTIONTYPE.key());
							if (((ftype==TwFunctionTypes.DeleteDecision) ||
								(ftype==TwFunctionTypes.CreateOtherDecision)))
								// TODO: what about ChangeCategoryDecision?
								if (c.id().equals(Category.permanent)) {
									satisfied = false;
									message = "Function " + fn.id()
										+ " of type " + ftype
										+ " incompatible with ComponentType "+ct.id()
										+ " "+Category.permanent+ " life span";
									break;
								}
						}
					}
				}

			}
			// if it's a relationProcess,
			else if (n instanceof RelationType) {
				// TODO: no maintainrel for permanent relations
			}
		}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + message+"]";
	}

}
