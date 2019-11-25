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

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.TreeNode;

/**
 * @author Ian Davies
 * @date 17 May 2018
 */
/*-
 * Input is node. 
 * Check that node has at least one child with a label in the
 * list.
 */
public class NodeAtLeastOneChildLabelOfQuery extends Query {
	private List<String> labels = new LinkedList<String>();

	public NodeAtLeastOneChildLabelOfQuery(StringTable table) {
		super();
		for (int i = 0; i < table.size(); i++)
			labels.add(table.getWithFlatIndex(i).trim());
	}

	private TreeNode node;
	@Override
	public Query process(Object input) {
		defaultProcess(input);
		node = (TreeNode) input;
		Iterable<? extends TreeNode> children = node.getChildren();
		satisfied = false;
		for (String label : labels) {
			for (TreeNode child : children) {
				if (child.classId().equals(label)) {
					satisfied = true;
					return this;
				}
			}
		}
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + " ||'"+ node.toUniqueString()+"' must have at least one child labelled '" + 
			labels.toString() + "'||]";
	}

}
