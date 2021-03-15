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

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_UIWIDGET;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;

public class UIStateMachineControllerQuery extends QueryAdaptor {

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		// input is the UserInterface
		TreeNode ui = (TreeNode) input;
		Class<?> smcClass = fr.cnrs.iees.rvgrid.statemachine.StateMachineController.class;
		List<TreeGraphDataNode> widgets = new ArrayList<>();
		List<TreeNode> containers = new ArrayList<>();
		for (TreeNode child : ui.getChildren())
			getWidgets(child, widgets, containers);
		List<String> ctrlNames = new ArrayList<>();
		for (TreeGraphDataNode widgetNode : widgets) {
			String kstr = (String) widgetNode.properties().getPropertyValue(TwArchetypeConstants.twaSubclass);
			try {
				Class<?> widgetClass = Class.forName(kstr);
				if (smcClass.isAssignableFrom(widgetClass)) {
					ctrlNames.add(widgetNode.toShortString());
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// nothing to test yet
		if (containers.isEmpty()) {
			return this;
		}
		if (!(ctrlNames.size() == 1)) {
			errorMsg = "Expected one widget that descends from '" + smcClass.getSimpleName()
					+ "' as child of [top,bottom,tab,container] but found " + ctrlNames.size() + ".";
			if (ctrlNames.isEmpty())
				actionMsg = "Add a control widget to either [top,bottom,tab,container].";
			else {
				actionMsg = "Remove one of "+ctrlNames+".";
			}

			// Ajoutez un widget de contrôle à [top,bottom,tab,container]
		}

		return this;
	}

	private static void getWidgets(TreeNode parent, List<TreeGraphDataNode> widgets, List<TreeNode> containers) {
		if (parent.classId().equals(N_UIWIDGET.label()))
			widgets.add((TreeGraphDataNode) parent);
		else
			containers.add(parent);

		for (TreeNode child : parent.getChildren())
			getWidgets(child, widgets, containers);
	}

}
