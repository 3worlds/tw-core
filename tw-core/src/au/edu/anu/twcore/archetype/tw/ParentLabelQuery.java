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
 * @author Jacques Gignoux - 6/9/2016
 * Constraint on a node's parent label
 *
 */
public class ParentLabelQuery extends Query {
	
	private List<String> labels = new LinkedList<String>();
	
	/**
	 * Use this constructor to test a set of labels. Argument in file
	 * must be an ObjectTable
	 * @param ot
	 */
	public ParentLabelQuery(StringTable ot) {
		super();
		for (int i=0; i<ot.size(); i++)
			labels.add(ot.getWithFlatIndex(i));
	}

	/**
	 * Use this constructor to test a single label
	 * @param s
	 */
	public ParentLabelQuery(String s) {
		labels.add(s);
	}
	
	@Override
	public Query process(Object input) { // input is a TreeNode
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		TreeNode parent = localItem.getParent();
		if (parent!=null)
			for (String label:labels) 
				if (parent.classId().equals(label)) {
					satisfied=true;
					break;
				}
		return this;
	}
	
	public String toString() {
		return "[" + stateString() + " |Parent label must be one of '" + labels.toString() + "'|]";
	}

}
