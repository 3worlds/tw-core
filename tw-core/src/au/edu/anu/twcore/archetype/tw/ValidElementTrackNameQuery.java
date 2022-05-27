package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.util.List;

import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

/**
 * A query to check that SampleXXX edges to data trackers have correct values for idGroup and idLifeCycle
 * 
 * @author Jacques Gignoux - 27 mai 2022
 *
 */
// BIG FLAW HERE! data could come from a file !!!!! sht.

public class ValidElementTrackNameQuery extends QueryAdaptor {
	
	public ValidElementTrackNameQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { // an Edge
		initInput(input);
		if ((input instanceof Edge)&&(input instanceof ReadOnlyDataHolder)) {
			ReadOnlyPropertyList ropl = ((ReadOnlyDataHolder)input).properties();
			boolean ok = false;
			List<Node> ivns = (List<Node>) get(((Edge)input).endNode(),
				children(),
				selectZeroOrMany(hasTheLabel(N_INITIALVALUES.label())));
			if (E_SAMPLEGROUP.type().isAssignableFrom(input.getClass())) {
				StringTable prop = (StringTable) ropl.getPropertyValue(P_DATASOURCE_IDGROUP.key());
				for (int i=0; i<prop.size(); i++)
					for (Node ivn:ivns)
						if (prop.getWithFlatIndex(i).equals(ivn.id())) {
							ok=true;
							break;
				}
				if (!ok) {
					errorMsg = "Cross-link '"+E_SAMPLEGROUP.label()
						+":"+((Identity)input).id()
						+"' property '"+P_DATASOURCE_IDGROUP.key()
						+"' must contain an existing group name ('InitialValues' node label)";
					actionMsg = "Provide at least one name of a group for data sampling in cross-link '"
						+((Identity)input).id()+"', property '"+P_DATASOURCE_IDGROUP.key();					
				}
			}				
			else if (E_SAMPLELIFECYCLE.type().isAssignableFrom(input.getClass())) {
				StringTable prop = (StringTable) ropl.getPropertyValue(P_DATASOURCE_IDLC.key());
				for (int i=0; i<prop.size(); i++)
					for (Node ivn:ivns)
						if (prop.getWithFlatIndex(i).equals(ivn.id())) {
							ok=true;
							break;
				}				
				if (!ok) {
					errorMsg = "Cross-link '"+E_SAMPLELIFECYCLE.label()
						+":"+((Identity)input).id()
						+"' property '"+P_DATASOURCE_IDLC.key()
						+"' must contain an existing group name ('InitialValues' node label)";
					actionMsg = "Provide at least one name of a group for data sampling in cross-link '"
						+((Identity)input).id()+"', property '"+P_DATASOURCE_IDLC.key();					
				}
			}
			else if (E_SAMPLECOMPONENT.type().isAssignableFrom(input.getClass())) {
				String gp = null;
				String lc = null;
				if (ropl.hasProperty(P_DATASOURCE_IDGROUP.key()))
					gp = (String) ropl.getPropertyValue(P_DATASOURCE_IDGROUP.key());
				if (ropl.hasProperty(P_DATASOURCE_IDLC.key()))
					lc = (String) ropl.getPropertyValue(P_DATASOURCE_IDLC.key());
				// TODO : finish this.
			}
		}
		return this;
	}

}
