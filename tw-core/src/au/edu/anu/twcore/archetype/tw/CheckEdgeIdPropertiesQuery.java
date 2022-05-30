package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ui.SampleComponentEdge;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * A (very specialised) query to check that an Element has idGroup or idLifeCycle properties
 * depending on its place in the config tree
 * 
 * @author Jacques Gignoux - 30 mai 2022
 *
 */
public class CheckEdgeIdPropertiesQuery extends CheckIdPropertiesQuery {

	public CheckEdgeIdPropertiesQuery() {
		super();
	}

	@Override
	public Queryable submit(Object input) { // input is a sampleComponent edge
		initInput(input);
		if (input instanceof SampleComponentEdge) {
			SampleComponentEdge sce = (SampleComponentEdge) input;
			ReadOnlyPropertyList ropl = sce.properties();
			checkIdHierarchy((ComponentType) sce.endNode(),ropl);
			buildMessages("cross-link",sce.classId()+":"+sce.id());
		}
		return this;
	}

}
