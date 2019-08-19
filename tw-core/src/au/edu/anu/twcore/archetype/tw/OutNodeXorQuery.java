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

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;

/**
 * Checks that an out node has either of two labels.
 *  
 * @author Jacques Gignoux - 6 juin 2019
 * Constraint: either 1..* nodes with label1 or 1..* nodes with label2
 * 
 */
public class OutNodeXorQuery extends Query implements TwArchetypeConstants{

	private String nodeLabel1 = null;
	private String nodeLabel2 = null;
	
	public OutNodeXorQuery(String nodeLabel1, String nodeLabel2) {
		this.nodeLabel1 = nodeLabel1;
		this.nodeLabel2 = nodeLabel2;
	}
	
	public OutNodeXorQuery(ObjectTable<?> table) {
		super();
		nodeLabel1 = (String) table.getWithFlatIndex(0);
		nodeLabel2 = (String) table.getWithFlatIndex(1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a node
		defaultProcess(input);
		Node localItem = (Node) input;
		List<Node> nl1 = (List<Node>) get(localItem, 
			edges(Direction.OUT),
			edgeListEndNodes(),
			selectZeroOrMany(hasTheLabel(twaNodeLabel1)));
		List<Node> nl2 = (List<Node>) get(localItem,
			edges(Direction.OUT),
			edgeListEndNodes(),
			selectZeroOrMany(hasTheLabel(twaNodeLabel2)));
		satisfied = (nl1.size()>0)^(nl2.size()>0);
		return this;
	}

	public String toString() {
		return "[" + stateString() + " There must be at least one out node with either label '" + twaNodeLabel1 + "' or '"+twaNodeLabel2+"']";
	}

}
