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
package au.edu.anu.twcore.root;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.PropertyListFactory;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.properties.impl.ReadOnlyPropertyListImpl;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

/**
 * A factory for 3Worlds specifications - has predefined labels matching nodes and edges.
 * .makePropertyList returns an extendable property list.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class TwConfigFactory extends TreeGraphFactory implements EditableFactory {

	private static Map<String,String> twLabels = new HashMap<>();
	
	// Sorry - but how else to i clean up when editing graphs?
	public void removeEdgeId(Edge edge) {
		this.scope.removeId(edge.id());
	}
	
	
	private static PropertyListFactory plf = new PropertyListFactory () {
		@Override
		public ReadOnlyPropertyList makeReadOnlyPropertyList(Property... properties) {
			return new ReadOnlyPropertyListImpl(properties);
		}
		@Override
		public ExtendablePropertyList makePropertyList(Property... properties) {
			return new ExtendablePropertyListImpl(properties);
		}
		@Override
		public ExtendablePropertyList makePropertyList(String... propertyKeys) {
			return new ExtendablePropertyListImpl(propertyKeys);
		}
	};
	
		
	public TwConfigFactory() {
		super("3worlds",twLabels);
	}

	public TwConfigFactory(String scopeName) {
		this();
//		if (scopeName!=null)
//			scope=new LocalScope(scopeName);
	}

	public TwConfigFactory(String scopeName, Map<String, String> labels) {
		this();
	}

	// initialisation with the mapping of labels to nodes and edges
	static {
		for (ConfigurationNodeLabels key:ConfigurationNodeLabels.values())
			twLabels.put(key.label(), key.type().getName());
		for (ConfigurationEdgeLabels key:ConfigurationEdgeLabels.values())
			twLabels.put(key.label(), key.type().getName());
	}	
	
	@Override
	public PropertyListFactory nodePropertyFactory() {
		return plf;
	}
	
	@Override
	public PropertyListFactory edgePropertyFactory() {
		return plf;
	}
	@Override
	public void expungeNode(Node node) {
		scope.removeId(node.id());
		for (Edge edge:node.edges())
			expungeEdge(edge);	
		for (TreeGraph<TreeGraphNode, ALEdge> g:graphs)
			g.removeNode( (TreeGraphNode) node);	
	}

	@Override
	public void expungeEdge(Edge edge) {
		scope.removeId(edge.id());
	}

}
