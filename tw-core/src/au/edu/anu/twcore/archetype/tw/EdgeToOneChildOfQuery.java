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

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphNode;
import fr.cnrs.iees.omugi.io.parsing.impl.NodeReference;

/**
 * Checks that an out edge points to one and only one child of a given node
 *
 * @author J. Gignoux - 21 mai 2020
 *
 */
public class EdgeToOneChildOfQuery extends QueryAdaptor {
	private String nodeRef;

	public EdgeToOneChildOfQuery(String reference) {
		super();
		nodeRef = reference;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		Node localItem = (Node) input;
		// get all the nodes
		TreeNode rootNode = (TreeNode) localItem;
		while (rootNode.getParent() != null)
			rootNode = rootNode.getParent();
		Iterable<TreeGraphNode> searchList = (Iterable<TreeGraphNode>) rootNode.subTree();
		// get the node which matches the reference passed as argument to the query
		// constructor
		TreeNode cset = null;
		for (TreeNode catset : searchList)
			if (NodeReference.matchesRef(catset, nodeRef)) {
				cset = catset;
				break;
			}
		// searches in all edges if their end node is one of the children of the
		// previous node
		boolean foundOne = false;
		if (cset != null)
			for (Edge e : localItem.edges(Direction.OUT)) {
				TreeNode targetNode = (TreeNode) e.endNode();
				// search for the node with the proper reference in the whole tree
				for (TreeNode cat : cset.getChildren())
					if ((targetNode.id().equals(cat.id()) && (targetNode.classId().equals(cat.classId())))) {
						foundOne |= true;
						break;
					}
			}
		if (!foundOne) {
			String[] msg = TextTranslations.getEdgeToOneChildOfQuery(nodeRef);
			actionMsg = msg[0];
			errorMsg = msg[1];
			return this;
		}
		return this;
	}

}
