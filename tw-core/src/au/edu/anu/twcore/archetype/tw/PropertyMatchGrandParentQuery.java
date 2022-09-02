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
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

/**
 * Check the presence of a certain property depending on parent' parent type.
 * 
 * @author Jacques Gignoux - 8 févr. 2022
 *
 */
// tested 8/2/2022 - looks OK
// 9/2/2022 - Not in use anymore - duplicates CheckFileIdentifiersQuery
public class PropertyMatchGrandParentQuery extends QueryAdaptor {
	
	private String parentClass = null;
	private String propName = null;
	
	/**
	 * Constructor
	 * 
	 * @param args a StringTable of dimension 2: 1st value is the parent class name, 2nd value
	 * is the required matching property in the child node
	 */
	public PropertyMatchGrandParentQuery(StringTable args) {
		super();
		if (args.size()==2) {
			parentClass = args.getWithFlatIndex(0);
			propName = args.getWithFlatIndex(1);
		}
		else
			throw new IllegalArgumentException("Archetype error: PropertyMatchParentQuery requires a StringTable[2] argument");
	}

	@Override
	public Queryable submit(Object input) { // input is a child node
		initInput(input);
		if (input instanceof TreeNode) {
			TreeNode tn = (TreeNode) input;
			TreeNode parent = tn.getParent();
			if (parent!=null) { // only possible if tree is broken due to edition
				TreeNode grandParent = parent.getParent();
				if (grandParent!=null) {
					if (grandParent.classId().equals(parentClass)) {
						if (tn instanceof ReadOnlyDataHolder) {
							if (((ReadOnlyDataHolder)tn).properties().hasProperty(propName))
								return this;
						}
						actionMsg = "Add the '"+propName+"' property to node '"+tn.toString()+"'.";
						errorMsg = "Node '"+tn.toString()+"' requires a '"+propName+"' property to match its '"
							+parent.toString()+"' parent.";
					}
				}
			}
		}
		return this;
	}

}
