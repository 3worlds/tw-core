package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.ecosystem.dynamics.initial.InitialValues;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import fr.cnrs.iees.omugi.properties.ReadOnlyPropertyList;

/**
 * A (very specialised) query to check that an Element has idGroup or idLifeCycle properties
 * depending on its place in the config tree
 * 
 * @author Jacques Gignoux - 30 mai 2022
 *
 */
public class CheckNodeIdPropertiesQuery extends CheckIdPropertiesQuery {

	public CheckNodeIdPropertiesQuery() {
		super();
	}

	@Override
	public Queryable submit(Object input) { // input is an initialValues node 
		initInput(input);
		if (input instanceof InitialValues) {
			InitialValues iv = (InitialValues) input;
			if (iv.getParent() instanceof ComponentType) { // otherwise check not needed
				ReadOnlyPropertyList ropl = iv.properties();
				checkIdHierarchy((ComponentType)iv.getParent(),ropl);
				buildMessages("node",iv.classId()+":"+iv.id());
			}
		}
		return this;
	}

}
