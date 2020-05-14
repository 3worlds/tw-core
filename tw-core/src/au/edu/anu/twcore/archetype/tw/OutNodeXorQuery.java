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

import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.ens.biologie.generic.utils.Duple;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

/**
 * Checks that an out node has either of two labels.
 *
 * @author Jacques Gignoux - 6 juin 2019 Constraint: either 1..* nodes with
 *         label1 or 1..* nodes with label2
 *
 */
public class OutNodeXorQuery extends Query {

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

	private Node localItem;

	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		localItem = (Node) input;
		Duple<List<Node>,List<Node>> nodeLists = getNodeLists(localItem, nodeLabel1,nodeLabel2);
//
//		List<Node> nl1 = (List<Node>) get(localItem.edges(Direction.OUT), edgeListEndNodes(),
//				selectZeroOrMany(hasTheLabel(nodeLabel1)));
//		List<Node> nl2 = (List<Node>) get(localItem.edges(Direction.OUT), edgeListEndNodes(),
//				selectZeroOrMany(hasTheLabel(nodeLabel2)));
//		satisfied = (nl1.size() > 0) ^ (nl2.size() > 0);
		satisfied = (nodeLists.getFirst().size()>0)^(nodeLists.getSecond().size()>0);
		return this;
	}

	public String toString() {
		return "[" + stateString() + "'" + localItem.classId() + ":" + localItem.id()
				+ "' must have at least one edge to a node labelled either [" + nodeLabel1 + "] or [" + nodeLabel2
				+ "].]";
	}

	@SuppressWarnings("unchecked")
	private static Duple<List<Node>,List<Node>> getNodeLists(Node localItem,String nodeLabel1,String nodeLabel2){
		List<Node> nl1 = (List<Node>) get(localItem.edges(Direction.OUT), edgeListEndNodes(),
				selectZeroOrMany(hasTheLabel(nodeLabel1)));
		List<Node> nl2 = (List<Node>) get(localItem.edges(Direction.OUT), edgeListEndNodes(),
				selectZeroOrMany(hasTheLabel(nodeLabel2)));
		return new Duple<List<Node>,List<Node>>(nl1,nl2);

	}

	public static boolean propose(Node localItem, Node proposedEndNode, String nodeLabel1, String nodeLabel2) {
		Duple<List<Node>,List<Node>> nodeLists = getNodeLists(localItem, nodeLabel1,nodeLabel1);
		String choice;
		if (!nodeLists.getFirst().isEmpty())
			choice = nodeLists.getFirst().get(0).classId();
		else
			choice = nodeLists.getSecond().get(0).classId();

		return choice.equals(proposedEndNode.classId());
	}

}
