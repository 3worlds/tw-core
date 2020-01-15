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
 * Data for 2D maps of a single variable
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class Output2DData extends LabelledItemData {

	private DataLabel zlabel;
	private Number[][] map;

	public Output2DData(SimulatorStatus status, 
			int senderId, 
			int metadataType,
			int nx,
			int ny) {
		super(status, senderId, metadataType);
		map = new Number[nx][ny];
	}
	
	public void addValue(int ix, int iy, Number value) {
		if ((ix>=0) && (ix<map.length) && (iy>=0) && (iy<map[0].length)) 
			map[ix][iy] = value;
	}
	
	public void setZLabel(DataLabel label) {
		zlabel = label;
	}

	public void setZLabel(String...labelParts) {
		zlabel = new DataLabel(labelParts);
	}

	
	public Number[][] map() {
		return map;
	}
	
	public DataLabel zLabel() {
		return zlabel;
	}

}
