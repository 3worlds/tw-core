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

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import au.edu.anu.twcore.ecosystem.runtime.TimerEventQueue;
import au.edu.anu.twcore.InitialisableNode;

/**
 * Class needed by event-driven timer
 * 
 * @author Ian Davies Jun 4, 2012
 * 
 *         refurbished by J. Gignoux 22/11/2016 - cf comments with 'JG'
 *         redecorated by I. Davies 5/7/2018 - updated for new
 *         calendar-compatible time system
 * 
 *         TODO This is being update (wip)
 *
 *
 *
 */
public class EventQueueNode//
		extends InitialisableNode//
		implements LimitedEdition<TimerEventQueue>, Sealable {

	private boolean sealed;

	private TimeUnits to;
	private LocalDateTime startDateTime;
	private Map<Integer, TimerEventQueue> queues;

	// default constructor
	public EventQueueNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public EventQueueNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			queues = new HashMap<>();
			TimeModel timeModel = (TimeModel) getParent();
			timeModel.initialise();
			startDateTime = timeModel.timeLine.getTimeOrigin();
			to = timeModel.timeLine.shortestTimeUnit();
			sealed = true;
		}
	}

	@Override
	public int initRank() {
		return N_EVENTQUEUE.initRank();
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

	private TimerEventQueue makeQueue() {
		return new TimerEventQueue(to, startDateTime);
	}

	@Override
	public TimerEventQueue getInstance(int index) {
		if (!sealed)
			initialise();
		if (!queues.containsKey(index))
			queues.put(index, makeQueue());
		return queues.get(index);
	}

}
