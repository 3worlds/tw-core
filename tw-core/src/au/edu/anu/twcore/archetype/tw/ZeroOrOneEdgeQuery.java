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
package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.List;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

/**
 * 
 * @author Ian Davies - ?
 *
 */
public class ZeroOrOneEdgeQuery extends Query {

	private String edgeLabel = null;
	
	public ZeroOrOneEdgeQuery(String name) {
		edgeLabel=name;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		Node localItem = (Node) input;
		List<Edge> el = (List<Edge>) get(localItem,
			outEdges(),
			selectZeroOrMany(hasTheLabel(edgeLabel)));
		if (el.size()<=1) satisfied=true;
		return this;
	}
	
	public String toString() {
		return "[" + stateString() + " Node must have 0..1 out edges with label '" + edgeLabel+"']";
	}


}
