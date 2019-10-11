package au.edu.anu.twcore.ui;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge for linking a TimeSeriesDisplayWidget to its prey, a TimeSeriesDatatracker
 * 
 * @author Jacques Gignoux - 11 oct. 2019
 *
 */
public class TrackTimeSeriesEdge extends ALEdge {

	public TrackTimeSeriesEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
