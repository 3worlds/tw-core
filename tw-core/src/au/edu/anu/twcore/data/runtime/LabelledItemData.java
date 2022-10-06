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

import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import au.edu.anu.twcore.ecosystem.runtime.tracking.AbstractDataTracker;
/**
 * Ancestor class for data messages where the variables originate from a
 * particular entity with a unique label (e.g. a SystemComponent instance).
 * 
 * @author Jacques Gignoux - 16 oct. 2019
 *
 */
public abstract class LabelledItemData extends TimeData {

	private DataLabel itemLabel = null;

	/**
	 * @param status       Current status of the simulator
	 * @param senderId     Simulator unique id.
	 * @param metaDataType {@link AbstractDataTracker}.
	 */
	public LabelledItemData(SimulatorStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}

	/**
	 * @return The item label.
	 */
	public DataLabel itemLabel() {
		return itemLabel;
	}

	/**
	 * Construct the item label from a var arg list.
	 * 
	 * @param labels list of label parts.
	 */
	public void setItemLabel(String... labels) {
		itemLabel = new DataLabel(labels);
	}

	/**
	 * Set the item label.
	 * 
	 * @param labels The item label.
	 */
	public void setItemLabel(DataLabel labels) {
		itemLabel = labels;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(" item=").append(itemLabel.toString());
		return sb.toString();
	}

}
