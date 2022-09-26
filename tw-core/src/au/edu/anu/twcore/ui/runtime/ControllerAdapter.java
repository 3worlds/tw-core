/**************************************************************************
 *  TW-UIFX - ThreeWorlds User-Interface fx                               *
 *                                                                        *
 *  Copyright 2018: Jacques Gignoux & Ian D. Davies                       *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            *
 *                                                                        *
 *  TW-UIFX contains the Javafx interface for ModelMaker and ModelRunner. *
 *  This is to separate concerns of UI implementation and the code for    *
 *  these java programs.                                                  *
 *                                                                        *
 **************************************************************************
 *  This file is part of TW-UIFX (ThreeWorlds User-Interface fx).         *
 *                                                                        *
 *  TW-UIFX is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-UIFX is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-UIFX.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>.                  *
 *                                                                        *
 **************************************************************************/

package au.edu.anu.twcore.ui.runtime;

import fr.cnrs.iees.rvgrid.statemachine.Event;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineController;
import fr.cnrs.iees.rvgrid.statemachine.StateMachineEngine;

/**
 * 
 * This adapter makes UI controllers more responsive by posting all state
 * machine transition events via a separate thread.
 * 
 * @author Ian Davies - 23 Oct 2020
 */
public class ControllerAdapter extends StateMachineController {

	/**
	 * @param observed The {@link StateMachineController}.
	 */
	public ControllerAdapter(StateMachineEngine<StateMachineController> observed) {
		super(observed);
	}

	/**
	 * Calls {@link StateMachineController#sendEvent(Event)} from the thread.
	 * 
	 * @param event The state machine event to post.
	 */
	public void sendEventThreaded(Event event) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				sendEvent(event);
			}
		});
		t.start();
	}

}
