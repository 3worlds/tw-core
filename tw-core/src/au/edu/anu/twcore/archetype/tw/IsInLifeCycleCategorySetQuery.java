package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.dynamics.LifeCycle;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import java.util.List;

/**
 * A Query to check that a categorized object's categories belong to a categorySet found in
 * its parent - designed for testing that recruit and produce nodes only deal with the categorySet
 * applicable to their parent lifecycle 
 * 
 * @author Jacques Gignoux - 11 sept. 2019
 *
 */
// tested, works ok. 11/9/2019
public class IsInLifeCycleCategorySetQuery extends Query {
	
	private String failedCat = "";

	public IsInLifeCycleCategorySetQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a recruit or produce node
		TreeGraphNode node = (TreeGraphNode) input;
		LifeCycle parent = (LifeCycle) node.getParent();
		CategorySet catset =  (CategorySet) get(parent.edges(Direction.OUT),
			selectOne(hasTheLabel(E_APPLIESTO.label())),
			endNode());
		List<Category> cats = (List<Category>) get(catset.getChildren(),
			selectOneOrMany(hasTheLabel(N_CATEGORY.label())));
		Category toCat = (Category) get(node.edges(Direction.OUT),
			selectOne(hasTheLabel(E_TOCATEGORY.label())),
			endNode());
		Category fromCat = (Category) get(node.edges(Direction.OUT),
			selectOne(hasTheLabel(E_FROMCATEGORY.label())),
			endNode());
		satisfied = cats.contains(fromCat);
		if (!satisfied)
			failedCat = fromCat.id();
		else {
			satisfied &= cats.contains(toCat);
			if (!satisfied)
				if (failedCat.isEmpty())
					failedCat = toCat.id();
				else
					failedCat += ","+toCat.id();
		}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + "'"+ failedCat +"' must be in the life cycle category set.]";
	}

}
