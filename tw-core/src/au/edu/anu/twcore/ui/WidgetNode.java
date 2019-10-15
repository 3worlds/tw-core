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
package au.edu.anu.twcore.ui;

import fr.cnrs.iees.OmugiClassLoader;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineController;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.lang.reflect.Constructor;
import java.util.List;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeData;
import au.edu.anu.twcore.data.runtime.TimeSeriesData;
import au.edu.anu.twcore.ecosystem.dynamics.DataTrackerNode;
import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import au.edu.anu.twcore.experiment.Experiment;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import au.edu.anu.twcore.ui.runtime.StatusWidget;
import au.edu.anu.twcore.ui.runtime.Widget;

/**
 * A class matching the "widget" node of the 3Worlds configuration
 * 
 * @author Jacques Gignoux - 14 juin 2019
 *
 */
public class WidgetNode extends InitialisableNode implements Singleton<Widget>, Sealable {

	private boolean sealed = false;
	private Widget widget;

	public WidgetNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public WidgetNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			String subclass = (String) properties().getPropertyValue(P_WIDGET_SUBCLASS.key());
			ClassLoader classLoader = OmugiClassLoader.getAppClassLoader();
			Class<? extends Widget> widgetClass;
			try {
				widgetClass = (Class<? extends Widget>) Class.forName(subclass, true, classLoader);
				// Status & StateMachineController widgets
				if ((StatusWidget.class.isAssignableFrom(widgetClass))
						| (StateMachineController.class.isAssignableFrom(widgetClass))) {
					Constructor<? extends Widget> widgetConstructor = widgetClass
							.getDeclaredConstructor(StateMachineEngine.class);
					// ah!
//					Experiment exp = (Experiment) get(getParent().getParent().getParent(),
//						children(),
//						selectOne(hasTheLabel(N_EXPERIMENT.label())));
					TreeNode root = (TreeNode) this;
					// this could be a function of any TreeNode
					while (root.getParent() != null)
						root = root.getParent();
					Experiment exp = (Experiment) get(root.getChildren(), 
						selectOne(hasTheLabel(N_EXPERIMENT.label())));
					StateMachineController obs = exp.getInstance();
					widget = widgetConstructor.newInstance(obs.stateMachine());
				}
				// Other widgets
				else {
					Constructor<? extends Widget> widgetConstructor = widgetClass.getDeclaredConstructor();
					widget = widgetConstructor.newInstance();
				}
				widget.setProperties(id(), properties());
				// tracker sending data to this widget
				List<DataTrackerNode> timeSeriesTrackers = (List<DataTrackerNode>) get(edges(Direction.OUT),
					selectZeroOrMany(hasTheLabel(E_TRACKSERIES.label())),
					edgeListEndNodes()); 
				for (DataTrackerNode dtn:timeSeriesTrackers)
					if (widget instanceof DataReceiver)
						dtn.attachTimeSeriesWidget((DataReceiver<TimeSeriesData, Metadata>) widget);

			} catch (Exception e) {
				e.printStackTrace();
			}
			SimulatorNode sim = (SimulatorNode) get(edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(E_TRACKTIME.label())), endNode());
			if (sim != null)
				sim.addObserver((DataReceiver<TimeData, Metadata>) widget);
//			sim.addObserver((DataReceiver<LabelValuePairData, Metadata>) widget);
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_UIWIDGET.initRank();
	}

	@Override
	public Widget getInstance() {
		if (!sealed)
			initialise();
		return widget;
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

}
