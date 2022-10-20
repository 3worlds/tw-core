package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.data.TableNode;
import au.edu.anu.twcore.ecosystem.dynamics.DataTrackerNode;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataTracker2D;
import fr.cnrs.iees.omugi.graph.Edge;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;

import java.util.List;

/**
 * A temporary query to suppress when DataTracker2D is properly designed
 * 
 * @author Jacques Gignoux - 19 mai 2022
 *
 */
public class Tmp2DMapQuery extends QueryAdaptor {

	public Tmp2DMapQuery() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) { // input is a data tracker node
		initInput(input);
		if ( input instanceof DataTrackerNode) {
			DataTrackerNode dn = (DataTrackerNode) input;
			if (dn.properties().hasProperty(P_DATATRACKER_SUBCLASS.key())) {
				if (dn.properties().getPropertyValue(P_DATATRACKER_SUBCLASS.key())
					.equals(DataTracker2D.class.getCanonicalName())) {
					List<Edge> le = (List<Edge>) get(dn,
						outEdges(),
						selectZeroOrMany(hasTheLabel(E_TRACKTABLE.label())));
					// for safety: this kind of data tracker can only have 1 trackTable edge
					if (le.size()!=1) {
						errorMsg = "Data tracker '"+dn.id()+"' must have exactly one 'TrackTable' cross-link, found "+le.size();
						if (le.size()==0)
							actionMsg = "Provide one 'TrackTable' cross-link to DataTracker '"+dn.id()+"'";
						else if (le.size()==2)
							actionMsg = "Remove one 'TrackTable' cross-link to DataTracker '"+dn.id()+"'";
						else
							actionMsg = "Remove "+(le.size()-1)+" 'TrackTable' cross-links to DataTracker '"+dn.id()+"'";
					}
					// if exactly one trackTable link is found, then the table must have 2 dimensions
					else {
						TableNode tn = (TableNode) le.get(0).endNode();
						le = (List<Edge>) get(tn,
							outEdges(),
							selectZeroOrMany(hasTheLabel(E_SIZEDBY.label())));
						if (le.size()!=2) {
							errorMsg = "Data tracker '"+dn.id()+"' must track a table of dimension 2, found "
								+le.size() + " dimensions in table '"+tn.id()+"'";
							actionMsg = "Replace 'TrackTable' cross-link target, table '"+tn.id()
								+"', by a table with dimension 2, or change table dimension";
						}
						
					}
				}
			}
		}
		return this;
	}

}
