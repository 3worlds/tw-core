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
public class ChildXorQuery extends Query implements TwArchetypeConstants{

	private String nodeLabel1 = null;
	private String nodeLabel2 = null;
	
	public ChildXorQuery(String nodeLabel1, String nodeLabel2) {
		this.nodeLabel1 = nodeLabel1;
		this.nodeLabel2 = nodeLabel2;
	}
	
	public ChildXorQuery(StringTable table) {
		super();
		nodeLabel1 = table.getWithFlatIndex(0);
		nodeLabel2 = table.getWithFlatIndex(1);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {  // input is a node
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		List<TreeNode> nl1 = (List<TreeNode>) get(localItem, 
			children(),
			selectZeroOrMany(hasTheLabel(nodeLabel1)));
// this is wrong - at least for the query ???		
//			selectZeroOrMany(hasTheLabel(twaNodeLabel1)));
		List<TreeNode> nl2 = (List<TreeNode>) get(localItem,
			children(),			
			selectZeroOrMany(hasTheLabel(nodeLabel2)));
		// this is wrong - at least for the query ???		
//			selectZeroOrMany(hasTheLabel(twaNodeLabel2)));
		satisfied = (nl1.size()>0)^(nl2.size()>0);
		return this;
	}

	public String toString() {
		return "[" + stateString() + " |There must be at least one child node with either label '" + nodeLabel1 + "' or '"+nodeLabel2+"'|]";
	}

}
