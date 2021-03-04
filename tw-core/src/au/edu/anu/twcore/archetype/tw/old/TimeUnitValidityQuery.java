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

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.rscs.aot.old.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.twcore.constants.TimeScaleType;
import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * A Query to check that time units are valid according to a time scale type
 * Applies to a property of a Node which is or has a link to a TimeLine node
 * (with a scale property)
 * 
 * @author gignoux
 *
 */
@Deprecated
public class TimeUnitValidityQuery extends Query {

	private TimeScaleType refScale = null;
	private String pscale = null;
	private String pname = null;

	public TimeUnitValidityQuery(StringTable parameters) {
		super();
		pname = parameters.getWithFlatIndex(0); // name of the time property
		pscale = parameters.getWithFlatIndex(1); // name of the time scale prop
	}

	@Override
	public Query process(Object input) { // input is a Node with 2 properties, one of them has the time scale
		defaultProcess(input);
		ReadOnlyDataHolder localItem = (ReadOnlyDataHolder) input;
		TreeNode localNode = (TreeNode) input;
		// search for a property named pscale, which has the time scale type
		refScale = (TimeScaleType) localItem.properties().getPropertyValue(pscale);
		// If null, this query should remain silent;
		if (refScale == null) {
			ReadOnlyDataHolder p = (ReadOnlyDataHolder) localNode.getParent();
			if (p != null) 
				refScale = (TimeScaleType) p.properties().getPropertyValue(pscale);
		}
		Property prop = null;
		prop = localItem.properties().getProperty(pname);
		if (prop == null)
			// satisfied = false;
			satisfied = true;
		else if (refScale == null)
			satisfied = true;
		// remain silent until there are time models present
		// Could make the default refScale ABITRARY but I think it would seem confusing.
		else if(!localNode.hasChildren())
			satisfied = true;
		else {
			TimeUnits tu = (TimeUnits) prop.getValue();
			if (tu == null)
				tu = TimeUnits.UNSPECIFIED;
			satisfied = TimeScaleType.validTimeUnits(refScale).contains(tu);
		}
		return this;
	}

	public String toString() {
		return "[" + stateString() + "Property value for " + pname + " must be one of {"
				+ TimeScaleType.validTimeUnits(refScale).toString() + "}.]";
	}

}
