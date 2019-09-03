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
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineObserver;
import fr.ens.biologie.generic.Singleton;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.lang.reflect.Constructor;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.experiment.Experiment;
import au.edu.anu.twcore.ui.runtime.ControlWidget;
import au.edu.anu.twcore.ui.runtime.Widget;

/**
 * A class matching the "widget" node of the 3Worlds configuration
 * 
 * @author Jacques Gignoux - 14 juin 2019
 *
 */

// Copying the StoppingCondition pattern - not sure if this will work

public class WidgetNode extends InitialisableNode implements Singleton<Widget> {
	
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
		super.initialise();
		/*
		 * I need widget to be valid when ModelRunner shows it ui regardless of whether
		 * or not initialise() has been called.
		 */
		// JG: this looks like a flaw to me: there may be important properties that
		// you must use to construct your singleton.
		// You can always initialise the graph before making the singleton anyway.
		String subclass = (String) properties().getPropertyValue(P_WIDGET_SUBCLASS.key());
		ClassLoader classLoader = OmugiClassLoader.getAppClassLoader();
		Class<? extends Widget> widgetClass;
		try {
			widgetClass = (Class<? extends Widget>) Class.forName(subclass, false, classLoader);
			// ControlWidgets
			if (ControlWidget.class.isAssignableFrom(widgetClass)) {
				Constructor<? extends Widget> widgetConstructor = 
					widgetClass.getDeclaredConstructor(StateMachineObserver.class);
				Experiment exp = (Experiment) get(getParent().getParent().getParent(),
					children(),
					selectOne(hasTheLabel(N_EXPERIMENT.label())));
				StateMachineObserver obs = exp.getInstance(); 
				widget = widgetConstructor.newInstance(obs);
			}
			// Other widgets
			else {
				Constructor<? extends Widget> widgetConstructor = widgetClass.getDeclaredConstructor();
				widget = widgetConstructor.newInstance();
			}
			widget.setProperties(id(),properties());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int initRank() {
		return N_UIWIDGET.initRank();
	}

	@Override
	public Widget getInstance() {
		// ensure its a Singleton
		if (widget != null)
			return widget;
		return widget;
	}

}
