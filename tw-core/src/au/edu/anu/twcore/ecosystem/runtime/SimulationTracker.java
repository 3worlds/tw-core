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
package au.edu.anu.twcore.ecosystem.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * An ancestor for data trackers which track a flow of timed data
 * 
 * @author Jacques Gignoux - 19 févr. 2021
 *
 */
public interface SimulationTracker {
	
	/**
	 * Perform any operations required at the beginning of a time step.
	 *  
	 * @param status the simulator status
	 * @param time the time step (in simulator TimeLine units)
	 */
	public void openTimeRecord(SimulatorStatus status, long time);
	
	/**
	 * Perform any operations required at the end of a time step
	 */
	public void closeTimeRecord();
	
	/**
	 * Perform any operations required to start recording. NOTE: must be called after
	 * {@code openTimeRecord(...)}.
	 */
	public default void openRecord() {
		// DO NOTHING
	}
	
	/**
	 * Perform any operations required to stop recording  (eg flush data). NOTE: must be called before
	 * {@code closeTimeRecord(...)}.
	 */
	public default void closeRecord() {
		// DO NOTHING
	}

}
