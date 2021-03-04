package au.edu.anu.twcore.archetype.tw.old;

import au.edu.anu.rscs.aot.old.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.rscs.aot.old.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.old.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.Collection;
import java.util.HashSet;


/**
 * Check that if a SearchProcess refers to a Space, the the ComponentTypes processed by it will
 * implement the proper coordinates 
 * 
 * @author Jacques Gignoux - 26 févr. 2021
 *
 */
@Deprecated
public class SearchProcessConsistencyQuery extends Query {
	
	private String pname = "";
	private SpaceNode space = null;

	public SearchProcessConsistencyQuery() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a ProcessNode
		defaultProcess(input);
		ProcessNode proc = (ProcessNode) input;
		pname = proc.id();
		satisfied = true;
		boolean checkProcess = false;
		Collection<FunctionNode> funx = (Collection<FunctionNode>) get(proc.getChildren(),
			selectZeroOrMany(hasTheLabel(N_FUNCTION.label())));
		// look if the process is a SearchProcess (= has a relateToDecision function as a child)
		for (FunctionNode func:funx)
			if (func.properties().hasProperty(P_FUNCTIONTYPE.key()))
				if (func.properties().getPropertyValue(P_FUNCTIONTYPE.key())
					.equals(TwFunctionTypes.RelateToDecision))
					checkProcess = true;
		if (checkProcess) {
			// get the space of this ProcessNode
			space = (SpaceNode) get(proc.edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(E_SPACE.label())),
				endNode());
			if (space!=null) {
				// get the relation type of this SearchProcess
				Collection<RelationType> relt = (Collection<RelationType>) get(proc.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
					edgeListEndNodes());
				if (relt.size()>0) {
					RelationType rt = relt.iterator().next();
					// get the toCategories of the relation type
					Collection<Category> tocs = (Collection<Category>) get(rt.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())),
						edgeListEndNodes());
					// get the from categories of the relation type
					Collection<Category> fromcs = (Collection<Category>) get(rt.edges(Direction.OUT),
						selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())),
						edgeListEndNodes());
					Collection<SpaceNode> toSpaces = new HashSet<>();
					Collection<SpaceNode> fromSpaces = new HashSet<>();
					for (Category c:tocs) {
						// look for all fields in categories
						Collection<TreeGraphDataNode> records = (Collection<TreeGraphDataNode>) get(c.edges(Direction.OUT),
							edgeListEndNodes(),
							selectZeroOrMany(hasTheLabel(N_RECORD.label())));
						for (TreeGraphDataNode rec:records) 
							for (TreeNode field:rec.getChildren()){
							// look for fields which are referenced by a space
							Collection<SpaceNode> sp = (Collection<SpaceNode>) get(field.edges(Direction.IN),
								edgeListStartNodes(),
								selectZeroOrMany(hasTheLabel(N_SPACE.label())));
							// record the to-spaces
							toSpaces.addAll(sp);
						}
					}
					for (Category c:fromcs) {
						// look for all fields in categories
						Collection<TreeGraphDataNode> records = (Collection<TreeGraphDataNode>) get(c.edges(Direction.OUT),
							edgeListEndNodes(),
							selectZeroOrMany(hasTheLabel(N_RECORD.label())));
						for (TreeGraphDataNode rec:records) 
							for (TreeNode field:rec.getChildren()){
							// look for fields which are referenced by a space
							Collection<SpaceNode> sp = (Collection<SpaceNode>) get(field.edges(Direction.IN),
								edgeListStartNodes(),
								selectZeroOrMany(hasTheLabel(N_SPACE.label())));
							// record the from-spaces
							fromSpaces.addAll(sp);
						}
					}
					satisfied = (toSpaces.contains(space))&(fromSpaces.contains(space));
				}
			}
		}
		return this;
	}

	@Override
	public String toString() {
		String s = "[" + this.getClass().getName() 
			+ ": All componentTypes that can be processed by '" + pname 
			+ "' must have valid coordinates for space '" + space.id() + "']";
		return s;
	}

	
}
