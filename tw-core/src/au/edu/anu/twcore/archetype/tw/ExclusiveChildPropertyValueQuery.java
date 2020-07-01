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

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * Checks that if a child node with a given property value is present, then no child with another
 * value in the same property can be present. Can be instantiated with a single label, or a
 * table of compatible labels.
 *
 * @author J. Gignoux - 22 mai 2020
 *
 */
public class ExclusiveChildPropertyValueQuery extends NodeHasPropertyValueQuery {

	public ExclusiveChildPropertyValueQuery(BooleanTable values, String pname) {
		super(values, pname);
	}

	public ExclusiveChildPropertyValueQuery(ByteTable values, String pname) {
		super(values, pname);
	}

	public ExclusiveChildPropertyValueQuery(CharTable values, String pname) {
		super(values, pname);
	}

	public ExclusiveChildPropertyValueQuery(DoubleTable values, String pname) {
		super(values, pname);
	}

	public ExclusiveChildPropertyValueQuery(FloatTable values, String pname) {
		super(values, pname);
	}

	public ExclusiveChildPropertyValueQuery(IntTable values, String pname) {
		super(values, pname);
	}

	public ExclusiveChildPropertyValueQuery(LongTable values, String pname) {
		super(values, pname);
	}

	public ExclusiveChildPropertyValueQuery(ShortTable values, String pname) {
		super(values, pname);
	}

	public ExclusiveChildPropertyValueQuery(String pname, BooleanTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(String pname, ByteTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(String pname, CharTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(String pname, DoubleTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(String pname, FloatTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(String pname, IntTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(String pname, LongTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(String pname, ShortTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(String pname, StringTable values) {
		super(pname, values);
	}

	public ExclusiveChildPropertyValueQuery(StringTable values, String pname) {
		super(values, pname);
	}

	@Override
	public Query process(Object input) { // input is a treenode (one of the siblings that must be tested
		defaultProcess(input);
		TreeNode topNode = (TreeNode) input;
		Class<?> nodeClass = topNode.getClass();
		topNode = topNode.getParent();
		List<TreeNode> nodesWithProperValue = new LinkedList<>();
		List<TreeNode> nodesWithOtherValue = new LinkedList<>();
		for (TreeNode child: topNode.getChildren())
			if (nodeClass.isAssignableFrom(child.getClass())) {
				super.process(child);
				if (satisfied) {
					nodesWithProperValue.add(child);
					satisfied = false;
				}
				else if (child instanceof ReadOnlyDataHolder) {
					ReadOnlyPropertyList props = ((ReadOnlyDataHolder) child).properties();
					if (props.hasProperty(propertyName))
						nodesWithOtherValue.add(child);
				}
		}
		satisfied  = nodesWithProperValue.isEmpty() ||
			((!nodesWithProperValue.isEmpty()) && (nodesWithOtherValue.isEmpty()));
		return this;
	}

	public String toString() {
		return "[" + stateString() + "ExclusiveChildPropertyValueQuery failed";
	}


}
