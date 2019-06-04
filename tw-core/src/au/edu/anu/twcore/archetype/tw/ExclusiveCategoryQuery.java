package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.LinkedList;
import java.util.List;


/**
 * A Query to check that a system component only belongs to one category of a given
 * category set (categories within a set are assumed exclusive) 
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class ExclusiveCategoryQuery extends Query {

	private CategorySet failedCategorySet = null;
	
	public ExclusiveCategoryQuery() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a node with out edges to categories
		defaultProcess(input);
		Node localItem = (Node) input;
		Iterable<Category> cats = (Iterable<Category>) get(localItem.edges(Direction.OUT),
			edgeListEndNodes(),
			selectOneOrMany(hasTheLabel(N_CATEGORY.label())));
		satisfied = true;
		List<CategorySet> csl = new LinkedList<>();
		for (Category c:cats) {
			CategorySet cs = (CategorySet) c.getParent();
			if (!csl.contains(cs))
				csl.add(cs);
			else {
				satisfied = false;
				failedCategorySet = cs;
				break;
			}
		}
		return this;
	}
	
	@Override
	public String toString() {
		return "[" + stateString()
			+ " Node cannot belong to two categories of the same set. Two categories of set '"
			+ failedCategorySet.id() + "' found"
			+ "]";
	}

}
