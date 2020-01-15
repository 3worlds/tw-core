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

/**
 * Ancestor class for data messages where the variables originate from a particular entity with
 * a unique label (e.g. a SystemComponent instance).
 * 
 * @author Jacques Gignoux - 16 oct. 2019
 *
 */
public abstract class LabelledItemData extends TimeData {
	
	private DataLabel itemLabel = null;

	public LabelledItemData(SimulatorStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}
	
	public DataLabel itemLabel() {
		return itemLabel;
	}
	
	public void setItemLabel(String... labels) {
		itemLabel = new DataLabel(labels);
	}

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
