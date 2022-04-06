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

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.TreeNode;

/**
 * Checks that a node is the only one of its kind in its parent's children for a particular
 * parent type (enables to implement conditional multiplicity of child in parent)
 * 
 * @author Jacques Gignoux - 4 févr. 2022
 *
 */
public class UniqueSiblingQuery extends QueryAdaptor {
	
	private String parentType = null;
	
	public UniqueSiblingQuery(String parentType) {
		super();
		this.parentType = parentType.trim();
	}
	
	public UniqueSiblingQuery() {
		super();
	}

	@Override
	public Queryable submit(Object input) { // input is the node to check
		initInput(input);
		if (input instanceof TreeNode) {
			TreeNode child = (TreeNode) input;
			TreeNode parent = child.getParent();
			if ((parentType==null) || (parent.classId().equals(parentType))) {
				int count=0;
				for (TreeNode tn: parent.getChildren())
					if (tn.classId().equals(child.classId()))
						count++;
				if (count>1) {
					actionMsg = "Leave only one child of class '"+child.classId()+
						"' for node '"+parent.toString()+"'";
					errorMsg = "Node '"+parent.toString()+
						"' cannot have more than 1 child of class '"+child.classId()+"'";
				}
			}
		}
		return this;
	}

}
