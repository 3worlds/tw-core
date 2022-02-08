package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

/**
 * Check the presence of a certain property depending on parent type.
 * 
 * @author Jacques Gignoux - 8 févr. 2022
 *
 */
public class PropertyMatchParentQuery extends QueryAdaptor {
	
	private String parentClass = null;
	private String propName = null;
	
	/**
	 * Constructor
	 * 
	 * @param args a StringTable of dimension 2: 1st value is the parent class name, 2nd value
	 * is the required matching property in the child node
	 */
	public PropertyMatchParentQuery(StringTable args) {
		super();
		if (args.size()==2) {
			parentClass = args.getWithFlatIndex(0);
			propName = args.getWithFlatIndex(1);
		}
		else
			throw new TwcoreException("Archetype error: PropertyMatchParentQuery requires a StringTable[2] argument");
	}

	@Override
	public Queryable submit(Object input) { // input is a child node
		initInput(input);
		if (input instanceof TreeNode) {
			TreeNode tn = (TreeNode) input;
			TreeNode parent = tn.getParent();
			if (parent!=null) // only possible if tree is broken due to edition
				if (parent.classId().equals(parentClass)) {
					if (tn instanceof ReadOnlyDataHolder) {
						if (((ReadOnlyDataHolder)tn).properties().hasProperty(propName))
							return this;
				}
				actionMsg = "Add the '"+propName+"' property to node '"+tn.toString()+"'.";
				errorMsg = "Node '"+tn.toString()+"' requires a '"+propName+"' property to match its '"
					+parent.toString()+"' parent.";
			}
		}
		return this;
	}

}
