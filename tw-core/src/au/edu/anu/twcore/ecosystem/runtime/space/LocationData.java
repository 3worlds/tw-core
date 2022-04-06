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
package au.edu.anu.twcore.ecosystem.runtime.space;

import fr.cnrs.iees.uit.space.Point;

/**
 * An interface for objects which are located in a space. Defines method to return the variables
 * that are used to compute spatial coordinates.
 *
 * @author J. Gignoux - 23 nov. 2020
 *
 */
public interface LocationData {

	/**
	 *
	 * @return the variables that are used to compute spatial coordinates
	 */
	public default double[] coordinates() {
		return null;
	}

	/**
	 *
	 * @param rank the rank of the coordinate (= the dimension) in the space
	 * @return the <em>rank<sup>th</sup></em> variable used to compute spatial coordinates
	 */
	public default double coordinate(int rank) {
		return Double.NaN;
	}

	/**
	 *
	 * @return the coordinates as a Point (immutable)
	 */
	public default Point asPoint() {
		return null;
	}

	/**
	 * Sets the incoming values as valid coordinates
	 *
	 * @param coord
	 */
	public default void setCoordinates(double[] coord) {
		// DO NOTHING
	}
}
