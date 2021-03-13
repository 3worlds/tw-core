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
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.TreeNode;

public class NodeAtLeastOneChildLabelOfQuery extends QueryAdaptor {
	private List<String> labels = new LinkedList<String>();

	public NodeAtLeastOneChildLabelOfQuery(StringTable table) {
		super();
		for (int i = 0; i < table.size(); i++)
			labels.add(table.getWithFlatIndex(i).trim());
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode node = (TreeNode) input;
		Iterable<? extends TreeNode> children = node.getChildren();
		boolean ok = false;
		for (String label : labels) {
			for (TreeNode child : children) {
				if (child.classId().equals(label)) {
					ok = true;
					return this;
				}
			}
		}
		if (!ok) {
			errorMsg = "'" + node.toShortString() + "' must have at least one child labelled '"
					+ labels.toString() + "'.";
			
			actionMsg = "Add node that is one of "+labels.toString()+" to '"+node.toShortString()+"'.";
		}
		
		return this;
	}

}
