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

import au.edu.anu.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.*;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;

public class NodeHasPropertyValueQuery extends QueryAdaptor {
	protected final String propertyName;
	protected final List<Object> expectedValues;

	// constructors.... many cases
	private NodeHasPropertyValueQuery(String pname) {
		super();
		expectedValues = new LinkedList<>();
		propertyName = pname;
	}

	// reminder: parameters may come in any order
	// Strings
	public NodeHasPropertyValueQuery(String pname, StringTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(StringTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	// ints
	public NodeHasPropertyValueQuery(String pname, IntTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(IntTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	// doubles
	public NodeHasPropertyValueQuery(String pname, DoubleTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(DoubleTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	// longs
	public NodeHasPropertyValueQuery(String pname, LongTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(LongTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	// shorts
	public NodeHasPropertyValueQuery(String pname, ShortTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(ShortTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	// bytes
	public NodeHasPropertyValueQuery(String pname, ByteTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(ByteTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	// floats
	public NodeHasPropertyValueQuery(String pname, FloatTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(FloatTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	// booleans
	public NodeHasPropertyValueQuery(String pname, BooleanTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(BooleanTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	// chars
	public NodeHasPropertyValueQuery(String pname, CharTable values) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}

	public NodeHasPropertyValueQuery(CharTable values, String pname) {
		this(pname);
		for (int i = 0; i < values.size(); i++)
			expectedValues.add(values.getWithFlatIndex(i));
	}
	// todo: add other property types ?

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode localItem = (TreeNode) input;
		ReadOnlyPropertyList props = null;
		boolean ok = false;
		if (localItem != null)
			if (localItem instanceof ReadOnlyDataHolder) {
				props = ((ReadOnlyDataHolder) localItem).properties();
				if (props.hasProperty(propertyName))
					for (Object o : expectedValues)
						if ((o.equals(props.getPropertyValue(propertyName))) ||
						// this to handle enums
								(o.toString().equals(props.getPropertyValue(propertyName).toString()))) {
							ok = true;
							break;
						}
			}
		if (!ok) {
			String[] msgs = TextTranslations.getNodeHasPropertyValueQuery(propertyName,expectedValues,props.getPropertyValue(propertyName));
			actionMsg = msgs[0];
			errorMsg = msgs[1];
//
//			errorMsg = "Expected property '" + propertyName + "' to  have value '" + expectedValues.toString() + "' but found '"+props.getPropertyValue(propertyName)+"'.";
//			actionMsg = "Edit graph with a text editor to set '" + propertyName + "' value to one of '"
//					+ expectedValues.toString() + "'.";
		}
		return this;
	}

}
