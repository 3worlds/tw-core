package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.containers.Containing;
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
		implements CategorizedComponent<ComponentContainer>, Containing<ComponentContainer> {

	protected Categorized<? extends CategorizedComponent<ComponentContainer>> categories = null;
	private ComponentContainer content = null;

	public HierarchicalComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	@Override
	public Categorized<? extends CategorizedComponent<ComponentContainer>> membership() {
		return categories;
	}

	/**
	 * CAUTION: can be set only once, ideally just after construction
	 */
	@Override
	public void setCategorized(Categorized<? extends CategorizedComponent<ComponentContainer>> cats) {
		if (categories==null)
			categories = cats;
	}

	/**
	 * CAUTION: can be set only once, ideally just after construction
	 */
	@Override
	public void setContent(ComponentContainer container) {
		if (content==null)
			content = container;
	}

	@Override
	public ComponentContainer content() {
		return content;
	}


}
