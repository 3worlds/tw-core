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

import au.edu.anu.rscs.aot.collections.tables.*;
import au.edu.anu.rscs.aot.old.queries.Query;
import fr.cnrs.iees.graph.TreeNode;

/**
 * A query to check that a TreeNode parent has a certain property value
 *
 * @author Jacques Gignoux - 9 août 2019
 *
 */
@Deprecated
public class ParentHasPropertyValue extends NodeHasPropertyValueQuery {

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
	public Query process(Object input) {
		TreeNode localItem = (TreeNode) input;
		TreeNode parent = localItem.getParent();
		return super.process(parent);
	}

	public String toString() {
		return "[" + stateString() + "Parent property '"
			+ propertyName + "' must have value '"
			+ expectedValues.toString() + "'.]";
	}

}
