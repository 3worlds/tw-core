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
import java.util.List;
import au.edu.anu.rscs.aot.queries.Query;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * A query to check that a controller of some kind exists in the userInterface
 */

/**
 * @author Ian Davies
 *
 * @date 29 Dec 2019
 */
public class UIStateMachineControllerQuery extends Query {
	// could be SubTreeContainsOneOf??

	private String msg;

	private static void getWidgets(TreeNode parent, List<TreeGraphDataNode> widgets, List<TreeNode> containers) {
		if (parent.classId().equals(N_UIWIDGET.label()))
			widgets.add((TreeGraphDataNode) parent);
		else
			containers.add(parent);

		for (TreeNode child : parent.getChildren())
			getWidgets(child, widgets, containers);
	}

	@Override
	public Query process(Object input) {
		defaultProcess(input);
		TreeNode ui = (TreeNode) input;
		Class<?> smcClass = fr.cnrs.iees.rvgrid.statemachine.StateMachineController.class;
		List<TreeGraphDataNode> widgets = new ArrayList<>();
		List<TreeNode> containers = new ArrayList<>();

		for (TreeNode child : ui.getChildren()) 
			getWidgets(child, widgets, containers);

		int count=0;
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
		// ha - this is like dna methylation. Suppress the query if something else will
		// activate a query before hand.
		// In this case its the must have a child with label top, bottom or tab child.
		if (containers.isEmpty()) {
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
