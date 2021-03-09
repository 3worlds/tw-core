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

import au.edu.anu.rscs.aot.collections.tables.BooleanTable;
import au.edu.anu.rscs.aot.collections.tables.ByteTable;
import au.edu.anu.rscs.aot.collections.tables.CharTable;
import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.rscs.aot.collections.tables.FloatTable;
import au.edu.anu.rscs.aot.collections.tables.IntTable;
import au.edu.anu.rscs.aot.collections.tables.LongTable;
import au.edu.anu.rscs.aot.collections.tables.ShortTable;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.TreeNode;

/**
 * A query to check that a TreeNode parent has a certain property value
 *
 * @author Jacques Gignoux - 9 août 2019
 *
 */

public class ParentHasPropertyValue extends NodeHasPropertyValueQuery{
	public ParentHasPropertyValue(String pname, StringTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(StringTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, IntTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(IntTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, DoubleTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(DoubleTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, LongTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(LongTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, ShortTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(ShortTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, ByteTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(ByteTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, FloatTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(FloatTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, BooleanTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(BooleanTable values, String pname) {
		super(values, pname);
	}

	public ParentHasPropertyValue(String pname, CharTable values) {
		super(pname, values);
	}

	public ParentHasPropertyValue(CharTable values, String pname) {
		super(values, pname);
	}
	
	@Override
	public Queryable submit(Object input) {
		TreeNode localItem = (TreeNode) input;
		TreeNode parent = localItem.getParent();
		//TODO: Check this?
		super.submit(parent);
		if (!satisfied()) {
			actionMsg = "Edit graph file with text editor to repair file.";
			errorMsg =  "Parent property '"
					+ propertyName + "' must have value '"
					+ expectedValues.toString() + "'.]";
		}
		return this;

	}


}
