package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.data.runtime.TwData;
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

public class HierarchicalComponent
		extends TreeGraphDataNode
		implements CategorizedComponent {

	public HierarchicalComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void extrapolateState(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void interpolateState(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public Categorized<CategorizedComponent> membership() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TwData parameters() {
		// TODO Auto-generated method stub
		return null;
	}


}
