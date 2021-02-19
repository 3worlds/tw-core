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
package au.edu.anu.twcore.ecosystem.runtime.tracking;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.SimulationTracker;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.rngFactory.RngFactory;
import au.edu.anu.twcore.rngFactory.RngFactory.Generator;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.rvgrid.rendezvous.AbstractGridNode;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.ens.biologie.generic.utils.Logging;

/**
 * An ancestor for all kinds of DataTrackers - implements the messaging
 * capacity. All its methods inherited from {@link DataTracker} are implemented
 * as {@code final} to prevent erroneous behaviour. Descendants should just set
 * the {@code T} and {@code M} types, and possibly helper methods for
 * constructing these objects from the raw data.
 *
 * @author Jacques Gignoux - 3 sept. 2019
 *
 * @param <T>
 */
public abstract class AbstractDataTracker<T, M>
		extends AbstractGridNode
		implements DataTracker<T, M> {

	private static Logger log = Logging.getLogger(AbstractDataTracker.class);
	private static final String rngName = "DataTracker RNG";

	private Set<DataReceiver<T, M>> observers = new HashSet<>();
	private int messageType;
	protected int senderId = -1;
	protected Random rng;

	protected AbstractDataTracker(int messageType, int simulatorId) {
		super();
		this.messageType = messageType;
		// TODO: check this is ok for a RNG - do we want other settings?
		Generator gen = RngFactory.find(rngName);
		if (gen == null) {
			gen = RngFactory.newInstance(rngName, 0, RngResetType.NEVER, RngSeedSourceType.PSEUDO, RngAlgType.PCG32);
			rng = gen.getRandom();
		} else
			rng = gen.getRandom();
		senderId = simulatorId;
	}

	@Override
	public final void addObserver(DataReceiver<T, M> listener) {
		observers.add(listener);
	}

	@Override
	public final void sendMessage(int msgType, Object payload) {
		for (DataReceiver<T, M> dr : observers)
			if (dr instanceof GridNode) {
				log.info("Sending data to receiver " + dr.toString());
				GridNode gn = (GridNode) dr;
				RVMessage dataMessage = new RVMessage(msgType, payload, this, gn);
				gn.callRendezvous(dataMessage);
			}
	}

	@Override
	public final void removeObserver(DataReceiver<T, M> observer) {
		observers.remove(observer);
	}

	@Override
	public final void sendData(T data) {
		sendMessage(messageType, data);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public final void sendMetadataTo(GridNode gn, M meta) {
		if (observers.contains(gn)) {
			log.info("Sending data to receiver " + gn.toString());
			RVMessage dataMessage = new RVMessage(DataMessageTypes.METADATA, meta, this, gn);
			gn.callRendezvous(dataMessage);
		} else
			throw new TwcoreException("Attempt to send metadata msg to unobserved receiver. " + gn);
	}

	@Override
	public final boolean hasObservers() {
		return !observers.isEmpty();
	}

	//@Overrride
	public final Collection<DataReceiver<T, M>> observers() {
		return Collections.unmodifiableCollection(observers);
	}

}
