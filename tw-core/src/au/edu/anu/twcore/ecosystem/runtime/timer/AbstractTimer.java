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
package au.edu.anu.twcore.ecosystem.runtime.timer;

import au.edu.anu.twcore.ecosystem.dynamics.TimeModel;
import au.edu.anu.twcore.ecosystem.runtime.Timer;

public abstract class AbstractTimer implements Timer {

	/** The last time at which this time model was activated */
	protected long lastTime = 0L;
	protected TimeModel timeModel;

	public AbstractTimer(TimeModel timeModel) {
		super();
		this.timeModel = timeModel;
	}

	/**
	 * Computes the next time at which this time model will require activation
	 *
	 * @param time
	 *            the current time
	 * @return the next time for this time model
	 */
	@Override
	public final long nextTime(long time) {
		long adt = dt(time);
		if (adt != Long.MAX_VALUE)
			return lastTime + adt;
		else
			return Long.MAX_VALUE;
	}

	@Override
	public void reset() {
		lastTime = 0L;
	}

}
