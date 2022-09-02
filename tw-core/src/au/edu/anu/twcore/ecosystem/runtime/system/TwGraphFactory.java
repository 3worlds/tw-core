/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            *
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Map;

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

	public TwGraphFactory(int simulatorId) {
		super(new IntegerScope("3w-"+simulatorId));
	}

	public TwGraphFactory(IdentityScope scope, Map<String, String> labels,int simulatorId) {
		super(new IntegerScope("3w-"+simulatorId), labels);
	}

	// NodeFactory

	@Override
	public SystemComponent makeNode() {
		throw new UnsupportedOperationException("SystemComponents cannot be instantiated without a property list");
	}

	// use this to quickly instantiate SystemComponents
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
		throw new UnsupportedOperationException("SystemComponents cannot be instantiated without a property list");
	}

	@Override
	public SystemComponent makeNode(String proposedId) {
		throw new UnsupportedOperationException("SystemComponents cannot be instantiated without a property list");
	}

	@Override
	public SystemComponent makeNode(String proposedId, ReadOnlyPropertyList props) {
		return makeNode(props);
	}

// use the ancestor's
//	@Override
//	public SystemComponent makeNode(Class<? extends Node> nodeClass, String proposedId, ReadOnlyPropertyList props) {
//		return makeNode(proposedId, props);
//	}

	@Override
	public SystemComponent makeNode(Class<? extends Node> nodeClass, String proposedId) {
		throw new UnsupportedOperationException("SystemComponents cannot be instantiated without a property list");
	}

	// EdgeFactory

	@Override
	public SystemRelation makeEdge(Node start, Node end, String proposedId) {
		throw new UnsupportedOperationException("SystemRelations cannot be instantiated without a property list");
	}

	@Override
	public SystemRelation makeEdge(Node start, Node end) {
		throw new UnsupportedOperationException("SystemRelations cannot be instantiated without a property list");
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
		throw new UnsupportedOperationException("SystemRelations cannot be instantiated without a property list");
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
		throw new UnsupportedOperationException("SystemRelations cannot be instantiated without a property list");
	}

}
