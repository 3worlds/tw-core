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
/**
 * Checks that if a child node with a given property value is present, then no child with another
 * value in the same property can be present. Can be instantiated with a single label, or a
 * table of compatible labels.
 *
 * @author J. Gignoux - 22 mai 2020
 *
 */

import java.util.*;

import fr.cnrs.iees.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.properties.ReadOnlyPropertyList;

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
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode sibbling = (TreeNode) input;
		Class<?> sibblingClass = sibbling.getClass();
		TreeNode parent = sibbling.getParent();
		List<TreeNode> nodesWithProperValue = new LinkedList<>();
		List<TreeNode> nodesWithOtherValue = new LinkedList<>();
		boolean ok = true;
		for (TreeNode child : parent.getChildren())
			if (sibblingClass.isAssignableFrom(child.getClass())) {
				super.submit(child);
				if (satisfied()) {
					nodesWithProperValue.add(child);
					ok = false;
				} else if (child instanceof ReadOnlyDataHolder) {
					ReadOnlyPropertyList props = ((ReadOnlyDataHolder) child).properties();
					if (props.hasProperty(propertyName))
						nodesWithOtherValue.add(child);
				}
			}
		ok = nodesWithProperValue.isEmpty() || ((!nodesWithProperValue.isEmpty()) && (nodesWithOtherValue.isEmpty()));
		if (!ok) {
			String[] msgs = TextTranslations.getExclusiveChildPropertyValueQuery(sibbling.toShortString(),propertyName,expectedValues,nodesWithOtherValue.size());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		} else {
			actionMsg = null;
			errorMsg = null;
		}

		return this;
	}

}
