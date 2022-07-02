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

import fr.cnrs.iees.rvgrid.observer.Observer;
import au.edu.anu.twcore.ecosystem.runtime.simulator.*;
import au.edu.anu.twcore.ecosystem.runtime.simulator.SimulatorStates;

/**
 * An interface for objects able to receive data from a DataTracker. Descendants
 * of this class must call addRendezvous with the proper message type in their
 * constructor.
 * <p>
 * T: the type of data understood by this receiver
 * </p>
 * <p>
 * M: the type of metadata understood by this receiver
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public interface DataReceiver<T, M> extends Observer {

	/**
	 * Process implemented by all widgets to process the data.
	 * 
	 * @param data The data.
	 */
	public void onDataMessage(T data);

	/**
	 * Process metadata before a simulation starts. It is called when
	 * {@link Simulator} is in the {@link SimulatorStates#waiting waiting} state.
	 * Implementations may need to avoid reprocessing the metadata on re-running the
	 * simulation in the same session.
	 * 
	 * @param meta The metadata.
	 */
	public void onMetaDataMessage(M meta);

}
