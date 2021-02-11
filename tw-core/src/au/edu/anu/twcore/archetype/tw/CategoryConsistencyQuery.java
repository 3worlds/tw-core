package au.edu.anu.twcore.archetype.tw;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * <p>A Query to make sure that,</p> 
 * <ol>
 * <li>when a Process applies to categories that define data (drivers,
 * constants or decorators), then there is at least one ElementType node that belongs to these
 * categories;</li>
 * 
 * <li>[TODO:]the categories it refers to do not belong to different element types (???)</li>
 * </ol> 
 * 
 * @author Jacques Gignoux - 11 févr. 2021
 *
 */
public class CategoryConsistencyQuery extends Query {
	
	private String message = "CategoryConsistencyQuery.process() not run";
	
	public CategoryConsistencyQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a ProcessNode
		defaultProcess(input);
		ProcessNode proc = (ProcessNode) input;
		// CAUTION! relationProcess!
		List<TreeGraphDataNode> ltgn =  (List<TreeGraphDataNode>) get(proc.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
			edgeListEndNodes());
		// get all process categories
		Set<Category> catList = new HashSet<>();
		if (!ltgn.isEmpty()) {
			if (ltgn.get(0) instanceof Category) {
				catList.addAll((Collection<Category>) get(proc.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_APPLIESTO.label())),
					edgeListEndNodes()));
			}
			else if (ltgn.get(0) instanceof RelationType) {
				RelationType rel = (RelationType) ltgn.get(0);
				catList.addAll((Collection<Category>) get(rel.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_TOCATEGORY.label())),
					edgeListEndNodes()));
				catList.addAll((Collection<Category>) get(rel.edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_FROMCATEGORY.label())),
					edgeListEndNodes()));
			}
		}
		// get the list of categories that have data
		List<Category> categoriesWithData = new LinkedList<>();
		for (Category c:catList) {
			List<Record> crecs = (List<Record>) get(c.edges(Direction.OUT),
				edgeListEndNodes(),
				selectZeroOrMany(hasTheLabel(N_RECORD.label())));
			if (!crecs.isEmpty())
				categoriesWithData.add(c);
		}
		// check that at least one ElementType belongs to one of the categories with data
		boolean result = true;
		for (Category c:categoriesWithData) {
			Collection<Edge> belongers = (Collection<Edge>) get(c.edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())));
			if (belongers.isEmpty())
				result = false;
		}
		satisfied = result;
		if (!satisfied)
			message = "A Category process '"+proc.id()+"' applies must have a 'belongsTo' edge to an ElementType";
		return this;
	}
	
	public String toString() {
		return "[" + stateString() + " " + message + "]";
	}

}
