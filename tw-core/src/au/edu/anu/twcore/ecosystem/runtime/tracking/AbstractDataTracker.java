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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.DataTracker;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.rvgrid.rendezvous.AbstractGridNode;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
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
public abstract class AbstractDataTracker<T, M> extends AbstractGridNode implements DataTracker<T, M> {

	private static Logger log = Logging.getLogger(AbstractDataTracker.class);

	private Set<DataReceiver<T, M>> observers = new HashSet<>();
	private int messageType;
	protected int senderId = -1;

	protected AbstractDataTracker(int messageType) {
		super();
		this.messageType = messageType;
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
	public void sendMetadataTo(GridNode gn, M meta) {
		if (observers.contains(gn)) {
			log.info("Sending data to receiver " + gn.toString());
			RVMessage dataMessage = new RVMessage(DataMessageTypes.METADATA, meta, this, gn);
			gn.callRendezvous(dataMessage);
		} else
			throw new TwcoreException("Attempt to send metadata msg to unobserved receiver. " + gn);
	}

	@Override
	public boolean hasObservers() {
		return !observers.isEmpty();
	}

	public void setSender(int id) {
		senderId = id;
	}

	// As this is an abstract class, shouldn't these dummy methods be removed to force descendants to make a
	// decision about this?
	@Override
	public M getInstance() {
		// dummy - for descendants
		return null;
	}

	@Override
	public void updateTrackList() {
		// do nothing - for descendants
	}

	@Override
	public void removeTrackedItem(SystemComponent wasTracked) {
		// do nothing - for descendants
	}

	@Override
	public void postProcess() {
		// send a reset message to all observer widgets
	}

}
