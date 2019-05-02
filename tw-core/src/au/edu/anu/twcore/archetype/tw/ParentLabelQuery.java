package au.edu.anu.twcore.archetype.tw;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.TreeNode;

/**
 * @author Jacques Gignoux - 6/9/2016
 * Constraint on a node's parent label
 *
 */
public class ParentLabelQuery extends Query {
	
	private List<String> labels = new LinkedList<String>();
	
	/**
	 * Use this constructor to test a set of labels. Argument in file
	 * must be an ObjectTable
	 * @param ot
	 */
	// Would StringTable work, actually ?
	public ParentLabelQuery(ObjectTable<?> ot) {
		super();
		for (int i=0; i<ot.size(); i++)
			labels.add((String)ot.getWithFlatIndex(i));
	}

	/**
	 * Use this constructor to test a single label
	 * @param s
	 */
	public ParentLabelQuery(String s) {
		labels.add(s);
	}
	
	@Override
	public Query process(Object input) { // input is a TreeNode
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		TreeNode parent = localItem.getParent();
		if (parent!=null)
			for (String label:labels) 
				if (parent.classId().equals(label)) {
					satisfied=true;
					break;
				}
		return this;
	}
	
	public String toString() {
//		return "[" + this.getClass().getSimpleName() + ", satisfied=" + satisfied 
//			+ ", labels = " + labels+ "]";
		return "[" + stateString() + " Parent label must be one of '" + labels.toString() + "']";
	}

}
