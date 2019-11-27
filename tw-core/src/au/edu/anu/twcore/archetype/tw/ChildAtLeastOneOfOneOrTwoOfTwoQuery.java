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

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import fr.cnrs.iees.graph.TreeNode;

/**
 * Checks that a CHILD treenode has either of two labels. 
 * @author Jacques Gignoux - 5/9/2016
 * Constraint: either 1..* nodes with label1 or 1..* nodes with label2
 */
/**
 * @author Ian Davies
 *
 * @date 27 Sep 2019
 * 
 *       Constraint: Either 1 or 2 of nodeLabel1 or just 2 nodelabel2 - for tabs
 *       and containers in the UI
 */
// Great name!
public class ChildAtLeastOneOfOneOrTwoOfTwoQuery extends Query implements TwArchetypeConstants {

	private String nodeLabel1 = null;
	private String nodeLabel2 = null;

	public ChildAtLeastOneOfOneOrTwoOfTwoQuery(String nodeLabel1, String nodeLabel2) {
		this.nodeLabel1 = nodeLabel1;
		this.nodeLabel2 = nodeLabel2;
	}

	public ChildAtLeastOneOfOneOrTwoOfTwoQuery(StringTable table) {
		super();
		nodeLabel1 = table.getWithFlatIndex(0);
		nodeLabel2 = table.getWithFlatIndex(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {
		defaultProcess(input);
		TreeNode parent = (TreeNode) input;
		List<TreeNode> type1 = (List<TreeNode>) get(parent, children(), selectZeroOrMany(hasTheLabel(nodeLabel1)));
		List<TreeNode> type2 = (List<TreeNode>) get(parent, children(), selectZeroOrMany(hasTheLabel(nodeLabel2)));
		if (!type1.isEmpty() && type2.isEmpty())
			satisfied = true;
		else if (type1.isEmpty() && type2.size() == 2)
			satisfied = true;
		else if (type1.size() == 1 && type2.size() == 1)
			satisfied = true;
		return this;
	}

	public String toString() {
		return "[" + stateString() + " ||must have at least one child node with label '" + nodeLabel1
				+ "' or two children with label '" + nodeLabel2 + "'.||]";
	}

}
