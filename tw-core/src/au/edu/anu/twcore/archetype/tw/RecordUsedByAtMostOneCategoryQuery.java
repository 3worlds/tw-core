package au.edu.anu.twcore.archetype.tw;

import java.util.ArrayList;
import java.util.Collection;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.data.Record;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.ALEdge;

/**
 * Check that a root record is used by at most one category and for at most one usage among
 * {autoVar, decorators, drivers, constants}.
 * 
 * @author Jacques Gignoux - 9 févr. 2021
 *
 */
public class RecordUsedByAtMostOneCategoryQuery extends Query {
	
	private Collection<String> edgeLabels = new ArrayList<>(4); 
	private int nEdges = 0;
	private String recname;
	
	public RecordUsedByAtMostOneCategoryQuery(StringTable params) {
		super();
		for (int i=0; i<params.size(); i++)
			edgeLabels.add(params.getWithFlatIndex(i));
	}

	@Override
	public Query process(Object input) { // input is a Record root node
		defaultProcess(input);
		Record record = (Record) input;
		recname = record.id();
		for (ALEdge e:record.edges(Direction.IN))
			if (edgeLabels.contains(e.classId()))
				nEdges++;
		satisfied = (nEdges<=1);
		return this;
	}
	
	public String toString() {
		return "[" + this.getClass().getName() +
			": Record '" + recname + "' must be used by at most one Category. Found "+ nEdges +"]";
	}

}
