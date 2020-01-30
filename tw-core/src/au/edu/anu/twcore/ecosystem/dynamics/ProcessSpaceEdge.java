package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * Use this edge to relate a Process to a Space
 * 
 * @author Jacques Gignoux - 27 janv. 2020
 *
 */
//PROBLEM HERE: if a ComponentProcess has a Space, then it must have a relocate function
//to be called whenever a new component is created
//if a relation process has a space, then all the component processes relating to its from and
//to categories must have one.
public class ProcessSpaceEdge extends ALDataEdge {

	public ProcessSpaceEdge(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory graph) {
		super(id, start, end, props, graph);
	}

	public ProcessSpaceEdge(Identity id, Node start, Node end, EdgeFactory graph) {
		super(id, start, end, new ExtendablePropertyListImpl(), graph);
	}

	
}
