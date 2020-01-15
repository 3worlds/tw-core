package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Map;

import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALGraphFactory;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.IntegerScope;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The specific factory of 3Worlds for SystemComponents and SystemRelations
 * 
 * @author Jacques Gignoux - 15 janv. 2020
 *
 */
public class TwGraphFactory extends ALGraphFactory {

	public TwGraphFactory() {
		super(new IntegerScope("3w"));
	}

	public TwGraphFactory(IdentityScope scope, Map<String, String> labels) {
		super(new IntegerScope("3w"), labels);
	}
	
	// NodeFactory

	@Override
	public SystemComponent makeNode() {
		throw new TwcoreException("SystemComponents cannot be instantiated without a property list");
	}

	@Override
	public SystemComponent makeNode(ReadOnlyPropertyList props) {
		return new SystemComponent(scope.newId(),(SimplePropertyList) props,this);
	}

	@Override
	public SystemComponent makeNode(Class<? extends Node> nodeClass, ReadOnlyPropertyList props) {
		return makeNode(props);
	}

	@Override
	public SystemComponent makeNode(Class<? extends Node> nodeClass) {
		throw new TwcoreException("SystemComponents cannot be instantiated without a property list");
	}

	@Override
	public SystemComponent makeNode(String proposedId) {
		throw new TwcoreException("SystemComponents cannot be instantiated without a property list");
	}

	@Override
	public SystemComponent makeNode(String proposedId, ReadOnlyPropertyList props) {
		return makeNode(props);
	}

	@Override
	public SystemComponent makeNode(Class<? extends Node> nodeClass, String proposedId, ReadOnlyPropertyList props) {
		return makeNode(proposedId, props);
	}

	@Override
	public SystemComponent makeNode(Class<? extends Node> nodeClass, String proposedId) {
		throw new TwcoreException("SystemComponents cannot be instantiated without a property list");
	}

	// EdgeFactory
	
	@Override
	public SystemRelation makeEdge(Node start, Node end, String proposedId) {
		throw new TwcoreException("SystemRelations cannot be instantiated without a property list");
	}

	@Override
	public SystemRelation makeEdge(Node start, Node end) {
		throw new TwcoreException("SystemRelations cannot be instantiated without a property list");
	}

	@Override
	public SystemRelation makeEdge(Node start, Node end, ReadOnlyPropertyList props) {
		return new SystemRelation(scope.newId(),start,end,(SimplePropertyList) props,this);
	}

	@Override
	public SystemRelation makeEdge(Class<? extends Edge> edgeClass, Node start, Node end, ReadOnlyPropertyList props) {
		return makeEdge(start, end, props);
	}

	@Override
	public SystemRelation makeEdge(Class<? extends Edge> edgeClass, Node start, Node end) {
		throw new TwcoreException("SystemRelations cannot be instantiated without a property list");
	}

	@Override
	public SystemRelation makeEdge(Node start, Node end, String proposedId, ReadOnlyPropertyList props) {
		return makeEdge(start, end, props);
	}

	@Override
	public SystemRelation makeEdge(Class<? extends Edge> edgeClass, Node start, Node end, String proposedId,
			ReadOnlyPropertyList props) {
		return makeEdge(start,end,props);
	}

	@Override
	public SystemRelation makeEdge(Class<? extends Edge> edgeClass, Node start, Node end, String proposedId) {
		throw new TwcoreException("SystemRelations cannot be instantiated without a property list");
	}

}
