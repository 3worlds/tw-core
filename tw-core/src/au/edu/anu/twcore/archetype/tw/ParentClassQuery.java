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

import au.edu.anu.rscs.aot.archetype.ArchetypeArchetypeConstants;
import au.edu.anu.rscs.aot.collections.tables.ObjectTable;

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

/**
 * @author Jacques Gignoux - 6/9/2016 Constraint on a node's parent class
 *
 */
public class ParentClassQuery extends Query implements ArchetypeArchetypeConstants {

	private List<String> klasses = new LinkedList<String>();

	public ParentClassQuery(ObjectTable<?> ot) {
		super();
		for (int i = 0; i < ot.size(); i++)
			klasses.add((String) ot.getWithFlatIndex(i));
	}

	public ParentClassQuery(String s) {
		klasses.add(s);
	}

	@Override
	public Query process(Object input) { // input is a node
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		Node parent = (Node) localItem.getParent();
		ReadOnlyDataHolder parentProps = (ReadOnlyDataHolder) parent;
		if (parent != null && parentProps.properties().hasProperty(aaIsOfClass)) {
			String pKlass = (String) parentProps.properties().getPropertyValue(aaIsOfClass);
			for (String klass : klasses)
				if (pKlass.equals(klass)) {
					satisfied = true;
					break;
				}
		}
		return this;
	}

	public String toString() {
		// return "[" + this.getClass().getSimpleName() + ", satisfied=" + satisfied
		// + ", labels = " + labels+ "]";
		return "[" + stateString() + " Parent  must have class one of '" + klasses.toString() + "']";
	}

}
