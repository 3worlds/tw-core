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

package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.DataTrackerStatus;

/**
 * An ancestor class for data being sent from DataTrackers to DataReceivers
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public abstract class OutputData {
	
	private DataTrackerStatus status = null;
	private int senderId = -1;
	private int metadataType = -1;

	public OutputData(DataTrackerStatus status,int senderId,int metadataType) {
		super();
		this.senderId = senderId;
		this.status = status;
		this.metadataType = metadataType;
	}
	
	public DataTrackerStatus status() {
		return status;
	}

	public int sender() {
		return senderId;
	}

	/**
	 * 
	 * @return the metadata type matching this data record
	 */
	public int type() {
		return metadataType;
	}

}
