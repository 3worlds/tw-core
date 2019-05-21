package au.edu.anu.twcore.archetype.tw;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

/**
 * @author Jacques Gignoux - 6/9/2016 Constraint on a node's parent class
 *
 */
public class ParentClassQuery extends Query {

	private List<String> klasses = new LinkedList<String>();

	public ParentClassQuery(ObjectTable<?> ot) {
		super();
		for (int i = 0; i < ot.size(); i++)
			klasses.add((String) ot.getWithFlatIndex(i));
	}

	public ParentClassQuery(String s) {
		klasses.add(s);
	}

	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		Node parent = (Node) localItem.getParent();
		ReadOnlyDataHolder parentProps = (ReadOnlyDataHolder) parent;
		if (parent != null && parentProps.properties().hasProperty("isOfClass")) {
			String pKlass = (String) parentProps.properties().getPropertyValue("isOfClass");
			for (String klass : klasses)
				if (pKlass.equals(klass)) {
					satisfied = true;
					break;
				}
		}
		return this;
	}

	public String toString() {
		// return "[" + this.getClass().getSimpleName() + ", satisfied=" + satisfied
		// + ", labels = " + labels+ "]";
		return "[" + stateString() + " Parent  must have class one of '" + klasses.toString() + "']";
	}

}
