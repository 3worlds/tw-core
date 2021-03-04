package au.edu.anu.twcore.archetype.tw.old;

import au.edu.anu.rscs.aot.old.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.root.World;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>A class to check that processes applying to permanent objects do not have functions
 * only compatible with ephemeral objects. These are:</p>
 * <dl>
 * <dt>DeleteDecision</dt>
 * <dd>Only ephemeral components can be deleted</dd>
 * <dt>ChangeCategoryDecision</dt>
 * <dd>Only ephemeral components can change categories</dd>
 * <dt>CreateOtherDecision</dt>
 * <dd>Only ephemeral components can be created (but the creator may be permanent)</dd>
 * </dl>
 * 
 *
 * @author J. Gignoux - 13 nov. 2020
 *
 */
@Deprecated
public class LifespanFunctionCompatibilityQuery extends Query {

	private String message = "";

	public LifespanFunctionCompatibilityQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	private void checkComponentTypes(List<Category> procCats, 
			Category ceph,
			TreeNode root,
			ProcessNode pn,
			FunctionNode fn) {
		// if ephemeral category is present in process categories, everything is ok
		if (!procCats.contains(ceph)){
			// otherwise must search componentTypes
			List<ComponentType> lct = (List<ComponentType>) get(root.subTree(),
				selectZeroOrMany(hasTheLabel(N_COMPONENTTYPE.label())));
			for (ComponentType ct:lct) {
				// get componentType categories
				List<Category> lcct = (List<Category>) get(ct.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())),
					edgeListEndNodes());
				// if componentType categories that may use this process
				if (lcct.containsAll(procCats))
					// check the componentType is ephemeral
					if (!lcct.contains(ceph)) {
						satisfied = false;
						message = "ComponentType '"+ct.id() +
							"' is not ephemeral but may be processed by function '"+ fn.id()+
							"' of process '" + pn.id()+
							"' that only works on ephemeral ComponentTypes\n";
					}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a ProcessNode
		defaultProcess(input);
		ProcessNode pn = (ProcessNode) input;
		satisfied = true;
		// get the *ephemeral* category
		TreeNode root = World.getRoot(pn);
		Category ceph = (Category) get(root.subTree(),selectZeroOrOne(
			andQuery(hasTheLabel(N_CATEGORY.label()),hasTheName(Category.ephemeral))));
		if (ceph!=null) {
			// get all function types of this ProcessNode 		
			for (FunctionNode fn:(List<FunctionNode>) get(pn.getChildren(),
				selectZeroOrMany(hasTheLabel(N_FUNCTION.label())))) 
				if (fn.properties().hasProperty(P_FUNCTIONTYPE.key())) {
					List<Category> procCats = new LinkedList<>();
					// case 1: deleteDecision: componentTypes to which this process may
					// apply must all be of category *ephemeral* even if the process categories
					// do not contain *ephemeral*
					if (TwFunctionTypes.DeleteDecision.equals(fn.properties()
							.getPropertyValue(P_FUNCTIONTYPE.key()))) {
						procCats = (List<Category>) get(pn.edges(Direction.OUT),
							selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
							edgeListEndNodes());
						checkComponentTypes(procCats,ceph,root,pn,fn);
					}
					// case 2: createotherDecision: componentTypes to create (according to lifecycle)
					// must all be of category *ephemeral*
					if (TwFunctionTypes.CreateOtherDecision.equals(fn.properties()
							.getPropertyValue(P_FUNCTIONTYPE.key()))) {
						// check for life cycle spec
						List<TreeNode> prods = (List<TreeNode>) get(fn.edges(Direction.IN),
							selectZeroOrMany(hasTheLabel(E_EFFECTEDBY.label())),
							edgeListStartNodes(),
							selectZeroOrMany(hasTheLabel(N_PRODUCE.label())));
						// no life cycle: 
						if (prods.isEmpty())
							 procCats = (List<Category>) get(pn.edges(Direction.OUT),
								selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
								edgeListEndNodes());
						// life cycle: get the product categories
						else if (prods.size()==1)
							procCats = (List<Category>) get(prods.get(0).edges(Direction.OUT),
								selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())),
								edgeListEndNodes());
						checkComponentTypes(procCats,ceph,root,pn,fn);
					}
					// case3: both componentType to recruit from and to recruit to must
					// belong to category *ephemeral* even if the process doesnt specify so
					if (TwFunctionTypes.ChangeCategoryDecision.equals(fn.properties()
							.getPropertyValue(P_FUNCTIONTYPE.key())) ) {
						// check that recruiting componentTypes are ephemeral
						procCats = (List<Category>) get(pn.edges(Direction.OUT),
							selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
							edgeListEndNodes());
						String msg = message;
						checkComponentTypes(procCats,ceph,root,pn,fn);
						// check that recruited componentTypes are ephemeral
						List<TreeNode> recs = (List<TreeNode>) get(fn.edges(Direction.IN),
							selectZeroOrMany(hasTheLabel(E_EFFECTEDBY.label())),
							edgeListStartNodes(),
							selectZeroOrMany(hasTheLabel(N_RECRUIT.label())));
						if (recs.size()==1)
							procCats = (List<Category>) get(recs.get(0).edges(Direction.OUT),
								selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())),
								edgeListEndNodes());
						checkComponentTypes(procCats,ceph,root,pn,fn);
						message = msg+message;
					}
			}
		}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + message+"]";
	}

}
