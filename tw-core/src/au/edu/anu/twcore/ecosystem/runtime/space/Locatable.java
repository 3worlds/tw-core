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

/**
 * For objects which can be located using LocationData
 *
 * @author J. Gignoux - 23 nov. 2020
 *
 * to Ian: Isnt that name ugly? I tried to find the worst one...
 * other possible names: Placeable? Localisable? Situable? Schtroumpfable?
 *
 */
public interface Locatable {

	/**
	 * A LocationData object stores values that are used to compute spatial coordinates.
	 * eg to access the coordinates on Locatable A, type A.locationData().coordinates();
	 *
	 * @return the LocationData object for this Locatable
	 */
	public LocationData locationData();

	/**
	 *
	 * @return true if the Locatable can change spatial coordinates over time, false otherwise
	 */
	public boolean mobile();

	/**
	 * For dynamic objects only, return the computed next value for locations.
	 * Default is to assume no dynamics
	 *
	 * @return
	 */
	public default LocationData nextLocationData() {
		return locationData();
	}

}
