package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * A class to represent containers as system components. These are TreeGraphDataNodes, ie
 * they have children and parents through the hierarchy relation
 *
 * @author J. Gignoux - 16 avr. 2020
 *
 */

// The group parameters are actually this class lifetime constants !

public abstract class HierarchicalComponent
		extends TreeGraphDataNode
		implements CategorizedComponent {

	private Categorized<? extends CategorizedComponent> cats = null;

	public HierarchicalComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	@Override
	public Categorized<? extends CategorizedComponent> membership() {
		return cats;
	}

	@Override
	public void setCategorized(Categorized<? extends CategorizedComponent> cats) {
		if (cats==null)
			this.cats = cats;

	}

}
