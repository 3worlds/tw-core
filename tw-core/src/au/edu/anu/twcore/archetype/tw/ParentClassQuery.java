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

import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.archetype.TWA;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.SimpleDataTreeNode;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * @author Jacques Gignoux - 6/9/2016 Constraint on a node's parent class
 *
 */
public class ParentClassQuery extends QueryAdaptor{
	private final List<String> klasses;

	public ParentClassQuery(StringTable ot) {
		super();
		klasses = new LinkedList<String>();
		for (int i = 0; i < ot.size(); i++)
			klasses.add((String) ot.getWithFlatIndex(i));
	}

	public ParentClassQuery(String s) {
		klasses = new LinkedList<String>();
		klasses.add(s);
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode localItem = (TreeNode) input;
		Node parent = (Node) localItem.getParent();
		if (parent != null) {
			SimplePropertyList p = ((SimpleDataTreeNode) parent).properties();
			if (p.hasProperty(TWA.SUBCLASS)) {
				String subclass = (String) p.getPropertyValue(TWA.SUBCLASS);
				for (String klass : klasses) {
					if (subclass.equals(klass))
						return this;
				}
				String[] msgs = TextTranslations.getParentClassQuery(klasses,subclass);
				actionMsg = msgs[0];
				errorMsg = msgs[1];
//				actionMsg = "Edit graph file with text editor to repair file.";
//				errorMsg = "Parent must have class one of '" + klasses.toString() + "'.";
				return this;
			}
		}
		return this;
	}

}
