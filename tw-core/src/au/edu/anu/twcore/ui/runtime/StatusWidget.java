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

import au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorStates;
import fr.cnrs.iees.rvgrid.rendezvous.AbstractGridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.rendezvous.RendezvousProcess;
import fr.cnrs.iees.rvgrid.statemachine.State;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineObserver;
import fr.ens.biologie.generic.utils.Logging;

/**
 * Ancestor class for widgets able to process status messages
 *
 * @author Jacques Gignoux - 9 sept. 2019
 *
 */
public abstract class StatusWidget
		extends AbstractGridNode
		implements StateMachineObserver {

	private static Logger log = Logging.getLogger(StatusWidget.class);

	public StatusWidget(StateMachineEngine<StatusWidget> statusSender) {
		super();
		statusSender.addObserver(this);
		addRendezvous(new RendezvousProcess() {
			@Override
			public void execute(RVMessage message) {
				if (message.getMessageHeader().type()==statusSender.statusMessageCode()	) {
					State state = (State) message.payload();
					log.info(()->"received status message - now state = "+state.getName());
					onStatusMessage(state);
				}
			}
		},statusSender.statusMessageCode());
	}

	// helper method for this slightly confusing classes (one from rvgrid, the other from twcore
	// Maybe SimulatorStates should be called DeployerStates?
	public static boolean isSimulatorState(State state, SimulatorStates simState) {
		return state.getName().equals(simState.name());
	}


}
