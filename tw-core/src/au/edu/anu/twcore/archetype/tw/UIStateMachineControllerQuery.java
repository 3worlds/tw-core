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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.TableNode;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.twcore.constants.DataElementType;

import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * A query to check that the properties of a parameterValues or variableValues 
 * node match the definitions of the driver or parameter data
 */

/**
 * @author Ian Davies
 *
 * @date 29 Dec 2019
 */
public class UIStateMachineControllerQuery extends Query {
	// could be SubTreeContainsOneOf??

	private String msg;

	@Override
	public Query process(Object input) {
		defaultProcess(input);
		TreeNode ui = (TreeNode) input;
		Class<?> smcClass = fr.cnrs.iees.rvgrid.statemachine.StateMachineController.class;
		List<TreeGraphDataNode> widgets = new ArrayList<>();
		int containers =0;
		for (TreeNode child : ui.getChildren()) {
			containers++;
			for (TreeNode widget : child.getChildren())
				widgets.add((TreeGraphDataNode) widget);
		}
		int count = 0;
		for (TreeGraphDataNode widgetNode : widgets) {
			String kstr = (String) widgetNode.properties().getPropertyValue(TwArchetypeConstants.twaSubclass);
			try {
				Class<?> widgetClass = Class.forName(kstr);
				if (smcClass.isAssignableFrom(widgetClass)) {
					count++;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// ha - this is like dna methylation. Suppress the query if something else will activate a query before hand.
		// In this case its the must have a top, bottom or tab child.
		if (containers==0) {
			satisfied = true;
			return this;
		}
		if (!(count == 1)) {
			msg = "User interface must have one and only one controller widget";
			satisfied = false;
		} else
			satisfied = true;
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + msg + "]";

	}

}
