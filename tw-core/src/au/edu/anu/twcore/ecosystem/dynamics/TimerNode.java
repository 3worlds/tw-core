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

//	private TimeUnits timeUnit;
//
//	private int nTimeUnits;
//
//	protected boolean isExact = false;
//
//	/** if isExact is false grainsPerBaseUnit will be zero */
//	protected long grainsPerBaseUnit = 0L;
	
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


//	public int nTimeUnits() {
//		if (sealed)
//			return nTimeUnits;
//		throw new TwcoreException("attempt to access uninitialised data");
//	}
//
//	public TimeUnits timeUnit() {
//		if (sealed)
//			return timeUnit;
//		throw new TwcoreException("attempt to access uninitialised data");
//	}

	public Timeline timeLine() {
		if (sealed)
			return timeLine;
		throw new TwcoreException("attempt to access uninitialised data");
	}

//	public boolean isExact() {
//		if (sealed)
//			return isExact;
//		throw new TwcoreException("attempt to access uninitialised data");
//	}

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
	
	/**
	 * Utility to convert user time of this time unit to base time in timeLine time
	 * grains.
	 * 
	 * Watch out for this now: if you want the value of a time segment between t1
	 * and t2 you need to call this function for each t1 and t2 and take the
	 * difference to get the time line segment value.
	 * 
	 * 
	 * 
	 * @param modelTime
	 *            the user time to convert from
	 * @return internal time (= number of timegrains)
	 */
//	public final long modelToBaseTime(double modelTime) {
//		if (isExact)
//			return Math.round(modelTime * grainsPerBaseUnit);
//		else {
//			double result = TimeUtil.convertTime(modelTime, timeUnit, timeLine.shortestTimeUnit(),
//					timeLine.getTimeOrigin());
//			result = result * nTimeUnits;
//			return Math.round(result);
//		}
//	}
//	
    /**
     * Utility to convert internal time to this Time Model's time units
     * @return
     */
//    public final double userTime(long t) {
//    	return (1.0*t)/grainsPerBaseUnit;
////    	return (t-timeOrigin)/timeGrain; // time origin is gone: it's either 0 or a date depending on the calendar system
//    }


}
