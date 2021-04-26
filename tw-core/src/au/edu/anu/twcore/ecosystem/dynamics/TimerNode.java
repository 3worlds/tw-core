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
package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.archetype.TwArchetypeConstants;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.timer.ClockTimer;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventTimer;
import au.edu.anu.twcore.ecosystem.runtime.timer.ScenarioTimer;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.Sealable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Class matching the "system/dynamics/timeLine/timeModel" node label in the 3Worlds configuration tree.
 *
 * NB grain has been removed (now equals 1)
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class TimerNode
		extends InitialisableNode
		implements LimitedEdition<Timer>, Sealable, Resettable,TwArchetypeConstants {

	private boolean sealed = false;

	/** the reference time scale, normally belonging to the TimerModelSimulator */
	protected Timeline timeLine;

	private Map<Integer,Timer> timers = new HashMap<>();

	public TimerNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public TimerNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			timeLine = (Timeline) getParent();
			timeLine.initialise();
			sealed = true;
		}
	}
	
	//private int eventTimeInstance = 0;
	private Timer makeTimer() {
		Timer timer = null;
		// Clock timer
		if (properties().getPropertyValue(twaSubclass)
				.equals(ClockTimer.class.getName())) {
			timer = new ClockTimer(this);
		}
		// event-driven timer
		else if (properties().getPropertyValue(twaSubclass)
				.equals(EventTimer.class.getName())) {
			timer = new EventTimer(this);
		}
		// scenario timer
		else if (properties().getPropertyValue(twaSubclass)
				.equals(ScenarioTimer.class.getName())) {
//			timer = new ScenarioTimer(this);
		}
		return timer;
	}

	@Override
	public int initRank() {
		return N_TIMER.initRank();
	}

	@Override
	public Timer getInstance(int index) {
		if (!sealed)
			initialise();
		if (!timers.containsKey(index))
			timers.put(index, makeTimer());
		return timers.get(index);
	}

	public Timeline timeLine() {
		if (sealed)
			return timeLine;
		throw new TwcoreException("attempt to access uninitialised data");
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

	@Override
	public void reset() {
		for (Timer timer:timers.values())
			timer.reset();
	}

}
