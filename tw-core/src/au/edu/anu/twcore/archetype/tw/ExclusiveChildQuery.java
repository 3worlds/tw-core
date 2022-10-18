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

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.Queryable;
import fr.cnrs.iees.graph.TreeNode;

/**
 * Checks that a node has exactly one child with one of the labels specified in the constructor
 * 
 * @author gignoux
 *
 */
public class ExclusiveChildQuery extends RequiredLabelQuery {

	public ExclusiveChildQuery(String... lab) {
		super(lab);
	}

	public ExclusiveChildQuery(StringTable el) {
		super(el);
	}

	@Override
	public Queryable submit(Object input) {  // input is a treenode
		initInput(input);
		if (input instanceof TreeNode) {
			int count = countLabels(((TreeNode)input).getChildren());
			if (count==0) {
				errorMsg = "Node '"+input.toString()+"' must have exactly one child of one of the types "+requiredLabels.toString()
					+", but none found";
				actionMsg = "Add one child of types "+requiredLabels.toString()+ " to node '"+input.toString()+"'";
			}
			else if (count>1) {
				errorMsg =  "Node '"+input.toString()+"' must have exactly one child of one of the types "+requiredLabels.toString()
					+", but "+ count+" found";
				actionMsg = "Keep only one child of types "+requiredLabels.toString()+ " in node '"+input.toString()+"'";
			}
			else if (count<0) {
				errorMsg = "Count is negative - How did you manage to do that?";
				actionMsg = "You should get some pills, a lot of rest, and come back to work later.";
			}
		}
		return this;
	}

}
