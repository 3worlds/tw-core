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
package au.edu.anu.twcore.ui.runtime;

import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.runtime.tracking.AbstractDataTracker;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.rendezvous.RendezvousProcess;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.ens.biologie.generic.utils.Logging;

/**
 * An ancestor class for widgets displaying data
 *
 * @author Jacques Gignoux - 3 sept. 2019
 *
 * @param <T> The type of data sent in messages
 */
public abstract class AbstractDisplayWidget<T,M>
		extends StatusWidget
		implements DataReceiver<T,M> {

	private static Logger log = Logging.getLogger(AbstractDisplayWidget.class);

	protected AbstractDisplayWidget(StateMachineEngine<StatusWidget> statusSender,int dataType) {
		super(statusSender);
		// RV for data messages
		addRendezvous(new RendezvousProcess() {
			@SuppressWarnings("unchecked")
			@Override
			public void execute(RVMessage message) {
				if (message.getMessageHeader().type()==dataType) {
					T data = (T) message.payload();
					log.info(()->"received data message data = "+data.toString());
					onDataMessage(data);
					log.info(()->"finished processing data");
				}
			}
		},dataType);
		// RV for metadata messages
		addRendezvous(new RendezvousProcess() {
			@SuppressWarnings("unchecked")
			@Override
			public void execute(RVMessage message) {
				if (message.getMessageHeader().type()==AbstractDataTracker.METADATA) {
					M meta = (M) message.payload();
					log.info(()->"received metadata message data = "+meta.toString());
					onMetaDataMessage(meta);
					log.info(()->"finished processing metadata");
				}
			}
		},AbstractDataTracker.METADATA);

	}

}
