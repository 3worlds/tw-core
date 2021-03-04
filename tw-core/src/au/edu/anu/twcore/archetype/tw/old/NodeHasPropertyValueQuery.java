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
package au.edu.anu.twcore.archetype.tw.old;

import java.util.LinkedList;
import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.rscs.aot.old.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

/**
 * A query to check that a TreeNode has a certain property value
 *
 * @author Jacques Gignoux - 22 mai 2020
 *
 */
@Deprecated
public class NodeHasPropertyValueQuery extends Query {

	protected String propertyName = null;
	protected List<Object> expectedValues = new LinkedList<Object>();

	// constructors.... many cases
	private NodeHasPropertyValueQuery(String pname) {
		super();
		propertyName = pname;
	}
	// reminder: parameters may come in any order
	// Strings
	public NodeHasPropertyValueQuery(String pname, StringTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(StringTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// ints
	public NodeHasPropertyValueQuery(String pname, IntTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(IntTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// doubles
	public NodeHasPropertyValueQuery(String pname, DoubleTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(DoubleTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// longs
	public NodeHasPropertyValueQuery(String pname, LongTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(LongTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// shorts
	public NodeHasPropertyValueQuery(String pname, ShortTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(ShortTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// bytes
	public NodeHasPropertyValueQuery(String pname, ByteTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(ByteTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// floats
	public NodeHasPropertyValueQuery(String pname, FloatTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(FloatTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// booleans
	public NodeHasPropertyValueQuery(String pname, BooleanTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(BooleanTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// chars
	public NodeHasPropertyValueQuery(String pname, CharTable values) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	public NodeHasPropertyValueQuery(CharTable values, String pname) {
		this(pname);
		for (int i=0; i<values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// todo: add other property types ?

	@Override
	public Query process(Object input) { // input is a TreeNode
		defaultProcess(input);
		TreeNode localItem = (TreeNode) input;
		if (localItem!=null)
			if (localItem instanceof ReadOnlyDataHolder) {
				ReadOnlyPropertyList props = ((ReadOnlyDataHolder) localItem).properties();
				if (props.hasProperty(propertyName))
					for (Object o:expectedValues)
						if ((o.equals(props.getPropertyValue(propertyName))) ||
							// this to handle enums
							(o.toString().equals(props.getPropertyValue(propertyName).toString()))) {
							satisfied = true;
							break;
						}
			}
		return this;
	}

	public String toString() {
		return "[" + stateString() + "Node property '"
			+ propertyName + "' must have value '"
			+ expectedValues.toString() + "'.]";
	}


}
