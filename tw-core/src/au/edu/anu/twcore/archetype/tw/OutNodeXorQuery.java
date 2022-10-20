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

import java.util.*;

import fr.cnrs.iees.omugi.collections.tables.ObjectTable;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.*;
import fr.cnrs.iees.omhtk.utils.Duple;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
/**
 * Checks that an out node has either of two labels.
 *
 * @author Jacques Gignoux - 6 juin 2019 Constraint: either 1..* nodes with
 *         label1 or 1..* nodes with label2
 *
 */

public class OutNodeXorQuery extends QueryAdaptor {
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

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Node localItem = (Node) input;
		Duple<List<Node>, List<Node>> nodeLists = getNodeLists(localItem, nodeLabel1, nodeLabel2);
		if(!((nodeLists.getFirst().size() > 0) ^ (nodeLists.getSecond().size() > 0))){
			String[] msgs = TextTranslations.getOutNodeXorQuery(nodeLabel1,nodeLabel2);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		};

		return null;
	}

	@SuppressWarnings("unchecked")
	private static Duple<List<Node>, List<Node>> getNodeLists(Node localItem, String nodeLabel1, String nodeLabel2) {
		List<Node> nl1 = (List<Node>) get(localItem.edges(Direction.OUT), edgeListEndNodes(),
			selectZeroOrMany(hasTheLabel(nodeLabel1)));
		List<Node> nl2 = (List<Node>) get(localItem.edges(Direction.OUT), edgeListEndNodes(),
			selectZeroOrMany(hasTheLabel(nodeLabel2)));
		return new Duple<List<Node>, List<Node>>(nl1, nl2);
	}

	public static boolean propose(Node localItem, Node proposedEndNode, String nodeLabel1, String nodeLabel2) {
		/**
		 * if the proposed node classId is not in the set {nodelabel1,nodelable2} then
		 * this query is not relevant.
		 */
		if (Set.of(nodeLabel1, nodeLabel2).contains(proposedEndNode.classId())) {
			Duple<List<Node>, List<Node>> nodeLists = getNodeLists(localItem, nodeLabel1, nodeLabel2);
			String choice = null;
			if (!nodeLists.getFirst().isEmpty())
				choice = nodeLists.getFirst().get(0).classId();
			else if (!nodeLists.getSecond().isEmpty())
				choice = nodeLists.getSecond().get(0).classId();

			if (choice == null)
				return true;
			return choice.equals(proposedEndNode.classId());
		}
		return true;
	}

}
