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

import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import au.edu.anu.rscs.aot.collections.tables.StringTable;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;


/**
 * @author Jacques Gignoux - 31/5/2019
 * 
 * Constraint: some nodes must have ONE of either a property or a child node
 */
public class ChildXorPropertyQuery extends Query {
	
	private String nodeLabel = null;
	private String propertyName = null;
	
	public ChildXorPropertyQuery(StringTable args) {
		nodeLabel = args.getWithFlatIndex(0);
		propertyName = args.getWithFlatIndex(1);
	}

	@Override
	public Query process(Object input) { // NB: input is the TreeNode on which the Query is called		
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		boolean propertyPresent = false;
		if (localItem instanceof ReadOnlyDataHolder)
			propertyPresent = (((ReadOnlyDataHolder) localItem).properties().hasProperty(propertyName));
		Node n = (Node) get(localItem,
			children(),
			selectZeroOrOne(hasTheLabel(nodeLabel)));
		boolean edgePresent = (n!=null);
		satisfied = (propertyPresent^edgePresent);
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + " ||must have either property '" + propertyName.toString() + "' or child '"+nodeLabel+"'.||]";
	}

}
