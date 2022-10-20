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
import fr.cnrs.iees.graph.TreeNode;

/**
 * Check that the parent of a {@link TreeNode} has one of a set of allowed
 * values.
 * 
 * @author Jacques Gignoux - 6/9/2016
 *
 */
public class ParentLabelQuery extends QueryAdaptor {
	private final List<String> labels;

	/**
	 * Use this constructor to test a set of labels. Argument in file must be an
	 * ObjectTable
	 * 
	 * @param ot
	 */
	public ParentLabelQuery(StringTable ot) {
		super();
		labels = new LinkedList<String>();
		for (int i = 0; i < ot.size(); i++)
			labels.add(ot.getWithFlatIndex(i));
	}

	/**
	 * Use this constructor to test a single label
	 * 
	 * @param s
	 */
	public ParentLabelQuery(String s) {
		labels = new LinkedList<String>();
		labels.add(s);
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode localItem = (TreeNode) input;
		TreeNode parent = localItem.getParent();
		if (parent != null) {
			for (String label : labels)
				if (parent.classId().equals(label)) {
					return this;
				}
			String[] msgs = TextTranslations.getParentLabelQuery(labels, localItem.toShortString());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//			actionMsg = "Change parent to be one of '"+labels.toString()+"'.";
//			errorMsg = "Expected parent to be on of '" + labels.toString() + "' but found '"+localItem.toShortString()+"'.";
		}
		return this;
	}

}
