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
package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;

/**
 * Old stuff refurbished in 2017. A stopping condition based on time - will stop
 * when a maximum value is reached The max may be "infinite" (max long)
 * 
 * @author gignoux - 7 mars 2017
 *
 */
public class SimpleStoppingCondition extends AbstractStoppingCondition {

	private long endTime = Long.MAX_VALUE;

	public static StoppingCondition defaultStoppingCondition() {
		return new SimpleStoppingCondition();
	}

	/**
	 * Constructor for the default stopping conditions, ie no stopping before
	 * Long.MaxVALUE
	 */
	private SimpleStoppingCondition() {
		super();
	}

	public SimpleStoppingCondition(long endTime) {
		super();
		this.endTime = endTime;
	}

	@Override
	public boolean stop() {
//		if (simulator().currentTime() > endTime)
		if (simulator().currentTime() >= endTime)
			return true;
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(time > ").append(endTime).append(')');
		return sb.toString();
	}

}
