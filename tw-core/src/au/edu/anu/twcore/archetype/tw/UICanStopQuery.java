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
import au.edu.anu.twcore.ui.WidgetNode;
import au.edu.anu.twcore.ui.runtime.WidgetGUI;
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
 * A query to check that if controller is headless then a StoppingCondition must exist. 
 * Otherwise there is no need for one as the user can do it.
 */

/**
 * @author Ian Davies
 *
 * @date 29 Dec 2019
 */
public class UICanStopQuery extends Query {

	private String msg;

	private static void getWidgets(TreeNode parent, List<TreeGraphDataNode> widgets, List<TreeNode> containers) {
		if (parent.classId().equals(N_UIWIDGET.label()))
			widgets.add((TreeGraphDataNode) parent);
		else
			containers.add(parent);

		for (TreeNode child : parent.getChildren())
			getWidgets(child, widgets, containers);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {
		defaultProcess(input);
		TreeNode ui = (TreeNode) input;
		Class<?> smcClass = fr.cnrs.iees.rvgrid.statemachine.StateMachineController.class;
		List<TreeGraphDataNode> widgets = new ArrayList<>();
		List<TreeNode> containers = new ArrayList<>();
		List<TreeNode> controllersGui = new ArrayList<>();
		List<TreeNode> controllersHl = new ArrayList<>();

		for (TreeNode child : ui.getChildren())
			getWidgets(child, widgets, containers);

		for (TreeGraphDataNode widgetNode : widgets) {
			String kstr = (String) widgetNode.properties().getPropertyValue(TwArchetypeConstants.twaSubclass);
			try {
				Class<?> widgetClass = Class.forName(kstr);
				if (smcClass.isAssignableFrom(widgetClass)) {
					if (widgetNode.getParent().classId().equals(N_UIHEADLESS.label()))
						controllersHl.add(widgetNode);
					else
						controllersGui.add(widgetNode);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// other queries will deal with this
		if (controllersHl.isEmpty()) {
			satisfied = true;
			return this;
		}
		// other queries will deal with this
		if (!controllersGui.isEmpty()) {
			satisfied = true;
			return this;
		}
		WidgetNode ctrlNodeHl = (WidgetNode) controllersHl.get(0);
		// ok we need a stopping condition but only if system.dynamics exists
		TreeNode root = ui.getParent();
		if (root == null) {
			satisfied = true;
			return this;
		}

		List<TreeNode> systems = (List<TreeNode>) get(root.getChildren(),
				selectZeroOrMany(hasTheLabel(N_SYSTEM.label())));
		// other queries will deal with this
		if (systems.isEmpty()) {
			satisfied = true;
			return this;

		}

		List<TreeNode> dyns = new ArrayList<>();
		for (TreeNode system : systems) {
			TreeNode dyn = (TreeNode) get(system.getChildren(), selectZeroOrOne(hasTheLabel(N_DYNAMICS.label())));
			if (dyn != null)
				dyns.add(dyn);
		}
		if (dyns.isEmpty()) {
			satisfied = true;
			return this;
		}
		for (TreeNode dyn : dyns) {
			List<TreeNode> stpConds = (List<TreeNode>) get(dyn.getChildren(),
					selectZeroOrMany(hasTheLabel(N_STOPPINGCONDITION.label())));
			if (!stpConds.isEmpty()) {
				satisfied = true;
				return this;
			}
		}
		satisfied = false;
		msg = "Stopping condition required but none found.";
		return this;
	}

	@Override
	public String toString() {
		return "[" + stateString() + msg + "]";

	}

}
