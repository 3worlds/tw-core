package au.edu.anu.twcore.archetype.tw;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.omugi.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.*;
import fr.cnrs.iees.graph.Element;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;

/**
 * Checks that a Dataholder has at least one of a series of properties
 * 
 * @author Jacques Gignoux - 30 mai 2022
 *
 */
public class PropertyOrQuery extends QueryAdaptor {

	private List<String> props = new ArrayList<String>();
	
	public PropertyOrQuery(String prop) {
		super();
		props.add(prop);
	}
	
	public PropertyOrQuery(StringTable prop) {
		super();
		for (int i=0; i<prop.size(); i++)
			props.add(prop.getWithFlatIndex(i));
	}


	@Override
	public Queryable submit(Object input) {
		initInput(input);
		if (input instanceof ReadOnlyDataHolder) {
			boolean ok = false;
			for (String prop:props)
				if (((ReadOnlyDataHolder)input).properties().hasProperty(prop)) {
					ok = true; 
					break;
			}
			if (!ok) {
				errorMsg = ((Element)input).classId()+":"+((Element)input).id()
					+"' lacks one of the following properties: "+props.toString();
				actionMsg = "Add at least one property among "+props.toString()+" to "
					+((Element)input).classId()+":"+((Element)input).id();
			}
		}
		return this;
	}

}
