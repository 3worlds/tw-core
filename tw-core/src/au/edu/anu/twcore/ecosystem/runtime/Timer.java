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

import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.cnrs.iees.omhtk.Resettable;

/**
 * The runtime counterpart of TimeModel
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public interface Timer extends Resettable {

	/**
	 * The next time step according to this time model.
	 *
	 * @param time
	 *            the current time
	 * @return the next time step This has to be overriden in descendant time models
	 */
	public long dt(long time);

	/**
	 * Advances time for this time model, ie replaces lastTime with newTime in
	 * RegularTS and pops event from queue in irregularTS (Ian)
	 *
	 * @param newTime
	 */
	public void advanceTime(long newTime);

	public long nextTime(long t);

	/**
	 * Converts the 3worlds internal time tick in longs into user-defined time in doubles
	 * 
	 * @param t
	 * @return
	 */
	public double userTime(long t);
	
	/**
	 * Converts the user-defined time in doubles into the 3worlds internal time tick in longs
	 * 
	 * @param t
	 * @return
	 */
	public long twTime(double t);

	public TimeUnits timeUnit();

}
