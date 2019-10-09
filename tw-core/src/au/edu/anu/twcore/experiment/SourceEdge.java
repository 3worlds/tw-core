package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.identity.Identity;

/**
 * Edge linking data sink to data tracker as a source of data to save
 * 
 * @author Jacques Gignoux - 9 oct. 2019
 *
 */
public class SourceEdge extends ALEdge {

	public SourceEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, graph);
	}

}
