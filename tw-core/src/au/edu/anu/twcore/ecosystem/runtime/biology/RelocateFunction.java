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
package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.space.Location;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.uit.space.Box;

/**
 * Interface for user-defined ecological functions changing the location of a SystemComponent.
 *
 * @author Jacques Gignoux - 30 janv. 2020
 *
 */
public abstract class RelocateFunction extends TwFunctionAdapter {

	/**
	 * recompute the location of a system component from its current location and, if
	 * required, of another component (eg its parent or a pedator, etc.).
	 * Attention: ctLocation, other and otherLocation can be null!
	 *
	 * @param t 		current time
	 * @param dt		current time interval
	 * @param focal		system component to relocate
	 * @param ctLocation	current location of focal
	 * @param limits	space bounding box - new location must be inside this box
	 * @return new location of focal
	 */
	public abstract double[] relocate(double t,
		double dt,
		SystemComponent focal,
		Location ctLocation,
		SystemComponent other,
		Location otherLocation,
		Box limits);

}
