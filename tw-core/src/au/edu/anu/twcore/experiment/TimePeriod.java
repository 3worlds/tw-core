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
package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.omugi.graph.GraphFactory;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.SimplePropertyList;
import fr.cnrs.iees.omugi.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.DateTimeType;
import fr.cnrs.iees.omhtk.Resettable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "experiment/timePeriod" node label in the 3Worlds configuration tree.
 * Has the "start" and "end" properties.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class TimePeriod extends InitialisableNode implements Resettable {

	private DateTimeType start;
	private DateTimeType end;
	// TODO: 
//	private StoppingCondition stopOn = null;
	
	public TimePeriod(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		reset();
	}
	
	public TimePeriod(Identity id,GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
		reset();
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_TIMEPERIOD.initRank();
	}

	public long start() {
		return start.getDateTime();
	}
	
	public long end() {
		return end.getDateTime();
	}

	// call this every time properties are edited
	@Override
	public void reset() {
		if (properties().hasProperty(P_TIMEPERIOD_START.key()))
			start = (DateTimeType) properties().getPropertyValue(P_TIMEPERIOD_START.key());
		else
			start = new DateTimeType(0L);
		if (properties().hasProperty(P_TIMEPERIOD_END.key()))
			end = (DateTimeType) properties().getPropertyValue(P_TIMEPERIOD_END.key());
		else
			end = new DateTimeType(Long.MAX_VALUE);
	}
}
