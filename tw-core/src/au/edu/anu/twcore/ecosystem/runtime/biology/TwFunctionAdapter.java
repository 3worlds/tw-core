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
package au.edu.anu.twcore.ecosystem.runtime.biology;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.HierarchicalContext;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventQueue;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventQueueAdapter;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventQueueWriteable;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.rngFactory.RngFactory;
import au.edu.anu.twcore.rngFactory.RngFactory.Generator;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * Ancestor for the class doing the user-defined computation
 *
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public abstract class TwFunctionAdapter implements TwFunction {

	private AbstractProcess myProcess = null;
	protected HierarchicalContext focalContext = null;
	protected HierarchicalContext otherContext = null;
	Random rng = null;
	TwFunctionTypes fType;
	Set<TwFunctionTypes> csqTypes = new HashSet<>();
	Map<String,EventQueue> eventQueues = new TreeMap<>();

	/**
	 * constructor defining its own random number stream. It's a default stream with
	 * a 'secure' seed, ie impossible to replicate.
	 */
	public TwFunctionAdapter() {
		super();
	}

	@Override
	// CAUTION: can be set only once
	// this to prevent end-users to mess up with the internal code
	public final void initProcess(AbstractProcess process) {
		if (myProcess == null)
			myProcess = process;
	}

	@Override
	public final AbstractProcess process() {
		return myProcess;
	}

	@Override
	public void addConsequence(TwFunction function) {
		// do nothing - some descendants have no consequences
	}

	@Override
	public void setFocalContext(HierarchicalContext context) {
		focalContext = context;
	}

	@Override
	public void setOtherContext(HierarchicalContext context) {
		otherContext = context;
	}

	@Override
	public final Random rng() {
		return rng;
	}

	@Override
	// CAUTION: can be set only once
	// this to prevent end-users to mess up with the internal code
	public final void setRng(Random arng) {
		if (arng == null)
			throw new TwcoreException("valid random number generator expected");
		if (rng==null)
			rng = arng;
	}

	// CAUTION: can be set only once after construction
	@Override
	public final void setEventQueue(EventQueueWriteable queue, String queueName) {
		if (eventQueues.containsKey(queueName))
			throw new TwcoreException("attempt to set event queue more than once");
		eventQueues.put(queueName, new EventQueueAdapter(queue,this));
	}


	@Override
	public final EventQueue getEventQueue(String queueName) {
		return eventQueues.get(queueName);
	}

	/*-
	 * IDD: Nobody knows about this default rng so it can't be reset or stored in
	 * any initial state file i.e it is unmanaged.
	 * To avoid this, the edge to a RngNode must be 1..1 (or 1..*?)
	 * To get and therefore save the state of the rng use gen.getState()
	 * To set the state of an rng use gen.setStage(long state);
	 * To reset use gen.reset(); or
	 * RngFactory,find(key).reset();
	 * Since defRngName has RngResetType.never it can never be reset anyway
	 */
	// JG: I guess the simulator should keep a handle to it? now there will be one per simulator.
	@Override
	public final Random defaultRng(int index) {
		if (rng == null) {
			Generator gen = RngFactory.find(defRngName+":"+index);
			if (gen != null)
				this.rng = gen.getRandom();
			else {
				gen = RngFactory.newInstance(defRngName+":"+index, 0, RngResetType.never,
					RngSeedSourceType.secure,RngAlgType.Pcg32);
				this.rng = gen.getRandom();
			}
		}
		return rng;
	}

}
